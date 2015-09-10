import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import com.opencsv.CSVReader;

/**
 * Module pour lire un fichier csv et le mettre dans une base de donnees
 * 
 * 
 * @author correspondant
 *
 */
public class ImportCsvToDatabase {
	
	public static void importCsvToDatabaseUsingLoad(InterfaceInputCsv ui, ConnexionBD con)
	{
		try {
			ajoutMetaDonnees(ui, con);
			preparerCoordonnees(ui, con);
			preparerDonnees(ui, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param ui
	 * @param con
	 */
	public static void importCsvToDatabase(InterfaceInputCsv ui, ConnexionBD con)
	{

			try{
				CSVReader reader = new CSVReader(new FileReader("upload.csv"), ',');
				String insertQuery = "INSERT INTO txn_tbl (txn_id,txn_amount, card_number, terminal_id) values (null,?,?,?)";
				PreparedStatement pstmt = con.conn.prepareStatement(insertQuery);
				String[] rowData = null;
				int i = 0;
				while((rowData = reader.readNext()) != null){
				for (String data : rowData)
				{
						pstmt.setString((i % 3) + 1, data);

						if (++i % 3 == 0)
								pstmt.addBatch();// add batch

						if (i % 30 == 0)// insert when the batch size is 10
								pstmt.executeBatch();
				}}
				System.out.println("Les donnees sont bien enregistres");
			}
			catch (Exception e)
			{
					e.printStackTrace();
			}

	}
	
	/**
	 * Methode pour preparer les metadonnees provenant du fichier csv
	 * 
	 * @param ui
	 * @param con
	 * @throws SQLException
	 */
	protected static void ajoutMetaDonnees(InterfaceInputCsv ui, ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_inputs";
		String createTableQuery = "CREATE TABLE "+"ww_inputs"+" (" +
				  "IDb INT(11) NOT NULL," +
				  "Year INT(4) DEFAULT NULL," +
				  "Latitude  decimal(10,7) DEFAULT NULL," +
				  "Longitude  decimal(10,7) DEFAULT NULL," +
				  "KEY IDb (IDb)) ";
		
		String loadQuery = "LOAD DATA LOCAL INFILE '" + ui.inputPathText.getText() + "' " +
					"INTO TABLE ww_inputs FIELDS TERMINATED BY '\t'"
				+ " LINES TERMINATED BY '\n' (IDb,Year,Latitude, Longitude)";
		
		String indexQueryLat= "ALTER TABLE ww_inputs ADD INDEX(Latitude)";
		String indexQueryLong= "ALTER TABLE ww_inputs ADD INDEX(Longitude)";
		String indexQueryLatLong = "ALTER TABLE ww_inputs ADD INDEX(Latitude,Longitude)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.execute(loadQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryLat);//ajout des index
		con.stat.execute(indexQueryLong);
		con.stat.execute(indexQueryLatLong);
	}
	
	/**
	 * Methode pour céer une table contenant des couples x,y uniques avec un identifiant 
	 * unique
	 * 
	 * @param ui
	 * @param con
	 * @throws SQLException
	 */
	protected static void preparerCoordonnees(InterfaceInputCsv ui, ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_pruebaCoord";
		String createTableQuery = "CREATE TABLE ww_pruebaCoord (" +
				  "IDc INT(11) NOT NULL AUTO_INCREMENT," +
				  "Latitude  decimal(10,7) DEFAULT NULL," +
				  "Longitude  decimal(10,7) DEFAULT NULL," +
				  "weight INT(11)," +
				  "PRIMARY KEY (IDc)) ";
		
		String insertQuery = "INSERT INTO ww_pruebaCoord(Latitude,Longitude,weight)" +
				"SELECT Latitude,Longitude , COUNT(*) as weight " +
				"FROM ww_inputs " +
				"WHERE Latitude IS NOT NULL AND Longitude IS NOT NULL " +
				"AND Year BETWEEN "+ui.adeb+" AND "+ui.afin+" "+
				"GROUP BY Latitude,Longitude";
		
		String indexQueryLat= "ALTER TABLE ww_pruebaCoord ADD INDEX(Latitude)";
		String indexQueryLong= "ALTER TABLE ww_pruebaCoord ADD INDEX(Longitude)";
		String indexQueryLatLong = "ALTER TABLE ww_pruebaCoord ADD INDEX(Latitude,Longitude)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.execute(insertQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryLat);//ajout des index
		con.stat.execute(indexQueryLong);
		con.stat.execute(indexQueryLatLong);
	}
	
	/**
	 * Methode pour preparer les donnees correspondant aux publications 
	 * ou brevet ou autres, 
	 * 
	 * @param ui : interface qui contient les parametres pour le clustering 
	 * @param con : la connexion a la base de donnees
	 * @throws SQLException
	 */
	protected static void preparerDonnees(InterfaceInputCsv ui, ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_pruebaData";
		String createTableQuery = "CREATE TABLE "+"ww_pruebaData"+" (" +
				  "IDb INT(11) DEFAULT NULL," +
				  "IDc INT(11)  DEFAULT NULL," +
				  "Year INT(4) DEFAULT NULL," +
				  "KEY (IDb)) ";
		
		String insertQuery = "INSERT INTO ww_pruebaData " +
				"SELECT a.IDb,b.IDc,a.Year " +
				"FROM ww_inputs a INNER JOIN ww_pruebaCoord b " +
				"ON a.Latitude=b.Latitude AND a.Longitude=b.Longitude " +
				"WHERE a.Year BETWEEN "+ui.adeb+" AND "+ui.afin+" "+
				"ORDER BY a.IDb ";
		
		String indexQueryIDb= "ALTER TABLE ww_pruebaData ADD INDEX(IDb)";
		String indexQueryIDc= "ALTER TABLE ww_pruebaData ADD INDEX(IDc)";
		String indexQueryIDbIDc= "ALTER TABLE ww_pruebaData ADD INDEX(IDb,IDc)";
		String indexQueryYear = "ALTER TABLE ww_pruebaData ADD INDEX(Year)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.executeUpdate(insertQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryIDb);//ajout des index
		con.stat.execute(indexQueryIDc);
		con.stat.execute(indexQueryIDbIDc);
		con.stat.execute(indexQueryYear);
	}
	
	/**
	 * @param path
	 * @param con
	 * @throws SQLException
	 */
	protected static void ecritureDBScan(String path, ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_clusterDbScan";
		String createTableQuery = "CREATE TABLE "+"ww_clusterDbScan"+" (" +
				  "IDc INT(11) NOT NULL," +
				  "IdClusterDbScan INT(4) ) ";
		
		String loadQuery = "LOAD DATA LOCAL INFILE '" + path + "_DBScan.csv' " +
					"INTO TABLE ww_clusterDbScan FIELDS TERMINATED BY ';'"
				+ " LINES TERMINATED BY '\r\n' IGNORE 1 LINES ";
		
		String indexQueryLat= "ALTER TABLE ww_clusterDbScan ADD INDEX(IDc)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.execute(loadQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryLat);//ajout des index
		
	}
	
	/**
	 * Creation de la table pour enregistrer les donnees de clustering provenants de 
	 * @param path
	 * @param con
	 * @throws SQLException
	 */
	protected static void ecritureChameleon(String path, ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_clusterChameleon";
		String createTableQuery = "CREATE TABLE "+"ww_clusterChameleon"+" (" +
				  "IDc INT(11) NOT NULL," +
				  "IdClusterCham INT(4) ) ";
		
		String loadQuery = "LOAD DATA LOCAL INFILE '" + path + ".csv' " +
					"INTO TABLE ww_clusterChameleon FIELDS TERMINATED BY ';'"
				+ " LINES TERMINATED BY '\n' IGNORE 1 LINES ";
		
		String indexQueryLat= "ALTER TABLE ww_clusterChameleon ADD INDEX(IDc)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.execute(loadQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryLat);//ajout des index
		
	}
	
	protected static void produireResultatClustering(ConnexionBD con) throws SQLException{
		String dropTable = "DROP TABLE IF EXISTS ww_resultatclustering";
		String createTableQuery = "CREATE TABLE "+"ww_resultatclustering"+" (" +
				  "IDc INT(11) NOT NULL," +
				  "Latitude  decimal(10,7) DEFAULT NULL," +
				  "Longitude  decimal(10,7) DEFAULT NULL," +
				  "nbArticles INT(11)," +
				  "IdClusterDbScan INT(4) , " +
				  "IdClusterCham INT(4)," +
				  "isFusion varchar(4) )";
		
		String insertQuery = "INSERT INTO ww_resultatclustering "+
							"SELECT a.IDc,a.Latitude,a.Longitude,a.weight as nbArticles,"+
							"b.IdClusterDbScan,c.IdClusterCham,"+
							"IF(d.IdClusterCham IS NOT NULL,'Yes','No') as fusion "+
							"FROM ww_pruebacoord a "+
							"INNER JOIN ww_clusterdbscan b ON a.IDc=b.IDc "+
							"INNER JOIN ww_clusterchameleon c ON b.IDc=c.IDc "+
							"LEFT JOIN (SELECT a.IdClusterDbScan,b.IdClusterCham "+
										"FROM ww_clusterdbscan a "+
										"INNER JOIN ww_clusterchameleon b "+
										"ON a.IDc=b.IDc "+
										"WHERE a.IdClusterDbScan<>b.IdClusterCham "+
										"GROUP by a.IdClusterDbScan,b.IdClusterCham) d  "+
							"ON c.IdClusterCham=d.IdClusterCham " +
							"GROUP BY a.IDc";

		String indexQueryLat= "ALTER TABLE ww_resultatclustering ADD INDEX(IDc)";

		con.stat.execute(dropTable);
		con.stat.execute(createTableQuery);//creation de la table dans la bd
		con.stat.execute(insertQuery);//execute l insertion des donnees 
		con.stat.execute(indexQueryLat);//ajout des index
		
	}
	
	public static String getPathFromProject() {
		File currentDirFile = new File("");
		String helper = currentDirFile.getAbsolutePath().replace("\\", "/");
		return helper;
	}
	
	protected static void ecritureClustering(String path,ConnexionBD con) throws SQLException{
		produireResultatClustering(con);
		getPathFromProject();
		String exportClustering = "SELECT 'IDc','Lat','Long', 'NbArticles','IdClustDBScan'," +
				"'IdClustCham', 'isFusion' " +
									"UNION SELECT * INTO OUTFILE '"+getPathFromProject()+"/resultat_Clustfinal2.csv' " +
								  "FIELDS TERMINATED BY '\t' FROM ww_resultatclustering t";
		con.stat.execute(exportClustering);
	}
	
}
