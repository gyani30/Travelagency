import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class DestinationPage extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JLabel imgLabel;
    private Image backgroundImg;

    {
        try {
            backgroundImg = new ImageIcon("images/backgrounds/bg1.jpg").getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }
    }

    public DestinationPage(TravelAgencyApp app) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(new Color(135, 206, 250)); // Sky blue background for destination page

        JLabel header = new JLabel("Destinations");
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setForeground(new Color(38, 50, 95));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Country", "Description"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(217, 223, 242));
        table.getTableHeader().setForeground(new Color(43, 47, 119));
        JScrollPane sp = new JScrollPane(table);

        populateTable(app);

        add(sp, BorderLayout.CENTER);

        imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(320, 200));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(255, 255, 255)); // White shade for image panel
        imgLabel.setBorder(BorderFactory.createLineBorder(new Color(217, 223, 242), 2));
        add(imgLabel, BorderLayout.EAST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(new Color(246, 248, 251));
        JButton back = new JButton("Back"); TravelAgencyApp.styleButton(back);
        addHoverEffect(back);
        back.addActionListener(e -> app.cards.show(app.cardPanel, "travelmode"));
        JButton viewHotels = new JButton("View Hotels"); TravelAgencyApp.styleButton(viewHotels);
        addHoverEffect(viewHotels);
        viewHotels.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a destination first.", "Select", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            app.selectedDestination = app.db.getDestinationById(id);
            // Show image for selected destination
            if (app.selectedDestination != null && app.selectedDestination.imagePath != null) {
                java.io.File f = new java.io.File(app.selectedDestination.imagePath);
                if (f.exists()) {
                    ImageIcon icon = new ImageIcon(app.selectedDestination.imagePath);
                    Image img = icon.getImage().getScaledInstance(320, 200, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                    imgLabel.setText("");
                } else {
                    imgLabel.setIcon(null);
                    imgLabel.setText("[Add image]");
                }
            }
            app.cards.show(app.cardPanel, "hotels");
        });
        buttons.add(back); buttons.add(viewHotels);
        add(buttons, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                Destination d = app.db.getDestinationById(id);
                if (d != null && d.imagePath != null) {
                    java.io.File f = new java.io.File(d.imagePath);
                    if (f.exists()) {
                        ImageIcon icon = new ImageIcon(d.imagePath);
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

    private void populateTable(TravelAgencyApp app) {
        model.setRowCount(0);
        for (Destination d : app.db.getAllDestinations()) {
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
            java.io.File f = new java.io.File(d.imagePath);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(d.imagePath);
                Image scaled = icon.getImage().getScaledInstance(320, 200, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
                imgLabel.setText("");
            } else {
                imgLabel.setText("[Add image]");
            }
            JLabel nameLabel = new JLabel(d.name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            nameLabel.setForeground(new Color(43, 47, 119));
            panel.add(imgLabel, BorderLayout.CENTER);
            panel.add(nameLabel, BorderLayout.SOUTH);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    app.selectedDestination = d;
                    app.cards.show(app.cardPanel, "hotels");
                }
            });
            model.addRow(new Object[]{panel});
        }
    }
}
