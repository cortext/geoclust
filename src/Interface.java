import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;


public class Interface extends JFrame
{
	public Interface ui;
	public static InterfaceBD uibd;
	public ConnexionBD con;
	public int adeb;
	public int afin;
	public int brevpat;
	public boolean ri_pour_rc;
	JPanel pan = new JPanel();
	JPanel pan1 = new JPanel();
	
	public long temp_ini;
	
	JButton bouton2 = new JButton("Classifier");

	JLabel label0 = new JLabel("Tables de la base de données");
	JLabel label1 = new JLabel("Intervalle d'analyse");
	public JLabel brevet = new JLabel("Table des brevets");
	public JLabel publication = new JLabel("Table des publications");
	public JLabel coord= new JLabel("Table des coordonnées et identifiants");
	public JLabel output = new JLabel("Nom du fichier de sortie Clusters");
	public JLabel output2 = new JLabel("Nom du fichier de sortie RI_RC");
	public JLabel tdeb = new JLabel("Année début");
	public JLabel tfin = new JLabel("Année fin (inclue)");
	public JLabel nbpts = new JLabel("Nb minimal de points dans 1 cluster");
	public JLabel ray = new JLabel("Rayon maximal d'un cluster (en mètres)");
	public JLabel seuil1 = new JLabel("Seuil RI");
	public JLabel seuil2 = new JLabel("Seuil RC");
	public JLabel iter = new JLabel("Nombre d'itérations de Chameleon");
		
//	JRadioButton jr0 = new JRadioButton("Geographic  distance");
//    JRadioButton jr1 = new JRadioButton("Levenstein");
//    JRadioButton jr2 = new JRadioButton("Soundex");
    JRadioButton jr3 = new JRadioButton("Brevets et Publications");
    JRadioButton jr4 = new JRadioButton("Brevets");
    JRadioButton jr5 = new JRadioButton("Publications");
    JCheckBox checkbtn1 = new JCheckBox("Formule (RI*RC^1.5)");
    JRadioButton defaut = new JRadioButton("Defaut");
    JRadioButton param = new JRadioButton("Parametrable");
    

    
    ButtonGroup bg = new ButtonGroup();
    ButtonGroup bg1 = new ButtonGroup();

    JFormattedTextField pub = new JFormattedTextField();
    JFormattedTextField brev = new JFormattedTextField();
    JFormattedTextField jtfint3 = new JFormattedTextField();
    JFormattedTextField jtfint4 = new JFormattedTextField();
    JTextField fin = new JTextField();
    JTextField deb = new JTextField();
    public JTextField tnbpts = new JTextField();
    JTextField tray = new JTextField();
    JTextField tcoord = new JTextField();
    JTextField tseuil1 = new JTextField();
    JTextField tseuil2 = new JTextField();
    JTextField titer = new JTextField();
    
	public Interface(InterfaceBD bd)
	{
		uibd = bd;
		Color col = new Color(250, 250, 210);
		
		ui = this;
		this.setTitle("Logiciel de Clustering");
        this.setSize(800, 480);
        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pan.setBackground(col);
        pan1.setBackground(Color.getHSBColor(10, 10, 50));

        jr3.addActionListener(new RadioStateListener());
        jr4.addActionListener(new RadioStateListener());
        jr5.addActionListener(new RadioStateListener());
        checkbtn1.addActionListener(new CheckBoxListener());
        defaut.addActionListener(new RadioDef());
        param.addActionListener(new RadioDef());
        
        bouton2.addActionListener(new Clust());        
        
        Font f1 = new Font("Arial", Font.BOLD, 12);
        pub.setFont(f1);
        pub.setSize(100, 30);
        pub.setForeground(Color.BLUE);
        brev.setFont(f1);
        brev.setPreferredSize(new Dimension(100, 30));
        brev.setForeground(Color.BLUE);
        jtfint3.setFont(f1);
        jtfint3.setSize(100, 30);
        jtfint3.setForeground(Color.BLUE);
        jtfint4.setFont(f1);
        jtfint4.setSize(100, 30);
        jtfint4.setForeground(Color.BLUE);
        fin.setFont(f1);
        deb.setFont(f1);
        tray.setFont(f1);
        tseuil1.setFont(f1);
        titer.setFont(f1);
        tseuil2.setFont(f1);
        tnbpts.setFont(f1);
        tcoord.setFont(f1);
        fin.setSize(100, 30);
        fin.setForeground(Color.BLUE);
        deb.setForeground(Color.BLUE);
        tray.setForeground(Color.BLUE);
        tseuil1.setForeground(Color.BLUE);
        titer.setForeground(Color.BLUE);
        tseuil2.setForeground(Color.BLUE);
        tnbpts.setForeground(Color.BLUE);
        tcoord.setForeground(Color.BLUE);
        
        Font f2 = new Font("Arial", Font.BOLD, 14);
        label0.setFont(f2);
        label0.setForeground(Color.RED);
        label1.setFont(f2);
        label1.setForeground(Color.RED);
//        label5.setFont(f2);
//        label5.setForeground(Color.RED);      
        
//        bg.add(jr0);
//        bg.add(jr1);
//        bg.add(jr2);
        bg.add(jr3);
        bg.add(jr4);
        bg.add(jr5);
        bg1.add(defaut);
        bg1.add(param);
     
//        jr0.setBackground(col);
//        jr1.setBackground(col);
//        jr2.setBackground(col);
        jr3.setBackground(col);
        jr4.setBackground(col);
        jr5.setBackground(col);
        checkbtn1.setBackground(col);
        
        defaut.setBackground(Color.getHSBColor(10, 10, 50));
        param.setBackground(Color.getHSBColor(10, 10, 50));
                
        GridLayout gl1 = new GridLayout (13, 3, 40, 15);       
        
        pan1.setLayout(gl1);
        pan1.add(defaut);      		pan1.add(param);      		pan1.add(checkbtn1);
        pan1.add(label0);			pan1.add(label1);			pan1.add(seuil1);
        pan1.add(brevet);			pan1.add(tdeb);				pan1.add(tseuil1);
        pan1.add(brev);				pan1.add(deb);				pan1.add(seuil2);
        pan1.add(publication);		pan1.add(tfin);				pan1.add(tseuil2);
        pan1.add(pub);				pan1.add(fin);				pan1.add(output);
        pan1.add(coord);			pan1.add(nbpts);			pan1.add(jtfint3);
        pan1.add(tcoord);          	pan1.add(tnbpts);			pan1.add(output2);
        pan1.add(iter);         	pan1.add(ray);				pan1.add(jtfint4);
        pan1.add(titer);			pan1.add(tray);	        	pan1.add(jr4);
        pan1.add(new JLabel(""));	pan1.add(new JLabel(""));	pan1.add(jr5);
        pan1.add(new JLabel(""));	pan1.add(new JLabel(""));	pan1.add(jr3);
        pan1.add(new JLabel(""));	pan1.add(new JLabel(""));	pan1.add(bouton2);
        
        this.setContentPane(pan1);
        this.setVisible(true);

	}
	
	
	public class Clust implements ActionListener 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
			afin = Integer.valueOf(fin.getText());
			adeb = Integer.valueOf(deb.getText()); 
			
	    	con = new ConnexionBD(ui);
	    	try 
	    	{
	    		temp_ini=System.currentTimeMillis();
				ExtractionDonnees exd = new ExtractionDonnees(ui, con);
				
				DBScan dbs = new DBScan(exd);
				FiltresChameleon fc = new FiltresChameleon(dbs);
				EcritureResultats er = new EcritureResultats(dbs);
				EcritureRIRC rirc= new EcritureRIRC(fc); 
				
				long temp_final=System.currentTimeMillis()-temp_ini;
				
				long heure = temp_final/3600000;
				long manque_heure = temp_final%3600000;
				long minute = manque_heure/60000;
				long manque_minute = manque_heure%60000;
				long seg = manque_minute/1000;
				System.out.println("tiempo que tarda heure:minutes:seg -> "+heure+" h:"+minute+" m:"+seg+" s");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	}

	class RadioStateListener implements ActionListener
	{	 
        public void actionPerformed(ActionEvent e) 
        {
        	if(jr4.isSelected()){brevpat = 0;}//brevets selectionnés
        	else{if(jr5.isSelected()){brevpat = 1;}else{brevpat = 3;}}
        }
        
	}
	
	class CheckBoxListener implements ActionListener
	{	 
        public void actionPerformed(ActionEvent e) 
        {
        	if(checkbtn1.isSelected()){ri_pour_rc=true;}
        	else{ri_pour_rc=false;}
        }
        
	}
	

	class RadioDef implements ActionListener
	{	 
        public void actionPerformed(ActionEvent e) 
        {
        	if(defaut.isSelected())
        	{
        		pub.setText("a01_06_pub_idp_idc");
        		brev.setText("a01_04_pat_idb_idc");
        		jtfint3.setText("ResultatsClustering.csv");
        		jtfint4.setText("RI_RC.csv");
        		deb.setText("1980");
        		fin.setText("2010");
        		tnbpts.setText("5");
        		tray.setText("25000");
        		tseuil1.setText("0.75");
        		tseuil2.setText("10");
        		titer.setText("1");
        		jr4.setSelected(true);
        		tcoord.setText("a01_03_longlatcouple_idc");
        	}
        }
        
	}
	
	 public static void main(String[] args)
	 {       
		 Interface fen = new Interface(uibd);   
	 }
}
