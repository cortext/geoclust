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

		//choix du seuil d'éloignement (pour le densité) R
		double R = Double.valueOf(exd.ui.tray.getText());//en mètres
//		System.out.println("ray = "+R);

		MNumClust = new Vector<Integer>();// matrice associant à chaque identifiant de pôle un numéro de cluster
		
		int numClust = 1;

		//on remplit MClust des identifiants des pôles sur la première colonne et de zéros sur la seconde colonne

		for (int k=0; k<v1; k++ )
		{
			MNumClust.add(0);
	    }//fin for k
	    
		//fin remplissage num clust avec des zeros
		System.out.println("Fin remplissage de numClust avec des zeros");

	    for (int i=0; i<v1; i++ )// i représente un pôle
	    {
		    int compt = -1;
            for (int j=0; j<v1; j++) //boucle pour compter le nombre de voisins proches de i. j est un voisin de i.
            {
                double lambdai = exd.Coord.get(i).get(1);
                double lambdaj = exd.Coord.get(j).get(1);
                double phii = exd.Coord.get(i).get(2);
                double phij = exd.Coord.get(j).get(2);
                double r = DistancePts(lambdai, phii, lambdaj, phij);

                if (r <= R)//ici, en excluant la distance nulle, on considère que i n'est pas voisin de lui-même.
                {
                    compt++;
                }
            }
    		
            if ((compt < MinPts)||(MinPts<0))//ici, pour réaliser un cluster, il faut au moins MinPts points autour de i dans un péripètre circulaire de rayon R.
            {continue;}
            
            if(MNumClust.get(i)==0)
            {
	        	MNumClust.set(i,numClust);
	            for (int j=0; j<v1; j++ )//boucle pour marquer les voisins de i proches s'ils sont assez nombreux
	            {
	                double lambdai = exd.Coord.get(i).get(1);
	                double lambdaj = exd.Coord.get(j).get(1);
	                double phii = exd.Coord.get(i).get(2);
	                double phij = exd.Coord.get(j).get(2);
	                double r = DistancePts(lambdai, phii, lambdaj, phij);http://www.pascal-plee.com/Documentations/DocNonDisponible.jpg
	
	                if (MNumClust.get(j) == 0)//est-ce que j est un voisin de i qui appartient déjà à un cluster ? cas où il ne l'est pas
	                {
	                    if ((i!=j)&&(r <= R))//est-ce que i et j sont assez proches ?
	                    {
	                        MNumClust.set(j,numClust);
	                    }
	                }else // si j est déjà dans un cluster
	                {
	                    if (i == j)
	                    {continue;}
	                    if (r <= R)//et si i et j sont assez proches
	                    {
	                        MNumClust.set(i,MNumClust.get(j));//alors on affecte à i le numéro du cluster de j
	                    }//fin test proximité i et j
	                }//fin else
	            }//fin boucle sur j de marquage des voisins de i
		        numClust++;
            }else
            {
                for (int j=0; j<v1; j++ )//boucle pour marquer les voisins de i proches s'ils sont assez nombreux
	            {
	                double lambdai = exd.Coord.get(i).get(1);
	                double lambdaj = exd.Coord.get(j).get(1);
	                double phii = exd.Coord.get(i).get(2);
	                double phij = exd.Coord.get(j).get(2);
	                double r = DistancePts(lambdai, phii, lambdaj, phij);
	
	                if (MNumClust.get(j) == 0)//est-ce que j est un voisin de i qui appartient déjà à un cluster ? cas où il ne l'est pas
	                {
	                    if ((i!=j)&&(r <= R))//est-ce que i et j sont assez proches ?
	                    {
	                        MNumClust.set(j,MNumClust.get(i));
	                    }
	                }else // si j est déjà dans un cluster
	                {
	                    if (i == j || r > R)
	                    {continue;}
	                    
	                    MNumClust.set(j,MNumClust.get(i));//alors on affecte à i le numéro du cluster de j
	                    
	                    for(int k=0; k<v1; k++)
	                    {
	                    	if(MNumClust.get(k)==MNumClust.get(j))
	                    	{
	                    		MNumClust.set(k, MNumClust.get(j));
	                    	}
	                    }
	                }//fin else
	            }//fin boucle sur j de marquage des voisins de i        	
            }
	    }//fin boucle sur i
	    System.out.println("Fin DBScan");
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
}
