import java.sql.*;
import java.util.Vector;

/**
 * Classe pour obtenir les donnees de la base de donnees selon les criteres des requetes sql 
 * 
 * @author revollom
 *
 */
public class ExtractionDonnees 
{
	public Vector<Vector<Double>> Coord = new Vector<Vector<Double>>();
	public Vector<Vector<Integer>> IDpc = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> IDbc = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> Pub = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> Pat = new Vector<Vector<Integer>>();
	public ExtractionDonnees exd;
	public InterfaceInputDatabase ui;
	public InterfaceInputCsv uiCsv;
	
	public ExtractionDonnees(InterfaceInputDatabase u, ConnexionBD con) throws SQLException
	{
		exd = this;
		ui = u;

		int i = 0;
		
		if(ui.brevpat == 0)//brevets s�lectionn�s
		{
			// variable qu enregistre la requete avec laquel on veut obtenir les donnees pour les brevets  dans la base des donnees 
			String query = 	"SELECT "+ui.brev.getText()+".IDc,"+ui.brev.getText()+".IDb,"+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude, " + "COUNT("+ui.brev.getText()+".IDb) As quantite " +
							" FROM "+ui.tcoord.getText()+", "+ui.brev.getText()+" " +
							"WHERE " +ui.brev.getText()+".Year >= "+ ui.adeb+ " AND "+ui.brev.getText()+".Year <= " +ui.afin +" AND "+ ui.brev.getText()+".IDc = "+ui.tcoord.getText()+".IDc " +
							"GROUP BY "+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude ;";

			ResultSet resultat =  con.stat.executeQuery(query);  //variable que garde le resultat de la requete executee
			
			i = 0;
			while(resultat.next())								//on parcour les resultats 
			{ 
				Coord.add(new Vector<Double>());
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".IDc"));		// ajout des idcs dans la position 0 du vecteur des vecteurs
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".Longitude")); // ajout de la latitude dans la position 1 du vecteur 
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".Latitude"));	// ajout de la longitude
				Coord.get(i).add(0.0);													// Position pour savoir si le point est visite ou pas
				Coord.get(i).add(resultat.getDouble(".quantite")); 						// ajout quantite des brevets
				// le format final du vecteur Coord sera [idc1,lat1,long1,0.0,quantite1],[idc2,lat2,long2,0.0,quantite2],...,[idcN,latN,longN,0.0,quantiteN]
				IDbc.add(new Vector<Integer>());
				IDbc.get(i).add(i);														//on enregistre la position i dans le vecteur qui indique l index ou se trouve le resultat de IDc dans le vecteur Coord 
				IDbc.get(i).add((int) resultat.getDouble(ui.tcoord.getText()+".IDc"));	//ajout de IDc respective pour l index du vecteur Coord 
				i++;
			}
			String querybis = "select "+ui.brev.getText()+".IDc,"+ui.brev.getText()+".IDb from "+ui.brev.getText()+" where " +ui.brev.getText()+".Year >= "+ ui.adeb+ " AND "+ui.brev.getText()+".Year<= " +ui.afin+";";
			ResultSet resultatbis =  con.stat.executeQuery(querybis); 
			i = 0;
			while(resultatbis.next())
			{ 
				Pat.add(new Vector<Integer>());
				Pat.get(i).add((int)resultatbis.getDouble(ui.brev.getText()+".IDc"));
				Pat.get(i).add((int)resultatbis.getDouble(ui.brev.getText()+".IDb"));
				i++;
			}
			System.out.println("Brevets : "+Pat.size());
		}
		else if(ui.brevpat == 1)//publications s�lectionn�es
		{ 
			String query1 = "SELECT "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp,"+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude," + "COUNT("+ui.pub.getText()+".IDp) AS quantite "+
					        "FROM "+ui.tcoord.getText()+", "+ui.pub.getText()+" " +
							"WHERE " +ui.pub.getText()+".Year >= "+ ui.adeb+ " AND "+ui.pub.getText()+".Year<= " +ui.afin+" AND "+ui.pub.getText()+".IDc = "+ui.tcoord.getText()+".IDc " +
							"AND "+ ui.pub.getText()+".IDp NOT IN (SELECT document_id FROM block_publications_non_nano) " +
							"AND "+ui.tcoord.getText()+".Latitude <> 0 AND "+ ui.tcoord.getText()+".Longitude <> 0 "+
							"GROUP BY "+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude;";

			ResultSet resultat1 =  con.stat.executeQuery(query1); 
		    
			i = 0;
			while(resultat1.next())
			{ 
				Coord.add(new Vector<Double>());
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".IDc"));
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".Longitude"));
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".Latitude"));
				Coord.get(i).add(0.0);//Position pour savoir si le point est visite ou pas default false=0.0
				Coord.get(i).add(resultat1.getDouble(".quantite")); // Quantite des points c est a dire le poid
				IDpc.add(new Vector<Integer>());
				IDpc.get(i).add(i);
				IDpc.get(i).add((int) resultat1.getDouble(ui.tcoord.getText()+".IDc"));
				i++;
			}
			String query1bis = "select "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp " +
							   "from "+ui.pub.getText()+" " +
						   	   "where " +ui.pub.getText()+".Year >= "+ ui.adeb+ " " +
						   	   "AND "+ ui.pub.getText()+".IDp NOT IN (SELECT document_id FROM block_publications_non_nano) " +
						   	   "AND "+ui.pub.getText()+".Year<= " +ui.afin+";";
			ResultSet resultat1bis =  con.stat.executeQuery(query1bis); 
			i = 0;
			while(resultat1bis.next())
			{ 
				Pub.add(new Vector<Integer>());
				Pub.get(i).add((int)resultat1bis.getDouble(ui.pub.getText()+".IDc"));
				Pub.get(i).add((int)resultat1bis.getDouble(ui.pub.getText()+".IDp"));
				i++;
			}				
			System.out.println("Publications : "+Pub.size());
		}else//selection � la fois des brevets et publications
		{
			//chargement et taitement des brevets
			String querybis = "select "+ui.brev.getText()+".IDc,"+ui.brev.getText()+".IDb from "+ui.brev.getText()+" where " +ui.brev.getText()+".Year >= "+ ui.adeb+ " AND "+ui.brev.getText()+".Year<= " +ui.afin+";";
			ResultSet resultatbis =  con.stat.executeQuery(querybis); 
			i = 0;
			while(resultatbis.next())
			{ 
				Pat.add(new Vector<Integer>());
				Pat.get(i).add((int)resultatbis.getDouble(ui.brev.getText()+".IDc"));
				Pat.get(i).add((int)resultatbis.getDouble(ui.brev.getText()+".IDb"));
				i++;
			}
			System.out.println("Brevets : "+Pat.size());
			
			//chargement et traitement des publications
			String query1 = "SELECT "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp,"+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude " +
							"FROM "+ui.tcoord.getText()+", "+ui.pub.getText()+" " +
							"WHERE " +ui.pub.getText()+".Year >= "+ ui.adeb+ " AND "+ui.pub.getText()+".Year<= " +ui.afin+" " +
									"AND "+ui.pub.getText()+".IDc = "+ui.tcoord.getText()+".IDc " + 
							"GROUP BY "+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude ;";

			ResultSet resultat1 =  con.stat.executeQuery(query1); 
		
			i = 0;
			while(resultat1.next())
			{ 
				Coord.add(new Vector<Double>());
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".IDc"));
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".Longitude"));
				Coord.get(i).add(resultat1.getDouble(ui.tcoord.getText()+".Latitude"));
				Coord.get(i).add(0.0);//Position pour savoir si le point est visite ou pas default false=0.0
				IDpc.add(new Vector<Integer>());
				IDpc.get(i).add(i);
				IDpc.get(i).add((int) resultat1.getDouble(ui.tcoord.getText()+".IDc"));
				i++;
			}
			String query1bis = "SELECT "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp " +
								"FROM "+ui.pub.getText()+" " +
								"WHERE " +ui.pub.getText()+".Year >= "+ ui.adeb+ " " +
								"AND "+ui.pub.getText()+".Year<= " +ui.afin+";";
			ResultSet resultat1bis =  con.stat.executeQuery(query1bis); 
			i = 0;
			while(resultat1bis.next())
			{ 
				Pub.add(new Vector<Integer>());
				Pub.get(i).add((int)resultat1bis.getDouble(ui.pub.getText()+".IDc"));
				Pub.get(i).add((int)resultat1bis.getDouble(ui.pub.getText()+".IDp"));
				i++;
			}				
			System.out.println("Publications : "+Pub.size());			
		}
		System.out.println("Fin extraction donnees");		
	}
	
	public ExtractionDonnees(InterfaceInputCsv u, ConnexionBD con) throws SQLException
	{
		exd = this;
		uiCsv = u;
		String tablaCoord="ww_pruebacoord";
		String tablaData="ww_pruebadata";
		int i = 0;
		
		// variable qu enregistre la requete avec laquel on veut obtenir les donnees pour les brevets  dans la base des donnees 
		String query = 	"SELECT IDc, Latitude, Longitude, weight " +
						"FROM "+tablaCoord+";";
	
		ResultSet resultat =  con.stat.executeQuery(query);  //variable que garde le resultat de la requete executee
		
		i = 0;
		while(resultat.next())								//on parcour les resultats 
		{ 
			Coord.add(new Vector<Double>());
			Coord.get(i).add(resultat.getDouble(tablaCoord+".IDc"));		// ajout des idcs dans la position 0 du vecteur des vecteurs
			Coord.get(i).add(resultat.getDouble(tablaCoord+".Longitude")); // ajout de la latitude dans la position 1 du vecteur 
			Coord.get(i).add(resultat.getDouble(tablaCoord+".Latitude"));	// ajout de la longitude
			Coord.get(i).add(0.0);													// Position pour savoir si le point est visite ou pas
			Coord.get(i).add(resultat.getDouble(tablaCoord+".weight")); 						// ajout quantite des brevets
			// le format final du vecteur Coord sera [idc1,lat1,long1,0.0,quantite1],[idc2,lat2,long2,0.0,quantite2],...,[idcN,latN,longN,0.0,quantiteN]
			IDbc.add(new Vector<Integer>());
			IDbc.get(i).add(i);														//on enregistre la position i dans le vecteur qui indique l index ou se trouve le resultat de IDc dans le vecteur Coord 
			IDbc.get(i).add((int) resultat.getDouble(tablaCoord+".IDc"));	//ajout de IDc respective pour l index du vecteur Coord 
			i++;
		}
		String querybis = "SELECT b.IDc, a.IDb " +
				"FROM "+tablaData+" as a "+
				"INNER JOIN "+tablaCoord+" as b " +
				"ON  a.IDc=b.IDc; "; 
		ResultSet resultatbis =  con.stat.executeQuery(querybis); 
		i = 0;
		while(resultatbis.next())
		{ 
			Pat.add(new Vector<Integer>());
			Pat.get(i).add((int)resultatbis.getDouble("b.IDc"));
			Pat.get(i).add((int)resultatbis.getDouble("a.IDb"));
			i++;
		}
		System.out.println("Publications : "+Pat.size());
	}
}
