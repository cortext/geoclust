import java.util.Iterator;
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

		//choix du seuil d'�loignement (pour le densit�) R
		double eps = Double.valueOf(exd.ui.tray.getText());//en m�tres
//		System.out.println("ray = "+R);

		MNumClust = new Vector<Integer>();// matrice associant � chaque identifiant de p�le un num�ro de cluster
		
		int numClust = 0;

		//on remplit MClust des identifiants des p�les sur la premi�re colonne et de z�ros sur la seconde colonne

		for (int k=0; k<v1; k++ )
		{
			MNumClust.add(0);
	    }//fin for k
	    
		//fin remplissage num clust avec des zeros
		System.out.println("Fin remplissage de numClust avec des zeros");

	    for (int i=0; i<v1; i++ )// i repr�sente un p�le
	    {
		    if(exd.Coord.get(i).get(3)==0.0){
		    	exd.Coord.get(i).set(3,1.0);
		    
		    	Vector<Integer> PtsVoisins = regionQuery(i, eps);
		    	
		    	if(MinPts>0 && PtsVoisins.size()>MinPts){
		    		numClust++;
		    		expandCluster(i,PtsVoisins,numClust,eps,MinPts);
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
	
	public Vector<Integer> regionQuery(int i,Double eps){
		Vector<Integer> indexVoisins = new Vector<Integer>();
		
		for (int j=0; j<v1; j++) //boucle pour compter le nombre de voisins proches de i. j est un voisin de i.
		{
			if(i!=j){//excluir lui-meme
				double lambdai = exd.Coord.get(i).get(1);
				double lambdaj = exd.Coord.get(j).get(1);
				double phii = exd.Coord.get(i).get(2);
				double phij = exd.Coord.get(j).get(2);
				double distancePoints = DistancePts(lambdai, phii, lambdaj, phij);
				
				if (distancePoints <= eps  )//ici, en excluant la distance nulle, on consid�re que i n'est pas voisin de lui-m�me. et si le point est visite
				{
					indexVoisins.add(j);
				}
			}
		}
		return indexVoisins;
	}
	
	public void expandCluster(int i,Vector<Integer>PtsVoisins,int numClust,double eps,int MinPts){
		MNumClust.set(i,numClust);
		//liste contient les index des voisins
		for(int x=0;x<=PtsVoisins.size()-1;x++){
			int icousin=PtsVoisins.get(x);
			//on verifie si le p cousin(i cousin) n est pas visite 
			if(exd.Coord.get(icousin).get(3)==0.0){
				//Change pour visite
				exd.Coord.get(icousin).set(3, 1.0);
				Vector<Integer> PtsVoisinsCousins = regionQuery(icousin, eps);
				if((PtsVoisinsCousins.size()-1)>=MinPts){
					PtsVoisins.addAll(PtsVoisinsCousins);
				}
			}
			if(MNumClust.get(icousin)==0){
				MNumClust.set(icousin,numClust);
			}
		}
	}
}
