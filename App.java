import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception{
        int rowCount = 21;
        int colCount = 19;
        int tileSize = 32; //pixels 32 x 32
        int boardWidth = colCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Leeroy's PacMan");
        PacMan pacMan = new PacMan();
        ImageIcon myLogo = new ImageIcon("src/img.png");
        frame.setIconImage(myLogo.getImage());
        frame.add(pacMan);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pacMan.requestFocus();
        frame.setVisible(true);

    }
}
