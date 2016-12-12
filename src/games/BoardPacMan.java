package games;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class BoardPacMan extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dimension d;
    Font smallfont = new Font("Helvetica", Font.BOLD, 14);

    FontMetrics fmsmall, fmlarge;
    Image ii;
    Color dotcolor = new Color(192, 192, 0);
    Color mazecolor;

    boolean ingame = false;
    boolean dying = false;

    final int blocksize = 24;	// taille en pixel d un bloc
    final int nrofblocks = 15; 	//nbr de blocs de gauche a droite
    final int scrsize = nrofblocks * blocksize;	//taille de la fenetre
    final int pacanimdelay = 2; 
    final int pacmananimcount = 4;
    final int maxghosts = 10;	//nbr max de fantomes
    final int pacmanspeed = 6;	//vitesse du pacman

    int pacanimcount = pacanimdelay;
    int pacanimdir = 1;
    int pacmananimpos = 0;
    int nrofghosts = 6;
    int pacsleft, score;  
    int deathcounter;
    int[] dx, dy;
    int[] ghostx, ghosty, ghostdx, ghostdy, ghostspeed;

    Image ghost;
    Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    Image pacman3up, pacman3down, pacman3left, pacman3right;
    Image pacman4up, pacman4down, pacman4left, pacman4right;

    int pacmanx, pacmany, pacmandx, pacmandy;
    int reqdx, reqdy, viewdx, viewdy;

    final short leveldata[] =
    { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
      21, 0,  0,  0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
      21, 0,  0,  0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 
      21, 0,  0,  0,  17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 
      17, 18, 18, 18, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 20,
      17, 16, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 24, 20, 
      25, 16, 16, 16, 24, 24, 28, 0,  25, 24, 24, 16, 20, 0,  21, 
      1,  17, 16, 20, 0,  0,  0,  0,  0,  0,  0,  17, 20, 0,  21,
      1,  17, 16, 16, 18, 18, 22, 0,  19, 18, 18, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 20, 0,  21, 
      1,  17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0,  21,
      1,  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  21,
      1,  25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
      9,  8,  8,  8,  8,  8,  8,  8,  8,  8,  25, 24, 24, 24, 28 };
    //Ces chiffres repr�sentent le labyrinthe. Ils fournissent des informations sur lesquelles nous cr�ons les coins et les points. 
    //Par exemple le num�ro 19 dans le coin sup�rieur gauche signifie que le carr� aura des fronti�res haut et � gauche et un point. (16 + 2 + 1)
    
    final int validspeeds[] = { 1, 2, 3, 4, 6, 8 };
    final int maxspeed = 6;

    int currentspeed = 3;
    short[] screendata;
    Timer timer;


    public BoardPacMan() {

        GetImages();

        addKeyListener(new TAdapter());

        screendata = new short[nrofblocks * nrofblocks];
        mazecolor = new Color(5, 100, 5);
        setFocusable(true);

        d = new Dimension(400, 400);

        setBackground(Color.black);
        setDoubleBuffered(true);

        ghostx = new int[maxghosts];
        ghostdx = new int[maxghosts];
        ghosty = new int[maxghosts];
        ghostdy = new int[maxghosts];
        ghostspeed = new int[maxghosts];
        dx = new int[4];
        dy = new int[4];
        timer = new Timer(40, this);
        timer.start();
    }

    public void addNotify() {
        super.addNotify();
        GameInit();
    }


    public void DoAnim() {
        pacanimcount--;
        if (pacanimcount <= 0) {
            pacanimcount = pacanimdelay;						//variable pacanimdelay, rend l'animation un peu plus lent sinon, le pacman ouvrirait la bouche trop vite.
            pacmananimpos = pacmananimpos + pacanimdir;			//variable pacmananimpos, d�termine quelle image pacman est dessin�e.
            if (pacmananimpos == (pacmananimcount - 1) || pacmananimpos == 0)
                pacanimdir = -pacanimdir;
        }
    }

    public void PlayGame(Graphics2D g2d) {
        if (dying) {
            Death();
        } else {
            MovePacMan();
            DrawPacMan(g2d);
            moveGhosts(g2d);
            CheckMaze();
        }
    }


    public void ShowIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, scrsize / 2 - 30, scrsize - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, scrsize / 2 - 30, scrsize - 100, 50);

        String s = "Recommencer (O/N)";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (scrsize - metr.stringWidth(s)) / 2, scrsize / 2);
    }


    public void DrawScore(Graphics2D g) {
        int i;
        String s;

        g.setFont(smallfont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, scrsize / 2 + 96, scrsize + 16);
        for (i = 0; i < pacsleft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, scrsize + 1, this);
        }
    }


    public void CheckMaze() {
        short i = 0;
        boolean finished = true;

        while (i < nrofblocks * nrofblocks && finished) {	//v�rifie s'il ya des points restants � manger
            if ((screendata[i] & 16) != 0)					//16 repr�sente un point 
                finished = false;
            i++;
        }

        if (finished) {				//si tous les points sont consomm�s, nous passons au niveau suivant.
            score += 50;			//on gagne 50 points de score quand le niveau est fini

            if (nrofghosts < maxghosts)
                nrofghosts++;		//on recr�e les fantomes		
            if (currentspeed < maxspeed)
                currentspeed++;		//on augmente leurs vitesses (jusqu'� la vitesse max)
            LevelInit();
        }
    }

    public void Death() {

        pacsleft--;		//enl�ve une vie au pacman quand il meurt
        if (pacsleft == 0)
            ingame = false;
        LevelContinue();
    }

    public void moveGhosts(Graphics2D g2d) {
        short i;
        int pos;
        int count;

        for (i = 0; i < nrofghosts; i++) {
            if (ghostx[i] % blocksize == 0 && ghosty[i] % blocksize == 0) {				//continue seulement si le fantome a termin� de se d�placer d'une case.
                pos = ghostx[i] / blocksize + nrofblocks * (int)(ghosty[i] / blocksize);//d�termine o� le fant�me se trouve. Dans quelle position / carr�

                count = 0;
                if ((screendata[pos] & 1) == 0 && ghostdx[i] != 1) {	//S'il n'y a pas d'obstacle sur la gauche et le fant�me n'est pas d�j� 
                    dx[count] = -1;										//en mouvement vers la droite, le fant�me se d�place vers la gauche.
                    dy[count] = 0;										//et donc si le fant�me p�n�tre dans un tunnel, il continuera dans 
                    count++;											//la m�me direction jusqu'� ce qu'il soit sorti du tunnel
                }
                if ((screendata[pos] & 2) == 0 && ghostdy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }
                if ((screendata[pos] & 4) == 0 && ghostdx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }
                if ((screendata[pos] & 8) == 0 && ghostdy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }
 
                if (count == 0) {
                    if ((screendata[pos] & 15) == 15) {
                        ghostdx[i] = 0;
                        ghostdy[i] = 0;
                    } else {
                        ghostdx[i] = -ghostdx[i];
                        ghostdy[i] = -ghostdy[i];
                    }
                } else {
                    count = (int)(Math.random() * count);
                    if (count > 3)
                        count = 3;
                    ghostdx[i] = dx[count];
                    ghostdy[i] = dy[count];
                }

            }
            ghostx[i] = ghostx[i] + (ghostdx[i] * ghostspeed[i]);
            ghosty[i] = ghosty[i] + (ghostdy[i] * ghostspeed[i]);
            DrawGhost(g2d, ghostx[i] + 1, ghosty[i] + 1);

            if (pacmanx > (ghostx[i] - 18) && pacmanx < (ghostx[i] + 18) &&		//S'il ya une collision entre un fant�me 
                pacmany > (ghosty[i] - 18) && pacmany < (ghosty[i] + 18) &&		//et un Pacman, le pacman meurt.
                ingame) {														//18 represente une marge de px autour du
            																	//point associ� a l'image.(pr les collisions)
                dying = true;
                deathcounter = 64;

            }
        }
    }


    public void DrawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
    }


    public void MovePacMan() {		// Le reqdx et les variables reqdy sont d�termin�s dans la classe interne TAdapter. 
        int pos;					//Ces variables sont contr�l�es avec les touches fl�ch�es.
        short ch;

        if (reqdx == -pacmandx && reqdy == -pacmandy) {
            pacmandx = reqdx;
            pacmandy = reqdy;
            viewdx = pacmandx;
            viewdy = pacmandy;
        }
        if (pacmanx % blocksize == 0 && pacmany % blocksize == 0) {
            pos =
 pacmanx / blocksize + nrofblocks * (int)(pacmany / blocksize);
            ch = screendata[pos];

            if ((ch & 16) != 0) {
                screendata[pos] = (short)(ch & 15);		//Si le pacman d�place vers une position o� il ya un point
                score++;								//on enleve le point du labyrinthe et on augmente la valeur du score.
            }

            if (reqdx != 0 || reqdy != 0) {
                if (!((reqdx == -1 && reqdy == 0 && (ch & 1) != 0) ||
                      (reqdx == 1 && reqdy == 0 && (ch & 4) != 0) ||
                      (reqdx == 0 && reqdy == -1 && (ch & 2) != 0) ||
                      (reqdx == 0 && reqdy == 1 && (ch & 8) != 0))) {
                    pacmandx = reqdx;
                    pacmandy = reqdy; 		//Si le pacman ne peut pas aller plus loin dans sa direction actuelle, il y a un arr�t.
                    viewdx = pacmandx;
                    viewdy = pacmandy;
                }
            }

            if ((pacmandx == -1 && pacmandy == 0 && (ch & 1) != 0) ||
                (pacmandx == 1 && pacmandy == 0 && (ch & 4) != 0) ||
                (pacmandx == 0 && pacmandy == -1 && (ch & 2) != 0) ||
                (pacmandx == 0 && pacmandy == 1 && (ch & 8) != 0)) {
                pacmandx = 0;
                pacmandy = 0;
            }
        }
        pacmanx = pacmanx + pacmanspeed * pacmandx;
        pacmany = pacmany + pacmanspeed * pacmandy;
    }
    //Il ya quatre directions possibles pour un pacman. Il ya quatre images pour toutes les directions.
    //Les images sont utilis�es pour animer les ouvertures et fermetures de la bouche du pacman.
    
    public void DrawPacMan(Graphics2D g2d) {
        if (viewdx == -1)
            DrawPacManLeft(g2d);
        else if (viewdx == 1)
            DrawPacManRight(g2d);
        else if (viewdy == -1)
            DrawPacManUp(g2d);
        else
            DrawPacManDown(g2d);
    }

    public void DrawPacManUp(Graphics2D g2d) {
        switch (pacmananimpos) {
        case 1:
            g2d.drawImage(pacman2up, pacmanx + 1, pacmany + 1, this);
            break;
        case 2:
            g2d.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
            break;
        case 3:
            g2d.drawImage(pacman4up, pacmanx + 1, pacmany + 1, this);
            break;
        default:
            g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
            break;
        }
    }


    public void DrawPacManDown(Graphics2D g2d) {
        switch (pacmananimpos) {
        case 1:
            g2d.drawImage(pacman2down, pacmanx + 1, pacmany + 1, this);
            break;
        case 2:
            g2d.drawImage(pacman3down, pacmanx + 1, pacmany + 1, this);
            break;
        case 3:
            g2d.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
            break;
        default:
            g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
            break;
        }
    }


    public void DrawPacManLeft(Graphics2D g2d) {
        switch (pacmananimpos) {
        case 1:
            g2d.drawImage(pacman2left, pacmanx + 1, pacmany + 1, this);
            break;
        case 2:
            g2d.drawImage(pacman3left, pacmanx + 1, pacmany + 1, this);
            break;
        case 3:
            g2d.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
            break;
        default:
            g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
            break;
        }
    }


    public void DrawPacManRight(Graphics2D g2d) {
        switch (pacmananimpos) {
        case 1:
            g2d.drawImage(pacman2right, pacmanx + 1, pacmany + 1, this);
            break;
        case 2:
            g2d.drawImage(pacman3right, pacmanx + 1, pacmany + 1, this);
            break;
        case 3:
            g2d.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
            break;
        default:
            g2d.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
            break;
        }
    }


    public void DrawMaze(Graphics2D g2d) {		// dessine le labyrinthe sur base des chiffres dans le tableau de screendata
        short i = 0;
        int x, y;
     
        for (y = 0; y < scrsize; y += blocksize) {
            for (x = 0; x < scrsize; x += blocksize) {
                g2d.setColor(mazecolor);
                g2d.setStroke(new BasicStroke(2));

                if ((screendata[i] & 1) != 0) // Le num�ro 1 est une fronti�re gauche
                {
                    g2d.drawLine(x, y, x, y + blocksize - 1);
                }
                if ((screendata[i] & 2) != 0) // 2 est une bordure sup�rieure
                {
                    g2d.drawLine(x, y, x + blocksize - 1, y);
                }
                if ((screendata[i] & 4) != 0) // 4 est une fronti�re droite
                {
                    g2d.drawLine(x + blocksize - 1, y, x + blocksize - 1,
                                 y + blocksize - 1);
                }
                if ((screendata[i] & 8) != 0) // 8 est une fronti�re de fond
                {
                    g2d.drawLine(x, y + blocksize - 1, x + blocksize - 1,
                                 y + blocksize - 1);
                }
                if ((screendata[i] & 16) != 0) // 16 est un point
                {
                    g2d.setColor(dotcolor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }
                i++;
            }	//  Par exemple, nous avons 9 dans le tableau de screendata. Nous avons le premier bit (1) et le quatri�me bit (8) ensemble. 
        }		//	Donc, nous attirons un fond et une bordure gauche sur cette place particuli�re.
    }

    public void GameInit() {
        pacsleft = 3;		//initialise le nbre de vie du pacman � 3
        score = 0;			//initialise le score � 0
        LevelInit();
        nrofghosts = 6;		//initialise le nbre de fantome � 6
        currentspeed = 3;	//initialise la vitesse � 3
    }


    public void LevelInit() {
        int i;
        for (i = 0; i < nrofblocks * nrofblocks; i++)
            screendata[i] = leveldata[i];

        LevelContinue();
    }


    public void LevelContinue() {
        short i;
        int dx = 1;
        int random;

        for (i = 0; i < nrofghosts; i++) {
            ghosty[i] = 4 * blocksize;  //position de d�part des fantomes
            ghostx[i] = 4 * blocksize;
            ghostdy[i] = 0;
            ghostdx[i] = dx;
            dx = -dx;
            random = (int)(Math.random() * (currentspeed)); //vitesse aleatoire pr chaque fantome
            if (random > currentspeed)					 	//verifie que la vitesse al�atoire ne d�passe pas la limite
                random = currentspeed;
            ghostspeed[i] = validspeeds[random];
            
   
        }

        pacmanx = 7 * blocksize;		//position de depart du pacman
        pacmany = 11 * blocksize;
        pacmandx = 0;
        pacmandy = 0;
        reqdx = 0;
        reqdy = 0;
        viewdx = -1;
        viewdy = 0;
        dying = false;
    }

    public void GetImages()		//images des fantomes et des diff�rentes position du pacman
    {	

      ghost = new ImageIcon("ghost.png").getImage();
      pacman1 = new ImageIcon("pacman.png").getImage();
      pacman2up = new ImageIcon("up1.png").getImage();
      pacman3up = new ImageIcon("up2.png").getImage();
      pacman4up = new ImageIcon("up3.png").getImage();
      pacman2down = new ImageIcon("down1.png").getImage();
      pacman3down = new ImageIcon("down2.png").getImage();
      pacman4down = new ImageIcon("down3.png").getImage();
      pacman2left = new ImageIcon("left1.png").getImage();
      pacman3left = new ImageIcon("left2.png").getImage();
      pacman4left = new ImageIcon("left3.png").getImage();
      pacman2right = new ImageIcon("right1.png").getImage();
      pacman3right = new ImageIcon("right2.png").getImage();
      pacman4right = new ImageIcon("right3.png").getImage();

    }

    public void paint(Graphics g)
    {
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;

      g2d.setColor(Color.black);		//couleur de fond du jeu
      g2d.fillRect(0, 0, d.width, d.height);

      DrawMaze(g2d);
      DrawScore(g2d);
      DoAnim();
      if (ingame)
        PlayGame(g2d);
      else
        ShowIntroScreen(g2d);

      g.drawImage(ii, 5, 5, this);
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

          int key = e.getKeyCode();

          if (ingame)
          {
            if (key == KeyEvent.VK_LEFT) //fait aller le pacman a gauche en appuyant sur la fleche de gauche ou le chiffe 4 etc
            {
              reqdx=-1;
              reqdy=0;
            }
            else if (key == KeyEvent.VK_RIGHT)
            {
              reqdx=1;
              reqdy=0;
            }
            else if (key == KeyEvent.VK_UP)
            {
              reqdx=0;
              reqdy=-1;
            }
            else if (key == KeyEvent.VK_DOWN)
            {
              reqdx=0;
              reqdy=1;
            }
            else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) //annule la partie en appuyant sur escape
            {
              ingame=false;
            }
            else if (key == KeyEvent.VK_PAUSE) {		// met le jeu en pause en appuyant sur pause
                if (timer.isRunning())
                    timer.stop();
                else timer.start();
            }
          }
          else
          {
            if (key == 'o' || key == 'O')				//demarre le jeu en appuyant sur s
          {
              ingame=true;
              GameInit();
            }
            if (key == 'n' || key == 'N')				//demarre le jeu en appuyant sur s
          {
              ingame=true;
              System.exit(0);
            }
          }
      }

          public void keyReleased(KeyEvent e) {
              int key = e.getKeyCode();

              if (key == Event.LEFT || key == Event.RIGHT || 
                 key == Event.UP ||  key == Event.DOWN)
              {
                reqdx=0;
                reqdy=0;
              }
          }
      }

    public void actionPerformed(ActionEvent e) {
        repaint();  
    }
}