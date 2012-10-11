import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class FiltresChameleon
{
	public DBScan dbs;
	public Vector<Vector<Object>> Clusters = new Vector<Vector<Object>>();
	public Vector<Vector<Double>> ClustersCoords = new Vector<Vector<Double>>();
	public Vector<Vector<Object>> ClustersID = new Vector<Vector<Object>>();
	public Vector<Vector<Double>> RI = new Vector<Vector<Double>>();
	public boolean formuleIsSelected=false;
//	public Vector<Vector<Integer>> NbPubPat = new Vector<Vector<Integer>>();
	Vector<Vector<Integer>> CPubPat;
	public Vector<Vector<Integer>> IDpbc ;
	
	public Vector<Vector<Integer>> PubPat ;

	FiltresChameleon(DBScan dbscan)
	{
		dbs = dbscan;
		formuleIsSelected=dbs.exd.ui.checkbtn1.isSelected();
		
		if(dbs.exd.ui.brevpat == 1||dbs.exd.ui.brevpat == 3){PubPat = dbs.exd.Pub; IDpbc =dbs.exd.IDpc;}//publications sélectionnées
		else{PubPat = (Vector<Vector<Integer>>) dbs.exd.Pat; IDpbc = dbs.exd.IDbc;}
		
		System.out.println("Début filtres chameleon");

		CPubPat = ClassePubPat();
		//double ri_rc_parametres=Double.valueOf(dbs.exd.ui.tseuil1.getText())*Math.pow(Double.valueOf(dbs.exd.ui.tseuil2.getText()),1.5);
		double ri_rc_parametres=0.0;
		double ri_parametres=0.0;
		double rc_parametres=0.0;
		
		if(formuleIsSelected){
			ri_rc_parametres=Double.valueOf(dbs.exd.ui.tseuil1.getText());
		}
		else{
			ri_parametres=Double.valueOf(dbs.exd.ui.tseuil1.getText());
			rc_parametres=Double.valueOf(dbs.exd.ui.tseuil2.getText());
		}
		int nbfusions = 0;
		for(int iter=0; iter<Integer.valueOf(dbs.exd.ui.titer.getText()); iter++)
		{
			Filtres(dbs);
			for(int i=0; i<RI.size();i++)
			{
				double ri_rc=0.0;
				double ri=0.0;
				double rc=0.0;
				//TODO le text dit que ne est pas un OR sinon un AND  
				if(Clusters.size()>1)
				{
					//si on a choisi le faire pour la formule ri*rc^alpha 
					if(dbs.exd.ui.ri_pour_rc){
						ri_rc=(Double)RI.get(i).get(2)*(Math.pow((Double) RI.get(i).get(3),1.5));
					}
					else{
						ri=(double) RI.get(i).get(2);
					    rc=(double) RI.get(i).get(3);
					}
					if((ri_rc!=0.0 && ri_rc_parametres!=0.0 && ri_rc>ri_rc_parametres)||(ri!=0.0 && rc!=0.0 && ri>ri_parametres && rc>rc_parametres)){ 
					//if((ri_rc!=0.0 && ri_rc>ri_rc_parametres)){if anterior
						
						Integer a =Integer.valueOf(RI.get(i).get(0).toString().substring(0,RI.get(i).get(0).toString().length()-2));
						Integer b = Integer.valueOf(RI.get(i).get(1).toString().substring(0,RI.get(i).get(1).toString().length()-2));
						//TODO 
						if(a!=b){
						Fusionne(a,b);
						nbfusions++;
						}
						else{
							continue;
						}
					
					}
				}
			}
			/*
			if(dbs.exd.ui.brevpat == 3)
			{
				PubPat = (Vector<Vector<Integer>>) dbs.exd.Pat;
				
				CPubPat = ClassePubPat();
				Filtres(dbs);
				
				for(int i=0; i<RI.size();i++)
				{
					if(Clusters.size()>1&&(((Double) RI.get(i).get(2)> Double.valueOf(dbs.exd.ui.tseuil1.getText()) )&& ((Double) RC.get(i).get(2)>Double.valueOf(dbs.exd.ui.tseuil2.getText()) )))
					{
						Integer a =Integer.valueOf(RI.get(i).get(0).toString().substring(0,RI.get(i).get(0).toString().length()-2));
						Integer b = Integer.valueOf(RI.get(i).get(1).toString().substring(0,RI.get(i).get(1).toString().length()-2));
						//TODO
						if(a!=b){
							Fusionne(a,b);
							nbfusions++;
						}
						else{
							continue;
						}
					}
				}	
			}
			*/
			System.out.println("nb fusions ap l'itération numéro "+(iter+1)+" : " + nbfusions);
		}
		//TODO enlever Filtres(dbs) ;
		System.out.println("nb de clusters: " + Clusters.size());
//		System.out.println("Nb PubPat = "+ NbPubPat);
		System.out.println("RI = "+RI);
//		System.out.println("CPubPat = "+CPubPat);
//		System.out.println("IDpôles = "+IDpbc);
		System.out.println("Nombre de fusions de clusters : "+(nbfusions));
	}
	
	void Fusionne(Integer i, Integer j)
	{
		for (int k=0; k<dbs.MNumClust.size(); k++)
		{
			if((Integer)dbs.MNumClust.get(k) == i)
			{
				dbs.MNumClust.set(k,j);
			}
		}
	}
	void Filtres(DBScan dbscan)
	{
	
		Vector<Integer> num = new Vector<Integer>();		

		int nb=0;

		for(int i=0; i<dbs.MNumClust.size(); i++)
		{
			if(dbs.MNumClust.get(i)!=0)
			{
				if(nb!=0)
				{	boolean present = false;
					for(int k=0; k<nb; k++)
					{
						if(dbs.MNumClust.get(i)==num.get(k))
					 	{
							present=true;
							Clusters.get(k).add((int) IDpbc.get(i).get(0));
							ClustersID.get(k).add((int) IDpbc.get(i).get(1));
							ClustersCoords.get(k).add((double) IDpbc.get(i).get(1));
							ClustersCoords.get(k).add((double) dbs.exd.Coord.get(i).get(1));
							ClustersCoords.get(k).add((double) dbs.exd.Coord.get(i).get(2));
							break;//sors de k
						}
					}
					if(present==false)
					{
						Clusters.add(new Vector<Object>());
						Clusters.get(nb).add(dbs.MNumClust.get(i));
						Clusters.get(nb).add(IDpbc.get(i).get(0));
						
						num.add(dbs.MNumClust.get(i));
						
						ClustersID.add(new Vector<Object>());
						ClustersID.get(nb).add(dbs.MNumClust.get(i));
						ClustersID.get(nb).add((int) IDpbc.get(i).get(1));
						
						ClustersCoords.add(new Vector<Double>());
						ClustersCoords.get(nb).add((double)dbs.MNumClust.get(i));
						ClustersCoords.get(nb).add((double) IDpbc.get(i).get(1));
						ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(1));
						ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(2));
						
						nb++;
					}
					
				}else
				{
					Clusters.add(new Vector<Object>());
					Clusters.get(nb).add(dbs.MNumClust.get(i));
					Clusters.get(nb).add((int) IDpbc.get(i).get(0));//que pour requête sur publications pour le moment
					
					num.add(dbs.MNumClust.get(i));
					
					ClustersID.add(new Vector<Object>());
					ClustersID.get(nb).add(dbs.MNumClust.get(i));
					ClustersID.get(nb).add((int) IDpbc.get(i).get(1));//que pour requête sur publications pour le moment
					
					ClustersCoords.add(new Vector<Double>());
					ClustersCoords.get(nb).add((double)dbs.MNumClust.get(i));
					ClustersCoords.get(nb).add((double) IDpbc.get(i).get(1));
					ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(1));
					ClustersCoords.get(nb).add((double) dbs.exd.Coord.get(i).get(2));
					
					nb++;
				}
			}else
			{
				continue;
			}
		}//fin construction des clusters

//		System.out.println("nb de clusters avant cham nb: " + nb);
//		for(int i=0; i<Clusters.size(); i++)
//		{
//			System.out.print(Clusters.get(i).get(0)+ "; ");
//		}
		Vector<Vector<Double>> Coord_Moyenne=CoordMoyenneParCluster(ClustersCoords);
		System.out.println("Fin composition des clusters");
//		System.out.println("Clusters = "+Clusters);		
//		System.out.println("numéros des clusters = "+num);
//		System.out.println("MNumClust = "+dbs.MNumClust);
		int cluster_visite=1;
		for(int i=0; i<Clusters.size()-1; i++)
		{
//			int nbPubPat1 = 0;
//			for(int k=1; k<Clusters.get(i).size(); k++)//on comptabilise le nb de publications au sein d'un même cluster
//			{
//				nbPubPat1+=CPubPat.get((Integer) Clusters.get(i).get(k)).size();
//			}
//			NbPubPat.add(new Vector<Integer>());
//			NbPubPat.get(i).add(i+1);
//			NbPubPat.get(i).add(nbPubPat1);
			double ncInterne1pub=0.0;
			//Se realiza por fuera porque con el mismo se va a tener siempre
			ncInterne1pub = NbCollaboration(i,i);
			
			for(int j = cluster_visite; j<Clusters.size(); j++)
			{
				double distance=dbs.DistancePts(Coord_Moyenne.get(i).get(1), Coord_Moyenne.get(i).get(2), Coord_Moyenne.get(j).get(1), Coord_Moyenne.get(j).get(2))/1000;
				//TODO AQUI ESTA EL ERROR cambiar para que no se calcule entre ellos mismos
				if(i!=j && distance<150){
					double ncInterne2pub =NbCollaboration(j,j);
					double ncpub=NbCollaboration(i,j);
					
					//TODO CAMBIO DE VARIABLES POR LOS VALORES DE LOS CLUSTERS
					//Se transforma el objeto en una cadena para despues hacer posible el cambio a un double
					String clust_getI=Clusters.get(i).get(0).toString();
					String clust_getJ=Clusters.get(j).get(0).toString();
					
					//RI.get(RI.size()-1).add((double) i);
					//RI.get(RI.size()-1).add((double) j);

					//Se cambian las validaciones de casteo para incluir al cluster i y al cluster j
					RI.add(new Vector<Double>());
					RI.get(RI.size()-1).add(Double.parseDouble(clust_getI));
				    RI.get(RI.size()-1).add(Double.parseDouble(clust_getJ));
				    
					if(ncInterne1pub+ncInterne2pub!=0)
					{	
						RI.get(RI.size()-1).add((double)(ncpub/((ncInterne1pub+ncInterne2pub)/2)));
					}else
					{
						//TODO cambio por lo mas bajo en valor antes 999.999
						RI.get(RI.size()-1).add(0.0);
					}
					
					//calcul de RI
					double t1 = Clusters.get(i).size();
					double t2 = Clusters.get(j).size();
					
					/*
					RC.add(new Vector<Double>());
					RC.get(RC.size()-1).add(Double.parseDouble(clust_getI));
				    RC.get(RC.size()-1).add(Double.parseDouble(clust_getJ));
					*/
					
				    //RC.get(RC.size()-1).add((double) i);
					//RC.get(RC.size()-1).add((double) j);
					//TODO CALCULO RC EN RI
					if((t1/(t1+t2)*ncInterne1pub/t1+t2/(t1+t2)*ncInterne2pub/t2
							)!=0)
					{
						RI.get(RI.size()-1).add((double) (ncpub/(t1/(t1+t2)*ncInterne1pub/t1+t2/(t1+t2)*ncInterne2pub/t2)));
					}else
					{
						/* TODO aqui el cambiaba los valores de RI
						ncInterne1pub = 1;
						ncInterne2pub = 1;
						*/
						RI.get(RI.size()-1).add(0.0);
					}
					//calcul de RC
				}
			}
			cluster_visite++;
		}// on compare les RI et RC des clusters. Si ces valeurs sont très élevées, on fusionne les clusters. Sinon, on les laisse tels quels.
//		System.out.println("Clusters = "+Clusters);
//		System.out.println("ClustersID = "+ClustersID);
		System.out.println("Ri sin ordenar"+RI);
		Collections.sort(RI, new MyComparator(3));
		System.out.println("Ri ordenado"+RI);
		System.out.println("Fin filtres chameleon");
	}

	double NbCollaboration(int i,int j)
	{
		double nb=0;
		
		for(int k=1; k<(int)Clusters.get(i).size(); k++)//attention, zéro est le numéro du cluster
		{
			int a = (Integer) Clusters.get(i).get(k);
			for(int l=1; l<(int)Clusters.get(j).size(); l++)//attention, zéro est le numéro du pôle
			{
				int b = (Integer) Clusters.get(j).get(l);
				if(i==j)
				{
					if(k!=l)
					{
						for(int m=1; m<(int)CPubPat.get(a).size(); m++)//attention, zéro est le numéro du pôle
						{
							for(int n=1; n<(int)CPubPat.get(b).size(); n++)
							{
								if((int)CPubPat.get(a).get(m) == (int) CPubPat.get(b).get(n))//si les deux pôles ont un même id de publication
								{
									nb ++;
								}
							}
						}
					}else{continue;}
				}else
				{
					for(int m=1; m<(int)CPubPat.get(a).size(); m++)//attention, zéro est le numéro du cluster
					{
						for(int n=1; n<(int)CPubPat.get(b).size(); n++)
						{
							if((int) CPubPat.get(a).get(m) == (int) CPubPat.get(b).get(n))//si les deux pôles ont un même id de publication
							{
								nb ++;
							}
						}
					}
				}
			}
		}// on compte le nombre de publications/brevets nés de collaborations entre les pôles du cluster i et ceux du cluster j.
		return nb; 
	}
	
	/**
	 * Methode pour faire le calcul du centroide pour chaque cluster
	 * @author Michel Revollo
	 * 
	 * @param ClustersCoords coord de points
	 * 
	 * @return indice de cluster avec x,y centroides
	 */
	public Vector<Vector<Double>> CoordMoyenneParCluster(Vector<Vector<Double>> Coords){
		Vector<Vector<Double>> ClusterCentroid = new Vector<Vector<Double>>();
		for(int i=0;i<Coords.size();i++){
			double x_moyenne=0.0;
			double y_moyenne=0.0;
			int cont=0;
			int size=Coords.get(i).size();
			for (int j = 2; j < size; j=j+3) {//comienza en uno porque la posicion 0 indica el numero de cluster
				x_moyenne+=(Coords.get(i).get(j));
				y_moyenne+=Coords.get(i).get(j+1);
				cont++;//CoordPoint.
			}
			x_moyenne=x_moyenne/cont;
			y_moyenne=y_moyenne/cont;
			ClusterCentroid.add(new Vector<Double>());
			ClusterCentroid.get(i).add(Coords.get(i).get(0));
			ClusterCentroid.get(i).add(x_moyenne);
			ClusterCentroid.get(i).add(y_moyenne);
			
		}
		return ClusterCentroid;
	}


	Vector<Vector<Integer>> ClassePubPat()
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
		System.out.println("Fin réorganisation pub");
		return CP;// au final, CP contient, pour chaque pôle (IDC), toutes ses publications par leur IDP.
	}
	
	public class MyComparator implements Comparator<Vector<Double>> {
		  private int columnIndex = 0;
		  public MyComparator(int columnIndex) {this.columnIndex = columnIndex;}

		@Override
		public int compare(Vector<Double> arg0, Vector<Double> arg1) {
			// TODO Auto-generated method stub
			return arg1.get(columnIndex).compareTo(arg0.get(columnIndex));
		}
	} 

}
