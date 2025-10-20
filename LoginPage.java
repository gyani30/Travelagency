import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JPanel {
    private JTextField tfUser;
    private JPasswordField pfPass;
    private Image backgroundImg;

    {
        try {
            backgroundImg = new ImageIcon("images/backgrounds/bg1.jpg").getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }
    }

    public LoginPage(TravelAgencyApp app) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(135, 206, 250)); // Sky blue background for login page
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Login or Register");
        header.setFont(new Font("SansSerif", Font.BOLD, 22)); 
        header.setForeground(new Color(38, 50, 95));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(header, gbc);

        JLabel user = new JLabel("Username:"); 
        user.setForeground(new Color(43, 47, 119)); 
        gbc.gridy = 1; gbc.gridwidth = 1; add(user, gbc);
        tfUser = new JTextField(20); 
        tfUser.setFont(new Font("SansSerif", Font.PLAIN, 16)); 
        gbc.gridx = 1; add(tfUser, gbc);

        JLabel pass = new JLabel("Password:"); 
        pass.setForeground(new Color(43, 47, 119)); 
        gbc.gridx = 0; gbc.gridy = 2; add(pass, gbc);
        pfPass = new JPasswordField(20); 
        pfPass.setFont(new Font("SansSerif", Font.PLAIN, 16)); 
        gbc.gridx = 1; add(pfPass, gbc);

        JButton btnLogin = new JButton("Login"); 
        TravelAgencyApp.styleButton(btnLogin);
        addHoverEffect(btnLogin);
        gbc.gridx = 0; gbc.gridy = 3; btnLogin.addActionListener(e -> doLogin(app)); add(btnLogin, gbc);

        JButton btnRegister = new JButton("Register"); 
        TravelAgencyApp.styleButton(btnRegister);
        addHoverEffect(btnRegister);
        gbc.gridx = 1; btnRegister.addActionListener(e -> doRegister(app)); add(btnRegister, gbc);

        JButton btnBack = new JButton("Back"); 
        TravelAgencyApp.styleButton(btnBack);
        addHoverEffect(btnBack);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; btnBack.addActionListener(e -> app.cards.show(app.cardPanel, "hotels")); add(btnBack, gbc);
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

    private void doLogin(TravelAgencyApp app) {
        String u = tfUser.getText().trim();
        String p = new String(pfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username and password."); return; }
        User user = app.db.getUserByUsername(u);
        if (user == null || !user.password.equals(p)) {
            JOptionPane.showMessageDialog(this, "Invalid credentials."); return;
        }
        app.currentUser = user;
        JOptionPane.showMessageDialog(this, "Welcome, " + app.currentUser.username + "!");
        app.cards.show(app.cardPanel, "hotels");
    }

    private void doRegister(TravelAgencyApp app) {
        String u = tfUser.getText().trim();
        String p = new String(pfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username and password."); return; }
        if (app.db.getUserByUsername(u) != null) { JOptionPane.showMessageDialog(this, "Username already exists."); return; }
        app.db.createUser(u, p);
        JOptionPane.showMessageDialog(this, "Registered! Please login now.");
    }
}
