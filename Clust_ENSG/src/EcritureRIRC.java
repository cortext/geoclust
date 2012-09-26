import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class EcritureRIRC {
	
	EcritureRIRC(FiltresChameleon fc)
	{
		FileWriter fwe = null;
		try 
		{
//			fwe = new FileWriter ("ResultatsClustering.csv");
			fwe = new FileWriter ("RI_RC.csv");
			
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		} 
		
		BufferedWriter bwe = new BufferedWriter (fwe);
    	PrintWriter fichierSortie = new PrintWriter (bwe);
    	
    	fichierSortie.println("ID Cluster1;ID Cluster2;RI;RC;Fusion");
    	Double valeurRI_seuil=Double.valueOf(fc.dbs.exd.ui.tseuil1.getText());
    	Double valeurRC_seuil=Double.valueOf(fc.dbs.exd.ui.tseuil2.getText());
    	for(int i = 0; i<fc.RI.size(); i++)
    	{
    		Double cluster1=fc.RI.get(i).get(0);
    		Double cluster2=fc.RI.get(i).get(1);
    		Double ri=fc.RI.get(i).get(2);
    		Double rc=fc.RC.get(i).get(2);
    		
    		if(ri>valeurRI_seuil && rc>valeurRC_seuil){
    			fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";Y");
    		}
    		else{
    			fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";N");
    		}
    	}
    	
		fichierSortie.close();
		System.out.println("Fin écriture RI RC");
	}
	

}
