
package games;

import javax.swing.JFrame;

import games.BoardPacMan;


public class PacMan extends JFrame
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public PacMan()
  {
    add(new BoardPacMan());
    setTitle("Pacman");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(380, 420);
    setLocationRelativeTo(null);
    setVisible(true);
  }

}