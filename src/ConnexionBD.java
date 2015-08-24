import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;


public class ConnexionBD
{
 
	public static InterfaceBD rui;
	public Connection conn;
	public Statement stat; 
	
	/**
	 * Se connecter a la base de donnees
	 * @param ui
	 */
	public ConnexionBD(InterfaceBD ui) 
	{  
		rui = ui;
	
		try 
		{	 
			Class.forName("com.mysql.jdbc.Driver"); 
			

			String server = rui.tserver.getText();
			String user = rui.tuser.getText();
			String pw = rui.tpw.getText();
			String bd = rui.tbd.getText();
			
			String url = "jdbc:mysql://"+ server +"/"+ bd;
			Properties props = new Properties();
			props.setProperty("user",user);
			props.setProperty("password",pw);
			props.setProperty("autoReconnect", "true");

			conn = DriverManager.getConnection(url,user, pw);

			stat = conn.createStatement(); 
			
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