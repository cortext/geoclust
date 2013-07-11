import java.sql.*;
import java.util.Vector;

public class ExtractionDonnees 
{
	public Vector<Vector<Double>> Coord = new Vector<Vector<Double>>();
	public Vector<Vector<Integer>> IDpc = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> IDbc = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> Pub = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> Pat = new Vector<Vector<Integer>>();
	public ExtractionDonnees exd;
	public Interface ui;
	
	public ExtractionDonnees(Interface u, ConnexionBD con) throws SQLException
	{
		exd = this;
		ui = u;

		int i = 0;
		
		if(ui.brevpat == 0)//brevets s�lectionn�s
		{
		
			String query = 	"SELECT "+ui.brev.getText()+".IDc,"+ui.brev.getText()+".IDb,"+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude, " + "COUNT("+ui.brev.getText()+".IDb) As quantite " +
							" FROM "+ui.tcoord.getText()+", "+ui.brev.getText()+" " +
							"WHERE " +ui.brev.getText()+".Year >= "+ ui.adeb+ " AND "+ui.brev.getText()+".Year <= " +ui.afin +" AND "+ ui.brev.getText()+".IDc = "+ui.tcoord.getText()+".IDc " +
							"GROUP BY "+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude ;";

			ResultSet resultat =  con.stat.executeQuery(query); 
			
			i = 0;
			while(resultat.next())
			{ 
				Coord.add(new Vector<Double>());
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".IDc"));
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".Longitude"));
				Coord.get(i).add(resultat.getDouble(ui.tcoord.getText()+".Latitude"));
				Coord.get(i).add(0.0);//Position pour savoir si le point est visite ou pas
				Coord.get(i).add(resultat.getDouble(".quantite")); // ajout quantite des brevets 
				IDbc.add(new Vector<Integer>());
				IDbc.get(i).add(i);
				IDbc.get(i).add((int) resultat.getDouble(ui.tcoord.getText()+".IDc"));
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
			String query1bis = "select "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp from "+ui.pub.getText()+" where " +ui.pub.getText()+".Year >= "+ ui.adeb+ " AND "+ui.pub.getText()+".Year<= " +ui.afin+";";
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
			String query1 = "select "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp,"+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude from "+ui.tcoord.getText()+", "+ui.pub.getText()+" where " +ui.pub.getText()+".Year >= "+ ui.adeb+ " AND "+ui.pub.getText()+".Year<= " +ui.afin+" AND "+ui.pub.getText()+".IDc = "+ui.tcoord.getText()+".IDc GROUP BY "+ui.tcoord.getText()+".IDc, "+ui.tcoord.getText()+".Latitude,"+ui.tcoord.getText()+".Longitude ;";

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
			String query1bis = "select "+ui.pub.getText()+".IDc,"+ui.pub.getText()+".IDp from "+ui.pub.getText()+" where " +ui.pub.getText()+".Year >= "+ ui.adeb+ " AND "+ui.pub.getText()+".Year<= " +ui.afin+";";
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
}
