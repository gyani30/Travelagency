import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class PaymentPage extends JPanel {
    private JLabel summary;
    private JTextField cardField;
    private Image backgroundImg;

    {
        try {
            backgroundImg = new ImageIcon("images/backgrounds/bg1.jpg").getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }
    }

    public PaymentPage(TravelAgencyApp app) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(135, 206, 250)); // Sky blue background for payment page

        JLabel header = new JLabel("Payment"); 
        header.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        header.setForeground(new Color(38, 50, 95));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); 
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(10,10,10,10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        summary = new JLabel(); 
        summary.setFont(new Font("SansSerif", Font.PLAIN, 16)); 
        summary.setForeground(new Color(43, 47, 119));
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2; 
        center.add(summary, gbc);

        gbc.gridwidth = 1; 
        gbc.gridy = 1; 
        gbc.gridx = 0; 
        center.add(new JLabel("Card Number:"), gbc);
        cardField = new JTextField(18); 
        cardField.setFont(new Font("SansSerif", Font.PLAIN, 16)); 
        gbc.gridx = 1; 
        center.add(cardField, gbc);

        gbc.gridx = 0; 
        gbc.gridy = 2; 
        gbc.gridwidth = 2;
        JButton pay = new JButton("Pay Now"); 
        TravelAgencyApp.styleButton(pay); 
        addHoverEffect(pay);
        pay.addActionListener(e -> doPayment(app));
        center.add(pay, gbc);

        JButton back = new JButton("Back to Hotels"); 
        TravelAgencyApp.styleButton(back); 
        addHoverEffect(back);
        back.addActionListener(e -> app.cards.show(app.cardPanel, "hotels"));
        gbc.gridy = 3; 
        center.add(back, gbc);

        add(center, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshSummary(app);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(64, 162, 255)); // Modern blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.white);
            }
        });
    }

    private void refreshSummary(TravelAgencyApp app) {
        if (app.selectedHotel == null || app.selectedDestination == null) {
            summary.setText("No booking selected.");
            return;
        }
        String text = String.format("Booking: %s in %s (%s) — Price: %.2f per night\nUser: %s\nMode: %s",
                app.selectedHotel.name, app.selectedDestination.name, app.selectedDestination.country,
                app.selectedHotel.pricePerNight, (app.currentUser==null?"(guest)":app.currentUser.username), app.selectedMode);
        summary.setText("<html>" + text.replaceAll("\n","<br>") + "</html>");
    }

    private void doPayment(TravelAgencyApp app) {
        String card = cardField.getText().trim();
        if (card.length() < 12) { 
            JOptionPane.showMessageDialog(this, "Enter a valid card number (simulated)."); 
            return; 
        }
        if (app.currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login before booking.");
            app.cards.show(app.cardPanel, "login");
            return;
        }
        app.db.createBooking(app.currentUser.id, app.selectedDestination.id, app.selectedHotel.id, app.selectedMode);
        JOptionPane.showMessageDialog(this, "Payment successful! Booking confirmed.");
        app.selectedHotel = null; 
        app.selectedDestination = null;
        app.cards.show(app.cardPanel, "travelmode");
    }
}
