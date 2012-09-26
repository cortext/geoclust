import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class InterfaceBD extends JFrame 
{
	JPanel pan = new JPanel();
	JPanel pan1 = new JPanel();
	InterfaceBD ui;
	
	JButton bouton2 = new JButton("Connecter");

	JLabel label0 = new JLabel("Paramètres de connexion");

	public JLabel serveur = new JLabel("Serveur");
	public JLabel user = new JLabel("Utilisateur");
	public JLabel pw= new JLabel("Mot de passe");
	public JLabel bd = new JLabel("Base de données");
	
    JRadioButton defaut = new JRadioButton("Defaut");
    JRadioButton param = new JRadioButton("Parametrable");
 
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
        param.addActionListener(new RadioDef());
       
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
        bg1.add(param);
     
        defaut.setBackground(Color.getHSBColor(10, 10, 50));
        param.setBackground(Color.getHSBColor(10, 10, 50));
                
        GridLayout gl1 = new GridLayout (7, 2, 40, 15);       
        
        pan1.setLayout(gl1);
        pan1.add(defaut);      		pan1.add(param);      	
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
        		tuser.setText("mirema");
        		tpw.setText("michel");
        		tbd.setText("test");
        	}
        }
	}
	
	
	public class Connect implements ActionListener 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
			Interface interf = new Interface(ui);
			ui.setVisible(false);
	    }
	}
	
	
	
	 public static void main(String[] args)
	 {       
		 InterfaceBD fen = new InterfaceBD();   
	 }

}
