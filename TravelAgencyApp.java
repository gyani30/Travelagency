import javax.swing.*;
import java.awt.*;

public class TravelAgencyApp extends JFrame {
    public CardLayout cards;
    public JPanel cardPanel;
    public DBHelper db;

    // Shared state
    public String selectedMode = "Flight";
    public Destination selectedDestination;
    public Hotel selectedHotel;
    public User currentUser;

    public TravelAgencyApp() {
        setTitle("VoyageLux Travel Agency");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(246, 248, 251)); // #f6f8fb

        db = new DBHelper();
        db.init(); // create tables + sample data

        cards = new CardLayout();
        cardPanel = new JPanel(cards);
        cardPanel.setBackground(new Color(246, 248, 251)); // #f6f8fb

        cardPanel.add(new TravelModePage(this), "travelmode");
        cardPanel.add(new DestinationPage(this), "destination");
        cardPanel.add(new HotelSelectionPage(this), "hotels");
        cardPanel.add(new LoginPage(this), "login");
        cardPanel.add(new PaymentPage(this), "payment");

        add(cardPanel);
    }

    public static void styleButton(AbstractButton b) {
        b.setBackground(new Color(43, 47, 119)); // #2b2f77
        b.setForeground(Color.white);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            TravelAgencyApp app = new TravelAgencyApp();
            app.setVisible(true);
        });
    }
}
