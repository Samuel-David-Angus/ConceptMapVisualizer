import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame jframe = new JFrame("Concept Map Visualizer");
        jframe.setSize(1000, 800);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Canvas canvas = new Canvas();
        jframe.add(canvas);

        jframe.setVisible(true);

        canvas.showInstructionPopUp();


    }

}