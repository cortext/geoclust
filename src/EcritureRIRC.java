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
			fwe = new FileWriter (fc.dbs.exd.ui.jtfint4.getText());
			
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		} 
		
		BufferedWriter bwe = new BufferedWriter (fwe);
    	PrintWriter fichierSortie = new PrintWriter (bwe);
    	
    	fichierSortie.println("ID Cluster1;ID Cluster2;RI;RC;RI*RC^1.5;Fusion");
    	double valeurRI_seuil=0.0;
    	double valeurRC_seuil=0.0;
    	double valeurRI_pour_RC_ini=0.0;
    	
    	if(fc.dbs.exd.ui.checkbtn1.isSelected()){
    		valeurRI_pour_RC_ini=Double.valueOf(fc.dbs.exd.ui.tseuil1.getText());
    	}
    	else{
    		valeurRI_seuil=Double.valueOf(fc.dbs.exd.ui.tseuil1.getText());
        	valeurRC_seuil=Double.valueOf(fc.dbs.exd.ui.tseuil2.getText());
        	valeurRI_pour_RC_ini=valeurRI_seuil*Math.pow(valeurRC_seuil, 1.5);
    	}
    	
    	for(int i = 0; i<fc.RI.size(); i++)
    	{
    		int cluster1=(int)(double)fc.RI.get(i).get(0);
    		int cluster2=(int)(double)fc.RI.get(i).get(1);
    		double ri=fc.RI.get(i).get(2);
    		double rc=fc.RI.get(i).get(3);
    		double ri_rc=ri*Math.pow(rc,1.5); 
    		
    		if((ri>0.0 && rc>0.0 && valeurRC_seuil>0.0 && ri>valeurRI_seuil && rc>valeurRC_seuil) || (fc.dbs.exd.ui.checkbtn1.isSelected() && ri_rc> valeurRI_pour_RC_ini)){
    		//if((fc.dbs.exd.ui.checkbtn1.isSelected() && ri_rc> valeurRI_pour_RC_ini)){
    			fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";"+ri_rc+";Y");
    		}
    		else{
    			fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";"+ri_rc+";N");
    		}
    	}
    	
		fichierSortie.close();
		System.out.println("Fin écriture RI RC");
	}
	

}
