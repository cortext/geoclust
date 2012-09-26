import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class EcritureResultats {
	
	EcritureResultats(DBScan dbs)
	{
		FileWriter fwe = null;
		try 
		{
//			fwe = new FileWriter ("ResultatsClustering.csv");
			fwe = new FileWriter (dbs.exd.ui.jtfint3.getText());
			
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		} 
		
		BufferedWriter bwe = new BufferedWriter (fwe);
    	PrintWriter fichierSortie = new PrintWriter (bwe);
    	
    	fichierSortie.println("Id_couple;Numéro de cluster"+"Requête : ;Nb minimal de poits = "+dbs.exd.ui.tnbpts.getText()+";Rayon = " +dbs.exd.ui.tray.getText()+" m;Année début = "+dbs.exd.ui.deb.getText()+";Année fin = "+dbs.exd.ui.fin.getText());
    	for(int i = 0; i<dbs.v1; i++)
    	{
    		fichierSortie.println(dbs.exd.Coord.get(i).get(0).toString()+";"+dbs.MNumClust.get(i).toString());
    	}
		fichierSortie.close();
		System.out.println("Fin écriture DBScan");
	}
	

}
