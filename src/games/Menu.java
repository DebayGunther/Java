package games;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
 
public class Menu extends JFrame{
 

  private JButton bouton = new JButton("Snake");
  private JButton bouton2 = new JButton("Pacman");
  private JPanel container = new JPanel();
  
  public Menu(){
    this.setTitle("Mini jeux");
    this.setSize(300, 300);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
 
    container.setBackground(Color.white);
    container.setLayout(new BorderLayout());

        

    bouton.addActionListener(new BoutonListener());
    bouton2.addActionListener(new Bouton2Listener());
        
    JPanel south = new JPanel();
    south.add(bouton);
    south.add(bouton2);
    container.add(south, BorderLayout.SOUTH);
    Font police = new Font("Tahoma", Font.BOLD, 16);
    this.setContentPane(container);
    this.setVisible(true);

  }
      
  public static void main (String[] args){
	   		new Menu();
	   }
      

  class BoutonListener implements ActionListener{

    public void actionPerformed(ActionEvent arg0) {
    	new Snake();
    }
  }
      

  class Bouton2Listener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
     new PacMan();   
    }
  }      
}