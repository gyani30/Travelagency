import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class TravelModePage extends JPanel {
    private JPanel[] modeCards = new JPanel[3];
    private String[] modes = {"Flight", "Train", "Car"};
    private String[] descs = {"Fly comfortably and fast", "Relaxed scenic journeys", "Flexible road trips"};
    private String[] imgPaths = {"images/travel_modes/flight.jpg", "images/travel_modes/train.jpg", "images/travel_modes/car.jpg"};
    private JLabel[] imgLabels = new JLabel[3];
    private int selectedIdx = 0;
    private Image backgroundImg;
    {
        try {
            backgroundImg = new ImageIcon("images/backgrounds/bg1.jpg").getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }
    }

    public TravelModePage(TravelAgencyApp app) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(new Color(135, 206, 250)); // Sky blue background for travel mode page

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Travel Desk");
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(43, 47, 119));
        header.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Choose your travel mode — travel in style");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(85, 90, 138));
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 20, 20));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(40, 40, 40, 40));
        for (int i = 0; i < 3; i++) {
            modeCards[i] = createModeCard(app, i);
            center.add(modeCards[i]);
        }
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton deselect = new JButton("Deselect");
        TravelAgencyApp.styleButton(deselect);
        addHoverEffect(deselect);
        deselect.addActionListener(e -> {
            for (int i = 0; i < 3; i++) modeCards[i].setBackground(new Color(255,255,255,180));
            app.selectedMode = null;
            selectedIdx = -1;
        });
        JButton next = new JButton("Next — Destinations");
        TravelAgencyApp.styleButton(next);
        addHoverEffect(next);
        next.addActionListener(e -> {
            app.cards.show(app.cardPanel, "destination");
        });
        footer.add(deselect);
        footer.add(next);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createModeCard(TravelAgencyApp app, int idx) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(217, 223, 242), 2),
                new EmptyBorder(10, 10, 10, 10)));
        card.setBackground(idx == selectedIdx ? new Color(238, 243, 255, 180) : new Color(255,255,255,180));
        JLabel m = new JLabel(modes[idx], SwingConstants.CENTER);
        m.setFont(new Font("SansSerif", Font.BOLD, 22));
        m.setForeground(new Color(43, 47, 119));
        m.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.add(m, BorderLayout.NORTH);
        JLabel d = new JLabel("<html><center>" + descs[idx] + "</center></html>", SwingConstants.CENTER);
        d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        d.setForeground(new Color(102, 106, 144));
        card.add(d, BorderLayout.SOUTH);
        JLabel img = new JLabel();
        img.setPreferredSize(new Dimension(1, 1));
        img.setOpaque(false);
        java.io.File f = new java.io.File(imgPaths[idx]);
        if (f.exists()) {
            ImageIcon icon = new ImageIcon(imgPaths[idx]);
            int w = 0, h = 0;
            if (icon.getIconWidth() > icon.getIconHeight()) {
                w = 320; h = 200;
            } else {
                w = 200; h = 320;
            }
            Image scaled = icon.getImage().getScaledInstance(card.getWidth(), card.getHeight(), Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaled));
            img.setText("");
        } else {
            img.setText("[Add image]");
        }
        img.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(img, BorderLayout.CENTER);
        imgLabels[idx] = img;
        JButton select = new JButton("Select");
        TravelAgencyApp.styleButton(select);
        addHoverEffect(select);
        select.addActionListener(ev -> {
            for (int i = 0; i < 3; i++) modeCards[i].setBackground(new Color(255,255,255,180));
            card.setBackground(new Color(238, 243, 255, 180));
            app.selectedMode = modes[idx];
            selectedIdx = idx;
        });
        JPanel bp = new JPanel(); bp.setOpaque(false); bp.add(select);
        card.add(bp, BorderLayout.EAST);
        return card;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
