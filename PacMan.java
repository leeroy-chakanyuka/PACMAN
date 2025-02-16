import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int w;
        int h;
        Image img;
        int initX;
        int initY;

// by default the pac man looks up

        char direction = 'U'; // U - D - L - R
        int velocityX = 0;
        int velocityY = 0;

        public Block(Image img, int x, int y, int w, int h) {
            this.img = img;
            this.x = x;
            this.y = y; // in pixel
            this.w = w;
            this.h = h;

            this.initX = x;
            this.initY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -(tileSize / 4);
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -(tileSize / 4);
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.initX;
            this.y = this.initY;
        }
    }

    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32; // pixels 32 x 32
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhost;
    private Image orangeGhost;
    private Image pinkGhost;
    private Image redGhost;

    private Image pacUp;
    private Image pacDown;
    private Image pacLeft;
    private Image pacRight;

    // X = wallImage, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls; // literally identical to an arrayList in any other way, wrapping our container in a block object
    HashSet<Block> seeds;
    HashSet<Block> ghosts;
    Block paccy;
    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    public PacMan() {
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(new Color(154, 29, 213));
        addKeyListener(this);
        setFocusable(true);

        // Loading the images using getClass().getResource()
        this.pacRight = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();
        this.pacUp = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
        this.pacDown = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();
        this.pacLeft = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
        this.blueGhost = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();
        this.redGhost = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();
        this.orangeGhost = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();
        this.pinkGhost = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
        this.wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this); //listen for change every 50ms, check implemented method
        gameLoop.start(); // will constantly redraw
        System.out.println("Walls: " + walls.size());
        System.out.println("Seeds: " + seeds.size());
        System.out.println("Ghosts: " + ghosts.size());
    }

    public void loadMap() {
        // Init everything
        this.ghosts = new HashSet<>();
        this.seeds = new HashSet<>();
        this.walls = new HashSet<>();

        for (int rows = 0; rows < this.rowCount; rows++) {
            for (int col = 0; col < colCount; col++) {
                String row = tileMap[rows];
                char tileMapChar = row.charAt(col); // where is the current char

                // Getting the actual Pixel value, remember (0,0) is top left
                int currentCoordX = col * tileSize;
                int currentCoordY = rows * tileSize;

                if (tileMapChar == 'X') { // you hit a wallImage / border
                    Block wall = new Block(this.wallImage, currentCoordX, currentCoordY, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {
                    Block ghost = new Block(this.blueGhost, currentCoordX, currentCoordY, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(this.orangeGhost, currentCoordX, currentCoordY, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(this.pinkGhost, currentCoordX, currentCoordY, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(this.redGhost, currentCoordX, currentCoordY, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    paccy = new Block(this.pacRight, currentCoordX, currentCoordY, tileSize, tileSize);
                } else if (tileMapChar == ' ') { // seed
                    Block seed = new Block(null, currentCoordX + 14, currentCoordY + 14, 4, 4); // 4 x 4 pixels
                    seeds.add(seed);
                }
            }
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.w &&
                a.x + a.w > b.x &&
                a.y < b.y + b.h &&
                a.y + a.h > b.y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // method is inherited
        draw(g);
    }

    public void draw(Graphics g) {
        if (paccy != null && paccy.img != null) {
            g.drawImage(paccy.img, paccy.x, paccy.y, paccy.w, paccy.h, null);
            for (Block ghost : ghosts) {
                g.drawImage(ghost.img, ghost.x, ghost.y, ghost.w, ghost.h, null);
            }
            for (Block wall : walls) {
                g.drawImage(wall.img, wall.x, wall.y, wall.w, wall.h, null);
            }
            g.setColor(Color.white);
            for (Block seed : seeds) {
                g.fillRect(seed.x, seed.y, seed.w, seed.h);
            }
            // Draw score and lives
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            if (gameOver) {
                g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
            } else {
                g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
            }
        } else {
            System.out.println("paccy or paccy image is null");
        }
    }

    public void move() {
        paccy.x += paccy.velocityX;
        paccy.y += paccy.velocityY;

        // Check wall collisions
        for (Block wall : walls) {
            if (collision(paccy, wall)) {
                paccy.x -= paccy.velocityX;
                paccy.y -= paccy.velocityY;
                break;
            }
        }

        // Check ghost collisions
        for (Block ghost : ghosts) {
            if (collision(ghost, paccy)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.w >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // Check seed collision
        Block seedEaten = null;
        for (Block seed : seeds) {
            if (collision(paccy, seed)) {
                seedEaten = seed;
                score += 10;
            }
        }
        seeds.remove(seedEaten);

        if (seeds.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public void resetPositions() {
        paccy.reset();
        paccy.velocityX = 0;
        paccy.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            paccy.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            paccy.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            paccy.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            paccy.updateDirection('R');
        }

        if (paccy.direction == 'U') {
            paccy.img = pacUp;
        } else if (paccy.direction == 'D') {
            paccy.img = pacDown;
        } else if (paccy.direction == 'L') {
            paccy.img = pacLeft;
        } else if (paccy.direction == 'R') {
            paccy.img = pacRight;
        }
    }
}