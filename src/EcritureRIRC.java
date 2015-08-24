import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Classe pour enregistrer les resultats RI RC pour les clusters qu'ont été comparés avec le format 
 * cluster a, cluster b, RI, RC, RI*RC^1.5
 * @author Michel Revollo
 *
 */
public class EcritureRIRC {
	
	EcritureRIRC(FiltresChameleon fc, int iter)
	{
		FileWriter fwe = null;
		String nomFichier="";
		String seuil1=""; // ri*rc puiss 1/2
		String seuil2=""; // rc 
		boolean ri_rcSelected;
		
		if(fc.dbs.exd.ui!=null){
			nomFichier=fc.dbs.exd.ui.jtfint4.getText();
			seuil1=fc.dbs.exd.ui.tseuil1.getText();
			ri_rcSelected=fc.dbs.exd.ui.checkbtn1.isSelected();
			seuil2=fc.dbs.exd.ui.tseuil2.getText();
		}
		else{
			nomFichier=fc.dbs.exd.uiCsv.jtfint4.getText();
			seuil1=fc.dbs.exd.uiCsv.tseuil1.getText();
			ri_rcSelected=fc.dbs.exd.uiCsv.checkbtn1.isSelected();
			seuil2=fc.dbs.exd.uiCsv.tseuil2.getText();
		}
		
		try 
		{
			fwe = new FileWriter (nomFichier+iter+".csv");
			
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
    	
    	if(ri_rcSelected){ 								  // on regarde quelle formule a ete utilise
    		valeurRI_pour_RC_ini=Double.valueOf(seuil1); //enregistre le valeur de la formule RI*RC^1.5 defini dans les inputs
    	}
    	else{
    		valeurRI_seuil=Double.valueOf(seuil1);			// enregistre le valeur de ri defini dans les parametres
        	valeurRC_seuil=Double.valueOf(seuil2);			// garde le valeur de rc
        	valeurRI_pour_RC_ini=valeurRI_seuil*Math.pow(valeurRC_seuil, 1.5);		// calcule et enregistre le resultat avec la formule indique precedement
    	}
    	
    	for(int i = 0; i<fc.RI.size(); i++)		// on doit parcourir les comparaisons pour les imprimer 
    	{
    		int cluster1=(int)(double)fc.RI.get(i).get(0); 		//cluster a 
    		int cluster2=(int)(double)fc.RI.get(i).get(1);		//cluster b
    		double ri=fc.RI.get(i).get(2);						//resutat ri
    		double rc=fc.RI.get(i).get(3);						//resultat rc
    		double ri_rc=fc.RI.get(i).get(4); 					//resultat ri*rc^1.5
    		
    		//if((ri>0.0 && rc>0.0 && valeurRC_seuil>0.0 && ri>valeurRI_seuil && rc>valeurRC_seuil) || (fc.dbs.exd.ui.checkbtn1.isSelected() && ri_rc> valeurRI_pour_RC_ini)){
    		//if((fc.dbs.exd.ui.checkbtn1.isSelected() && ri_rc> valeurRI_pour_RC_ini)){
    		if(fc.RI.get(i).get(5).equals(1.0)){
    		fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";"+ri_rc+";Y");
    		}
    		else{
    			fichierSortie.println(cluster1+";"+cluster2+";"+ri+";"+rc+";"+ri_rc+";N");
    		}
    	}
    	
		fichierSortie.close();
		System.out.println("Fin Ecriture RI RC");
	}
	

}
