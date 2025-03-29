
import javax.swing.*;

public class App {

    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Mocking Bird");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MockingBird mockingBird = new MockingBird();
        frame.add(mockingBird);
        frame.pack();
        mockingBird.requestFocus();
        frame.setVisible(true);
    }
}
