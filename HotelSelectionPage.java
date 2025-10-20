import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class HotelSelectionPage extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JLabel titleLabel;
    private JLabel imgLabel;
    private Image backgroundImg;

    {
        try {
            backgroundImg = new ImageIcon("images/backgrounds/bg1.jpg").getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }
    }

    public HotelSelectionPage(TravelAgencyApp app) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(135, 206, 250)); // Sky blue background for hotel selection page

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        titleLabel = new JLabel("Hotels");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(38, 50, 95));
        head.add(titleLabel, BorderLayout.WEST);
        add(head, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Hotel Name", "Rating", "Price/Night"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(217, 223, 242));
        table.getTableHeader().setForeground(new Color(43, 47, 119));
        add(new JScrollPane(table), BorderLayout.CENTER);

        imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(320, 200));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(255, 255, 255)); // White shade for image panel
        imgLabel.setBorder(BorderFactory.createLineBorder(new Color(217, 223, 242), 2));
        add(imgLabel, BorderLayout.EAST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(new Color(246, 248, 251));
        JButton back = new JButton("Back");
        TravelAgencyApp.styleButton(back);
        addHoverEffect(back);
        back.addActionListener(e -> app.cards.show(app.cardPanel, "destination"));
        JButton login = new JButton("Login to Book");
        TravelAgencyApp.styleButton(login);
        addHoverEffect(login);
        login.addActionListener(e -> app.cards.show(app.cardPanel, "login"));
        JButton proceed = new JButton("Proceed to Payment");
        TravelAgencyApp.styleButton(proceed);
        addHoverEffect(proceed);
        proceed.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a hotel first.", "Select", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            app.selectedHotel = app.db.getHotelById(id);
            // Show image for selected hotel
            if (app.selectedHotel != null && app.selectedHotel.imagePath != null) {
                java.io.File f = new java.io.File(app.selectedHotel.imagePath);
                if (f.exists()) {
                    imgLabel.setIcon(new ImageIcon(app.selectedHotel.imagePath));
                    imgLabel.setText("");
                } else {
                    imgLabel.setIcon(null);
                    imgLabel.setText("[Add image]");
                }
            }
            if (app.currentUser == null) {
                int opt = JOptionPane.showConfirmDialog(this, "You need to login to continue. Go to login?", "Login required", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) app.cards.show(app.cardPanel, "login");
                return;
            }
            app.cards.show(app.cardPanel, "payment");
        });
        buttons.add(back);
        buttons.add(login);
        buttons.add(proceed);
        add(buttons, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                Hotel h = app.db.getHotelById(id);
                if (h != null && h.imagePath != null) {
                    java.io.File f = new java.io.File(h.imagePath);
                    if (f.exists()) {
                        ImageIcon icon = new ImageIcon(h.imagePath);
                        Image img = icon.getImage().getScaledInstance(320, 200, Image.SCALE_SMOOTH);
                        imgLabel.setIcon(new ImageIcon(img));
                        imgLabel.setText("");
                    } else {
                        imgLabel.setIcon(null);
                        imgLabel.setText("[Add image]");
                    }
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshForDestination(app);
            }
        });
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

    private void refreshForDestination(TravelAgencyApp app) {
        if (app.selectedDestination == null) {
            titleLabel.setText("Hotels - (No destination selected)");
            model.setRowCount(0);
            imgLabel.setIcon(null);
            imgLabel.setText("");
            return;
        }
        titleLabel.setText("Hotels in " + app.selectedDestination.name + ", " + app.selectedDestination.country + " — Mode: " + app.selectedMode);
        model.setRowCount(0);
        for (Hotel h : app.db.getHotelsByDestination(app.selectedDestination.id)) {
            JPanel panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    setOpaque(false);
                }
            };
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(217, 223, 242), 2),
                new EmptyBorder(10, 10, 10, 10)));
            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setOpaque(false);
            java.io.File f = new java.io.File(h.imagePath);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(h.imagePath);
                Image scaled = icon.getImage().getScaledInstance(320, 200, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
                imgLabel.setText("");
            } else {
                imgLabel.setText("[Add image]");
            }
            JLabel nameLabel = new JLabel(h.name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            nameLabel.setForeground(new Color(43, 47, 119));
            nameLabel.setOpaque(true);
            nameLabel.setBackground(new Color(255,255,255,180));
            nameLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
            panel.add(imgLabel, BorderLayout.CENTER);
            panel.add(nameLabel, BorderLayout.SOUTH);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    app.selectedHotel = h;
                    app.cards.show(app.cardPanel, "login");
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    nameLabel.setText(h.name + " | $" + h.pricePerNight);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    nameLabel.setText(h.name);
                }
            });
            model.addRow(new Object[]{panel});
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
