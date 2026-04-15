import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginUI();
        });
    }
}
