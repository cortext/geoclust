import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class EcritureResultats {
	
	EcritureResultats(DBScan dbs,boolean flag)
	{
		FileWriter fwe = null;
		String nomFichier="";
		
		if(dbs.exd.ui!=null){
			nomFichier=dbs.exd.ui.jtfint3.getText();
		}
		else{
			nomFichier=dbs.exd.uiCsv.jtfint3.getText();
		}
		
		try 
		{
			if(flag){
				fwe = new FileWriter (nomFichier+".csv");
			}
			else{
				fwe = new FileWriter (nomFichier+"_DBScan.csv");
			}
			
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		} 
		
		BufferedWriter bwe = new BufferedWriter (fwe);
    	PrintWriter fichierSortie = new PrintWriter (bwe);
    	//On imprime pour chaque publication ou brevet son couple id et son numero de cluster 
    	//fichierSortie.println("Id_couple;Numero de cluster"+"Requete : ;Nb minimal de poits = "+dbs.exd.ui.tnbpts.getText()+";Rayon = " +dbs.exd.ui.tray.getText()+" m;Annee debut = "+dbs.exd.ui.deb.getText()+";Annee fin = "+dbs.exd.ui.fin.getText());
    	for(int i = 0; i<dbs.v1; i++)
    	{
    		fichierSortie.println((int)(double)dbs.exd.Coord.get(i).get(0)+";"+dbs.MNumClust.get(i).toString());
    	}
		fichierSortie.close();
		
		System.out.println("Fin ecriture DBScan");
	}
	

}
