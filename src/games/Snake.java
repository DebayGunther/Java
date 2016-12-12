
package games;

import javax.swing.JFrame;


public class Snake extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Snake() {

        add(new BoardSnake());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 340);
        setLocationRelativeTo(null);
        setTitle("Snake");

        setResizable(false);
        setVisible(true);
    }

}