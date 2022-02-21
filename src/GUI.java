import javax.swing.*;

public class GUI extends JFrame{
    private JPanel mainPanel;
    private JTable table1;

    public GUI() {
        this.setContentPane(mainPanel);
        this.setVisible(true);
        this.setSize(1000,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
