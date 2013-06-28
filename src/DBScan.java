import java.util.Vector;


public class DBScan 
{
	public Vector<Integer> MNumClust;
	public ExtractionDonnees exd;
	public int v1;
	
	DBScan(ExtractionDonnees ex)
	{
		exd = ex;
		v1 = exd.Coord.size();
//		v1 = 10000;
		System.out.println("v1 = "+v1);
		//choix minPts
		int MinPts = Integer.valueOf(exd.ui.tnbpts.getText());
//		System.out.println("min pts = "+MinPts);
		int MinPds = Integer.valueOf(exd.ui.tpoids.getText());
		//choix du seuil d'aloignement (pour le densite) R
		double eps = Double.valueOf(exd.ui.tray.getText());//en metres
//		System.out.println("ray = "+R);

		MNumClust = new Vector<Integer>();// matrice associant a chaque identifiant de pole un numero de cluster
		
		int numClust = 0;

		//on remplit MClust des identifiants des poles sur la premiere colonne et de zeros sur la seconde colonne

		for (int k=0; k<v1; k++ )
		{
			MNumClust.add(0);
	    }//fin for k
	    
		//fin remplissage num clust avec des zeros
		System.out.println("Fin remplissage de numClust avec des zeros");

	    for (int i=0; i<v1; i++ )// i represente un pole
	    {
		    if(exd.Coord.get(i).get(3)==0.0){
		    	exd.Coord.get(i).set(3,1.0);
		    
		    	Vector<Object> PtsVoisinsPoids = regionQuery(i, eps);
		    	Vector<Integer> PtsVoisins = (Vector<Integer>) PtsVoisinsPoids.get(0);
		    	int poids_cluster = Integer.valueOf(PtsVoisinsPoids.get(1).toString());
		    	//if(MinPts>0 && PtsVoisins.size()>MinPts && poids_cluster>=MinPds){
		    	if(poids_cluster>=MinPds){
		    		numClust++;
		    		expandCluster(i,PtsVoisins,numClust,eps,MinPts,MinPds);
		    	}
		    }
	    }
            //fin boucle sur i
	    System.out.println("Fin DBScan quantite clusters "+numClust);
	}	
	
	public double DistancePts(double lambdai,double phii,double lambdaj,double phij)
	{
		double a = 6378137.0; //en m
		double b = 6356752.314; // en m
	    //il faut transformer les degres en radians
	    lambdai = lambdai*Math.PI/180;
	    lambdaj = lambdaj*Math.PI/180;
	    phii = phii*Math.PI/180;
	    phij = phij*Math.PI/180;

	    double wi = (b*b*Math.tan(phii))/(a*a);
	    double wj = (b*b*Math.tan(phij))/(a*a);

	    double xi = (a*Math.cos(lambdai)*Math.cos(phii))/wi;
	    double xj = (a*Math.cos(lambdaj)*Math.cos(phij))/wj;
	    double yi = (a*Math.sin(lambdai)*Math.cos(phii))/wi;
	    double yj = (a*Math.sin(lambdaj)*Math.cos(phij))/wj;
	    double zi = (b*b*Math.sin(phii))/(a*wi);
	    double zj = (b*b*Math.sin(phij))/(a*wj);
	    double x = xi-xj;
	    double y = yi-yj;
	    double z = zi - zj;

	    double r = Math.sqrt(x*x+y*y+z*z);

	    return r;
	}
	/*Trouve les points voisines d un point specifique, on fait lanlyse des voisins en obtenant le poids si le critere de distance minimale est remplie */
	public Vector<Object> regionQuery(int i,Double eps){
		Vector<Integer> indexVoisins = new Vector<Integer>();
		Vector<Object> indexVoisinsPoids= new Vector<Object>();
		int poids = 0;
		int poids_i=(int)(double)exd.Coord.get(i).get(4);
		poids += poids_i;
		for (int j=0; j<v1; j++) //boucle pour compter le nombre de voisins proches de i. j est un voisin de i.
		{
			if(i!=j){//excluir lui-meme
				double lambdai = exd.Coord.get(i).get(1);
				double lambdaj = exd.Coord.get(j).get(1);
				double phii = exd.Coord.get(i).get(2);
				double phij = exd.Coord.get(j).get(2);
				double distancePoints = DistancePts(lambdai, phii, lambdaj, phij);
				int poids_j=(int)(double)exd.Coord.get(j).get(4);
				
				if (distancePoints <= eps  )//ici, en excluant la distance nulle, on considere que i n'est pas voisin de lui-meme. et si le point est visite
				{
					indexVoisins.add(j);
					poids=poids_i+poids_j+poids;//modifier le poids, il faut additioner juste le poids_j + poids
				}
			}
		}
		indexVoisinsPoids.add(indexVoisins);
		indexVoisinsPoids.add(poids);
		return indexVoisinsPoids;
	}
	
	public void expandCluster(int i,Vector<Integer>PtsVoisins,int numClust,double eps,int MinPts,int MinPds){
		MNumClust.set(i,numClust);
		//liste contient les index des voisins
		if(PtsVoisins.size()!=0){
			for(int x=0;x<=PtsVoisins.size()-1;x++){
				int icousin=PtsVoisins.get(x);
				//on verifie si le p cousin(i cousin) n est pas visite 
				if(exd.Coord.get(icousin).get(3)==0.0){
					//Change pour visite
					exd.Coord.get(icousin).set(3, 1.0);
					Vector<Object> PtsVoisinsCousinsPoids = regionQuery(icousin, eps);
					Vector<Integer> PtsVoisinsCousins = (Vector<Integer>) PtsVoisinsCousinsPoids.get(0);
					int poids_cluster = Integer.valueOf(PtsVoisinsCousinsPoids.get(1).toString());
					
					//if((PtsVoisinsCousins.size()-1)>=MinPts && poids_cluster>=MinPds){
					if(poids_cluster>=MinPds){
						PtsVoisins.addAll(PtsVoisinsCousins);
					}
				}
				if(MNumClust.get(icousin)==0){
					MNumClust.set(icousin,numClust);
				}
			}
		}
	}
	/**
	 * Methode pour creer les clusters a partir des points qui ont le poid au dessus du parametre 
	 * et qui sont pas dans un cluster parce que ils ne remplissent pas la condition des voisines (MinPoints) 
	 * 
	 * @param i = point en question, numClust = le numero du nouveau cluster
	 * @author Michel Revollo
	 * */
	public void createClusterCities(int i,int numClust){ 
	}
}
