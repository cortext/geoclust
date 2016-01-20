import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * @author revollom
 * @ve
 *
 */
public class FiltresChameleon
{
	public DBScan dbs;
	public Vector<Vector<Object>> Clusters = new Vector<Vector<Object>>();
	public Vector<Vector<Double>> ClustersCoords = new Vector<Vector<Double>>();
	public Vector<Vector<Double>> RI = new Vector<Vector<Double>>();
	public boolean formuleIsSelected=false;
//	public Vector<Vector<Integer>> NbPubPat = new Vector<Vector<Integer>>();
	Vector<Vector<Integer>> CPubPat;
	public Vector<Vector<Integer>> IDpbc ;
	public Vector<Vector<Integer>> clustersFusion = new Vector<Vector<Integer>>(); //clusters qui sont fusiones avec un outre cluster
	
	public Vector<Vector<Integer>> PubPat ;

	FiltresChameleon(DBScan dbscan,int iter)
	{
		dbs = dbscan;
		String seuil1,seuil2;
		boolean ri_rc_choix;
		int brevpat=0;
		if(dbs.exd.ui!=null){
			formuleIsSelected=dbs.exd.ui.checkbtn1.isSelected();
			brevpat=dbs.exd.ui.brevpat;
			seuil1=dbs.exd.ui.tseuil1.getText();
			seuil2=dbs.exd.ui.tseuil2.getText();
			ri_rc_choix= dbs.exd.ui.ri_pour_rc;
		}
		else{
			formuleIsSelected=dbs.exd.uiCsv.checkbtn1.isSelected();
			seuil1=dbs.exd.uiCsv.tseuil1.getText();
			seuil2=dbs.exd.uiCsv.tseuil2.getText();
			ri_rc_choix= dbs.exd.uiCsv.ri_pour_rc;
		}
		
		
		
		if(brevpat == 1||brevpat == 3){PubPat = dbs.exd.Pub; IDpbc =dbs.exd.IDpc;}//publications s�lectionn�es
		else{PubPat = (Vector<Vector<Integer>>) dbs.exd.Pat; IDpbc = dbs.exd.IDbc;}
		
		System.out.println("Debut filtres chameleon");

		CPubPat = getPublicationsOrPatentsByPoint();
		//double ri_rc_parametres=Double.valueOf(seuil1)*Math.pow(Double.valueOf(seuil2),1.5);
		double ri_rc_parametres=0.0;
		double ri_parametres=0.0;
		double rc_parametres=0.0;
		
		if(formuleIsSelected){
			ri_rc_parametres=Double.valueOf(seuil1);
		}
		else{
			ri_parametres=Double.valueOf(seuil1);
			rc_parametres=Double.valueOf(seuil2);
		}
		int nbfusions = 0;

		int a=0;
			int b=0;
			filters(dbs);
			getRIAndRC();
			for(int i=0; i<RI.size();i++)
			{
				double ri_rc=0.0;
				double ri=0.0;
				double rc=0.0;

				if(Clusters.size()>1)
				{
					
					if(ri_rc_choix){				//si on a choisi le faire pour la formule ri*rc^alpha 
						ri_rc=(double)RI.get(i).get(4); 	//(Double)RI.get(i).get(2)*(Math.pow((Double) RI.get(i).get(3),1.5));
					}
					else{
						ri=(double) RI.get(i).get(2);
					    rc=(double) RI.get(i).get(3);
					}
					if((ri_rc!=0.0 && ri_rc_parametres!=0.0 && ri_rc>ri_rc_parametres)||(ri!=0.0 && rc!=0.0 && ri>ri_parametres && rc>rc_parametres)){ 
						
						a =Integer.valueOf(RI.get(i).get(0).toString().substring(0,RI.get(i).get(0).toString().length()-2));
						b = Integer.valueOf(RI.get(i).get(1).toString().substring(0,RI.get(i).get(1).toString().length()-2));
						//TODO && !clustersFusion.contains(a) && !clustersFusion.contains(b) comparaison pour savoir un des clusters est deja fusionne pour ne pas faire la fusion
						if(a!=b){
							Vector<Integer> infoClusterFusion = new Vector<Integer>();
							if(!infoClusterFusion.isEmpty()){
								for(int aux=0;aux<clustersFusion.size();aux++){
									if(clustersFusion.get(aux).contains(a) || clustersFusion.get(aux).contains(b)){
										fusionCluster(a,b,clustersFusion.get(aux).get(2));
										break;				//les deux clusters et le nro dont ils vont etre asigne
									}
									else if(aux==clustersFusion.size()){
										fusionCluster(a,b,null);
										
									}
								}
							}
							else{
								fusionCluster(a,b,null); 	//b deviens cluster a
							}
							infoClusterFusion.add(a);		//ajoute les clusters fusionnes 
							infoClusterFusion.add(b);
							infoClusterFusion.add(a);
							clustersFusion.add(infoClusterFusion); //nbfusions iteration aussi pour la liste de clusters fusion
							RI.get(i).set(5,1.0);
							nbfusions++;
						}
						else{
							continue;
						}
					
					}
				}
			}
			System.out.println("nb fusions ap l'iteration numero "+(iter+1)+" : " + nbfusions);
		//}fin for TODO
		System.out.println("nb de clusters: " + Clusters.size());
		System.out.println("RI = "+RI);
		System.out.println("Nombre de fusions de clusters : "+(nbfusions));
	}
	
	void fusionCluster(Integer clusterA, Integer clusterB, Integer newCluster)
	{
		for (int k=0; k<dbs.MNumClust.size(); k++)
		{
			if(newCluster==null){
				if(dbs.MNumClust.get(k).equals(clusterB))
				{
					dbs.MNumClust.set(k,clusterA);
				}
			}
			else{
				if(dbs.MNumClust.get(k).equals(clusterB) || dbs.MNumClust.get(k).equals(clusterA))
				{
					dbs.MNumClust.set(k,newCluster);
				}
			}
		}
	}
	
	/**
	 * Methode pour trouver les publications ou brevets associees a chaque id couple
	 * @param dbscan resultats de clustering dbscan
	 */
	void filters(DBScan dbscan)
	{
		Vector<Integer> num = new Vector<Integer>();		
		int nb=0;
		for(int i=0; i<dbs.MNumClust.size(); i++)
		{
			int auxa=0;
			int auxb=0;
			if(dbs.MNumClust.get(i)!=0){   
				boolean present = false;
				if(nb!=0)
				{	
					for(int k=0; k<nb; k++)
					{
						auxa=dbs.MNumClust.get(i);
						auxb=num.get(k);
						if(auxa==auxb)
						{
							present=true;
							Clusters.get(k).add((int) IDpbc.get(i).get(0));
							ClustersCoords.get(k).add((double) IDpbc.get(i).get(1));
							ClustersCoords.get(k).add((double) dbs.exd.Coord.get(i).get(1));
							ClustersCoords.get(k).add((double) dbs.exd.Coord.get(i).get(2));
							ClustersCoords.get(k).add((double) dbs.exd.Coord.get(i).get(4));
							break;//sors de k
						}
					}
				}
				if(present==false || nb==0)  
				{
					Clusters.add(new Vector<Object>());
					Clusters.get(nb).add(dbs.MNumClust.get(i));
					Clusters.get(nb).add(IDpbc.get(i).get(0));
					num.add(dbs.MNumClust.get(i));
					ClustersCoords.add(new Vector<Double>());
					ClustersCoords.get(nb).add((double)dbs.MNumClust.get(i));
					ClustersCoords.get(nb).add((double) IDpbc.get(i).get(1));
					ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(1));
					ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(2));
					ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(4));
					nb++;
				}
			}
		}
	}//fin construction des clusters

	/**
	 * Methode pour calculer les variables RI et RC 
	 * @RI Relative internconnectivity = Nombre collaboration interclusters / Nombre collaboration intracluster1 + Nombre collaboration intracluster2
	 * 
	 * @RC Relative closness = 
	 */
	public void getRIAndRC(){
		Vector<Vector<Double>> listeBarycentreParCluster=getCentroidByCluster(ClustersCoords);
		System.out.println("Fin composition des clusters");
		int cluster_visite=1;
		for(int i=0; i<Clusters.size()-1; i++)
		{
			Vector<Double>auxCalculNbCollabAndEdge = new Vector<Double>();
			 auxCalculNbCollabAndEdge.addAll(getNumberOfCollaborations(i,i));
			 
			double nbCollabIntraCluster1=0.0;
			//Se realiza por fuera porque con el mismo se va a tener siempre
			nbCollabIntraCluster1 = auxCalculNbCollabAndEdge.get(0);
			double nbEdgeIntraCluster1 = auxCalculNbCollabAndEdge.get(1);
			//on valide que si le denominateur est 0 il faut mettre 0.0 a la place de NaN;
			double closnessIntraCluster1= (nbCollabIntraCluster1/nbEdgeIntraCluster1 == Double.NaN ? 0.0 : nbCollabIntraCluster1/nbEdgeIntraCluster1) ;
			
			System.out.println("Nombre de clusters traites : "+i); // affiche l'avancement des comparaisons rirc
			
			for(int j = cluster_visite; j<Clusters.size(); j++)
			{
				
				double distance=dbs.DistancePts(listeBarycentreParCluster.get(i).get(1), listeBarycentreParCluster.get(i).get(2), listeBarycentreParCluster.get(j).get(1), listeBarycentreParCluster.get(j).get(2))/1000;
				
				if(i!=j && distance<100){//distance maximale pour fusioner les clusters
					auxCalculNbCollabAndEdge = new Vector<Double>();// on cree les listes tmp pour garder les collabs et nb des edges entre les clusters (cluster 1 avec 1, cluster 2 avec 2 et cluster 1 avec 2 )
					auxCalculNbCollabAndEdge.addAll(getNumberOfCollaborations(j,j));
					double nbCollabIntraCluster2 = auxCalculNbCollabAndEdge.get(0);
					double nbEdgeIntraCluster2 = auxCalculNbCollabAndEdge.get(1);
					double closnessIntraCluster2= (nbCollabIntraCluster2/nbEdgeIntraCluster2 == Double.NaN ? 0.0 : nbCollabIntraCluster2/nbEdgeIntraCluster2) ;
					
					auxCalculNbCollabAndEdge = new Vector<Double>();
					auxCalculNbCollabAndEdge.addAll(getNumberOfCollaborations(i,j));
					double nbCollabInterClusters1And2 = auxCalculNbCollabAndEdge.get(0);
					double nbEdgeInterClusters1And2 = auxCalculNbCollabAndEdge.get(1);
					double closnessInterClusters1And2= (nbCollabInterClusters1And2/nbEdgeInterClusters1And2 == Double.NaN ? 0.0 : nbCollabInterClusters1And2/nbEdgeInterClusters1And2);
					
					String clust_getI=Clusters.get(i).get(0).toString();
					String clust_getJ=Clusters.get(j).get(0).toString();
					
					RI.add(new Vector<Double>());
					RI.get(RI.size()-1).add(Double.parseDouble(clust_getI));
				    RI.get(RI.size()-1).add(Double.parseDouble(clust_getJ));
				    
				    //calcul de RI
					if(nbCollabIntraCluster1+nbCollabIntraCluster2!=0)
					{	
						RI.get(RI.size()-1).add((double)(nbCollabInterClusters1And2/((nbCollabIntraCluster1+nbCollabIntraCluster2)/2)));
					}else
					{
						RI.get(RI.size()-1).add(0.0);
					}
					
					double t1 = Clusters.get(i).size()-1;
					double t2 = Clusters.get(j).size()-1;
					//Calcul de RC
					if((((t1/(t1+t2))*closnessIntraCluster1)+((t2/(t1+t2))*closnessIntraCluster2)
							)!=0)
					{
						RI.get(RI.size()-1).add((double) (closnessInterClusters1And2 /
								(((t1/(t1+t2))*closnessIntraCluster1) + ((t2/(t1+t2))*closnessIntraCluster2))
							));
					}else
					{
						RI.get(RI.size()-1).add(0.0);
					}
					
					RI.get(RI.size()-1).add((Double)RI.get(RI.size()-1).get(2)*(Math.pow((Double) RI.get(RI.size()-1).get(3),1.5)));//ajout de RI*RC dans le vecteur
					RI.get(RI.size()-1).add(0.0);
				}
			}
			cluster_visite++;
		}// on compare les RI et RC des clusters. Si ces valeurs sont tr�s �lev�es, on fusionne les clusters. Sinon, on les laisse tels quels.
		System.out.println("Ri sin ordenar"+RI);
		Collections.sort(RI, new MyComparator(4)); //Critere pour lequel on veut trier (par le resultat RI*RC)  
		System.out.println("Ri ordenado"+RI);
		System.out.println("Fin filtres chameleon");
	}
		

	Vector<Double> getNumberOfCollaborations(int i,int j)
	{
		Vector<Double> nbCollabAndEdge = new Vector<Double>();
		double nbCollabTotal=0;
		double nbCollabParPoint=0;
		int edge=0;
		Vector<Vector<String>> pointVisite = new Vector<Vector<String>>();
	    pointVisite.add(new Vector<String>());
	    pointVisite.add(new Vector<String>());
		for(int k=1; k<(int)Clusters.get(i).size(); k++)//attention, z�ro est le num�ro du cluster
		{
			int a = (Integer) Clusters.get(i).get(k);
			for(int l=1; l<(int)Clusters.get(j).size(); l++)//attention, z�ro est le num�ro du p�le
			{
				int b = (Integer) Clusters.get(j).get(l);
				if(i!=j || (pointVisite.isEmpty() || (!pointVisite.get(0).contains(a+""+b) && !pointVisite.get(0).contains(b+""+a)) ) ){
					for(int m=1; m<(int)CPubPat.get(a).size(); m++)//attention trier la lista en la position a y el de la position b, z�ro est le num�ro du p�le 
					{ 
						if(CPubPat.get(b).contains(CPubPat.get(a).get(m))){
							search:
							for(int n=1; n<(int)CPubPat.get(b).size(); n++)
							{
								if(CPubPat.get(a).get(m)>=CPubPat.get(b).get(n)){
									if(((i==j && ((a!=b)||(a==b && m!=n))) || (i!=j)) && CPubPat.get(a).get(m).equals(CPubPat.get(b).get(n)))//si les deux p�les ont un m�me id de publication
									{
										nbCollabParPoint ++;
									}
								}
								else{
									break search;
								}
							}
						}
					}
					if(i==j){
						if(k==l)//si les points sont differents
						{
							nbCollabParPoint=nbCollabParPoint/2;
						}
						pointVisite.get(0).add(a+""+b);
						pointVisite.get(1).add(String.valueOf(nbCollabParPoint));
					}
				}

				if(nbCollabParPoint!=0){//cela implique qu aille au moins une collab pour l ajouter a la collab totale
					edge++;
					nbCollabTotal+=nbCollabParPoint;
					nbCollabParPoint=0;
				}
			}
		}// on compte le nombre de publications/brevets n�s de collaborations entre les p�les du cluster i et ceux du cluster j.
		nbCollabAndEdge.add(nbCollabTotal);
		nbCollabAndEdge.add((double) edge);
		return nbCollabAndEdge; 
	}
	
	
	/**
	 * Methode pour faire le calcul du barycentre pour chaque cluster
	 * pour  x = Ʃ xi*poidi/Ʃ ni et pour y = Ʃ yi*poidi/Ʃ ni
	 * @author Michel Revollo
	 * 
	 * @param ClustersCoords coord de points
	 * 
	 * @return indice de cluster avec x,y centroides
	 */
	public Vector<Vector<Double>> getCentroidByCluster(Vector<Vector<Double>> Coords){
		Vector<Vector<Double>> centroidByCluster = new Vector<Vector<Double>>();
		for(int i=0;i<Coords.size();i++){
			double x_moyenne=0.0;
			double y_moyenne=0.0;
			double poid_moyenne = 0.0;
			int cont=0;
			int size=Coords.get(i).size();
			for (int j = 2; j < size; j=j+4) {//demarre a un car la position 0 indique le numero du cluster
				x_moyenne+=(Coords.get(i).get(j) * Coords.get(i).get(j+2));
				y_moyenne+=(Coords.get(i).get(j+1) * Coords.get(i).get(j+2));
				poid_moyenne+=Coords.get(i).get(j+2);
				cont++;//CoordPoint.
			}
			x_moyenne=x_moyenne/poid_moyenne;
			y_moyenne=y_moyenne/poid_moyenne;
			centroidByCluster.add(new Vector<Double>());
			centroidByCluster.get(i).add(Coords.get(i).get(0));
			centroidByCluster.get(i).add(x_moyenne);
			centroidByCluster.get(i).add(y_moyenne);
			
		}
		return centroidByCluster;
	}


	/**
	 * Methode pour obtenir pour chaque IDc les publications lies a cet point meme si 
	 * la publication est deux ou plusieurs fois
	 * 
	 * @return Vecteur de vecteurs avec la position 0 du vecteur a linterieur egal IDc et les autres positions les idP
	 * du meme point
	 */
	Vector<Vector<Integer>> getPublicationsOrPatentsByPoint()
	{
		Vector<Vector<Integer>> CP = new Vector<Vector<Integer>>();
		for(int i=0; i<dbs.MNumClust.size(); i++)
		{
			CP.add(new Vector<Integer>());
			CP.get(i).add(IDpbc.get(i).get(1));
		}
		
		for(int i=0; i<PubPat.size(); i++)//0=IDC; 1=IDP
		{
			for(int j=0; j<dbs.MNumClust.size();j++)
			{
				if((int)PubPat.get(i).get(0) != (int) CP.get(j).get(0))
				{continue;}else
				{
					CP.get(j).add((int)PubPat.get(i).get(1));
					break;
				}
			}
		}

		System.out.println("Taille CP = "+CP.size());
		System.out.println("Fin reorganisation pub");
		return CP;// au final, CP contient, pour chaque pole (IDC), toutes ses publications par leur IDP.
	}
	
	public Vector<Vector<Double>> getClusterWithCoordsAfterFusion(Vector<Vector<Double>> custerCoords){
		Vector<Vector<Double>> clusterCoordsAfterFusion = new Vector<Vector<Double>>();
		
		
		
		return clusterCoordsAfterFusion;
	}
	
	/**
	 * Class implemente pour possibiliter la comparaison des positions specifiques dans deux vecteurs
	 * 
	 * @author revollom
	 *
	 */
	public class MyComparator implements Comparator<Vector<?>> {
		  private int columnIndex = 0;
		  public MyComparator(int columnIndex) {this.columnIndex = columnIndex;}

		@Override
		public int compare(Vector<?> arg0, Vector<?> arg1) {
			// TODO Auto-generated method stub
			return ((Double) arg1.get(columnIndex)).compareTo((Double)arg0.get(columnIndex));
		}
	} 

}
