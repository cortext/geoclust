import java.sql.*; 
import java.util.Properties;


public class ConnexionBD
{
 
	public static Interface rui;
	public Connection conn;
	public Statement stat; 
	
	public ConnexionBD(Interface ui) 
	{  
		rui = ui;
	
		try 
		{	 
			Class.forName("com.mysql.jdbc.Driver"); 
			
//			String server = "localhost:3307";
//			String user = "t_revollom";
//			String pw = "t14mich53";
//			String bd = "t_michel";

			String server = rui.uibd.tserver.getText();
			String user = rui.uibd.tuser.getText();
			String pw = rui.uibd.tpw.getText();
			String bd = rui.uibd.tbd.getText();
			
			String url = "jdbc:mysql://"+ server +"/"+ bd;
			Properties props = new Properties();
			props.setProperty("user",user);
			props.setProperty("password",pw);
			props.setProperty("autoReconnect", "true");

			conn = DriverManager.getConnection(url,user, pw);

			stat = conn.createStatement(); 
			
//			String query = "select Latitude from a01_03_longlatcouple_idc"; 
//			
//			ResultSet resultat =  stat.executeQuery(query); 
//			
//			
//			while(resultat.next())
//			{ 
//				System.out.println(""+resultat.getDouble("Latitude")); 
//			} 
//			
//		
		} catch(Exception ex) 
		{ 
			System.out.println(ex.getMessage()); 
		
		} 
	}
	
	public static void main(String[] args) 
	{  
		ConnexionBD co = new ConnexionBD(rui);
	}
}