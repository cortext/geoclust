import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

//ho
public class InterfaceBD extends JFrame 
{
	JPanel pan = new JPanel();
	JPanel pan1 = new JPanel();
	JPanel pan2 = new JPanel();
	InterfaceBD ui;
	
	JButton bouton2 = new JButton("Connecter");

	JLabel label0 = new JLabel("Paramàtres de connexion");

	public JLabel serveur = new JLabel("Serveur");
	public JLabel user = new JLabel("Utilisateur");
	public JLabel pw= new JLabel("Mot de passe");
	public JLabel bd = new JLabel("Base de données");
	public JLabel input_type = new JLabel("Input type");
	
    JRadioButton defaut = new JRadioButton("Defaut");
    
    String[] petStrings = { "csv", "database"}; //liste pour le combobox 
    JComboBox<String> comboBoxInputType = new JComboBox(petStrings); //combobox
 
    ButtonGroup bg1 = new ButtonGroup();

    JTextField tserver = new JTextField();
    JTextField tuser = new JTextField();
    JTextField tpw = new JTextField();
    JTextField tbd = new JTextField();
    
	public InterfaceBD()
	{
		Color col = new Color(250, 250, 210);
		
		ui = this;
		this.setTitle("Test Collections Generator");
        this.setSize(500, 300);
        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pan.setBackground(col);
        pan1.setBackground(Color.getHSBColor(10, 10, 50));
        

        defaut.addActionListener(new RadioDef());
        comboBoxInputType.addActionListener(new RadioDef());
       
        Font f1 = new Font("Arial", Font.BOLD, 12);
        tuser.setFont(f1);
        tuser.setForeground(Color.BLUE);
        tpw.setForeground(Color.BLUE);
        tpw.setFont(f1);
        tserver.setForeground(Color.BLUE);
        tserver.setFont(f1);
        tbd.setForeground(Color.BLUE);
        tbd.setFont(f1);
        
        Font f2 = new Font("Arial", Font.BOLD, 14);
        label0.setFont(f2);
        label0.setForeground(Color.RED);

        bouton2.addActionListener(new Connect());
        
        bg1.add(defaut);
        
        /*------------------------Ajout de input type ---------------------------------*/
        pan2.setBackground(Color.getHSBColor(10, 10, 50));// avoir le meme background
        comboBoxInputType.setBackground(Color.getHSBColor(10, 10, 50));
        
        GridLayout gl2 = new GridLayout(1, 2); 
        pan2.setLayout(gl2);
        pan2.add(input_type);
        pan2.add(comboBoxInputType);
        /*--------------------------------------------------------------------------- */
        
        defaut.setBackground(Color.getHSBColor(10, 10, 50));
        
                
        GridLayout gl1 = new GridLayout (7, 2, 40, 15);       
        
        pan1.setLayout(gl1);
        pan1.add(defaut);      		pan1.add(pan2);      	
        pan1.add(label0);			pan1.add(new JLabel(" "));			
        pan1.add(serveur);			pan1.add(pw);	
        pan1.add(tserver);			pan1.add(tpw);
        pan1.add(user);				pan1.add(bd);
        pan1.add(tuser);			pan1.add(tbd);
        pan1.add(new JLabel(" "));	pan1.add(bouton2);
        
        this.setContentPane(pan1);
        this.setVisible(true);
	}
	
	class RadioDef implements ActionListener
	{	 
        public void actionPerformed(ActionEvent e) 
        {
        	if(defaut.isSelected())
        	{
        		tserver.setText("localhost");
        		tuser.setText("root");
        		tpw.setText("michel");
        		tbd.setText("test");
        	}
        }
	}
	
	
	public class Connect implements ActionListener 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
			if(ui.comboBoxInputType.getSelectedItem().toString().equals("csv")){
				new InterfaceInputCsv(ui);
				
			}
			else{
				new InterfaceInputDatabase(ui);
			}
			ui.setVisible(false);
	    }
	}
	
	
	
	 public static void main(String[] args)
	 {       
		 InterfaceBD fen = new InterfaceBD();   
	 }
	 
}
