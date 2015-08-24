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
		System.out.println("v1 = "+v1);
		int MinPts, MinPds;
		double maxDistanceBetweenTwoPoints;
		if(exd.ui!=null){
			MinPts = Integer.valueOf(exd.ui.tnbpts.getText()); //choix minPts
			//System.out.println("min pts = "+MinPts);
			MinPds = Integer.valueOf(exd.ui.tpoids.getText()); //quantite des publications ou brevets dans un cluster
			maxDistanceBetweenTwoPoints = Double.valueOf(exd.ui.tray.getText()); //choix du seuil d'aloignement (pour le densite) R
			
		}
		else{
			MinPts = Integer.valueOf(exd.uiCsv.tnbpts.getText()); //choix minPts
			//System.out.println("min pts = "+MinPts);
			MinPds = Integer.valueOf(exd.uiCsv.tpoids.getText()); //quantite des publications ou brevets dans un cluster
			maxDistanceBetweenTwoPoints = Double.valueOf(exd.uiCsv.tray.getText()); //choix du seuil d'aloignement (pour le densite) R
		}
		MNumClust = new Vector<Integer>();// matrice associant a chaque identifiant de pole un numero de cluster
		int numClust = 1;
		
		//MClust est rempli avec les  identifiants des points (id couple x,y) sur la premiere colonne et de zeros sur la deuxieme colonne
		for (int k=0; k<v1; k++ )
		{ 
			MNumClust.add(0);
	    }
		System.out.println("Fin remplissage de numClust avec des zeros");

	    for (int i=0; i<v1; i++ )// i represente un pole (couple x,y)
	    {
		    if(exd.Coord.get(i).get(3)==0.0){
		    	exd.Coord.get(i).set(3,1.0);//dans la position 3 s indique si le pole est visite ou pas
		    
		    	Vector<Object> PtsVoisinsPoids = regionQuery(i, maxDistanceBetweenTwoPoints);
		    	Vector<Integer> PtsVoisins = (Vector<Integer>) PtsVoisinsPoids.get(0);
		    	int poids_cluster = Integer.valueOf(PtsVoisinsPoids.get(1).toString());
		    	if(poids_cluster>=MinPds){//
		    		expandCluster(i,PtsVoisins,numClust,maxDistanceBetweenTwoPoints,MinPts,MinPds);//fait appel a la methode pour trouver les voisins du cluster
		    		numClust++;
		    	}
		    }
	    }//fin boucle sur i
	    System.out.println("Fin DBScan quantite clusters "+numClust);
	}	
	
	/**
	 * Methode pour calculer la distance entre deux coordonnees geographiques
	 * 
	 * @author Alba Fuga, Lucie Mourgues ENSG
	 * @param lambdai 
	 * @param phii 
	 * @param lambdaj 
	 * @param phij
	 * @return distance en metres
	 */
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
	
	/**
	 * Fonction pour trouver les points voisines d un point specifique, on fait l anlyse des voisins en obtenant 
	 * le poids si le critere de distance minimale est remplie
	 * @param pole i (id de couple x,y)
	 * @param eps est la distance maximale entre deux points definie dans les parametres 
	 * @return liste avec les index des voisins
	 * @author Michel Revollo
	 */
	public Vector<Object> regionQuery(int i,Double eps){
		Vector<Integer> indexVoisins = new Vector<Integer>();
		Vector<Object> indexVoisinsPoids= new Vector<Object>();
		int poids = 0;
		int poids_i=(int)(double)exd.Coord.get(i).get(4);
		poids += poids_i;
		for (int j=0; j<v1; j++) //il faut compter le nombre de voisins proches de i.
		{
			if(i!=j && MNumClust.get(j)==0){//excluire lui-meme
				double lambdai = exd.Coord.get(i).get(1);
				double lambdaj = exd.Coord.get(j).get(1);
				double phii = exd.Coord.get(i).get(2);
				double phij = exd.Coord.get(j).get(2);
				double distancePoints = DistancePts(lambdai, phii, lambdaj, phij);
				int poids_j=(int)(double)exd.Coord.get(j).get(4);
				
				if (distancePoints <= eps  )		//en excluant la distance nulle, on considere que i n'est pas voisin de lui-meme. et si le point est visite
				{
					indexVoisins.add(j);
					poids=poids_j+poids; 			// on additione le poids j au poids calculee jusqu'au present
				}
			}
		}
		indexVoisinsPoids.add(indexVoisins);		//ajout les voisins dans une liste pour chaque couple x,y
		indexVoisinsPoids.add(poids);
		return indexVoisinsPoids;
	}
	
	/**
	 * Methode pour ajouter les voisins de i dans le cluster numClust
	 * @param i point a evaluer pour ajouter ses voisins
	 * @param PtsVoisins Voisins de i
	 * @param numClust le cluster auquel appartient i
	 * @param eps distance maximale defini dans les parametres
	 * @param MinPts Nombre minimal de points
	 * @param MinPds Nombre minimal de poids acceptee pour creer le cluster
	 */
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
}
