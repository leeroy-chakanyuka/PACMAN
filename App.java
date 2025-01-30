import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception{
        int rowCount = 21;
        int colCount = 19;
        int tileSize = 32; //pixels 32 x 32
        int boardWidth = colCount * tileSize;
        int boardHeight = rowCount * tileSize;

//        JFrame myFrame = new JFrame("Leeroy's Pac Man");
//
////        myFrame.setLocationRelativeTo(null); //pop up in the center
//
//        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //prevent memory leakage
//        myFrame.setResizable(false); // not a flow layout, will not be responsive to dimension change
//        myFrame.setSize(boardWidth, boardHeight);
//
//        PacMan myPaccy = new PacMan();
//        myFrame.add(myPaccy);
//        myFrame.pack(); // makes it fit all the components even without a specified size
//        myFrame.setVisible(true); //display

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
