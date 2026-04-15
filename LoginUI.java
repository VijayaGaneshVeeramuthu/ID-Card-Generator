import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginUI extends JFrame {

    private CustomTextField userField;
    private CustomPasswordField passField;
    private JLabel lblMessage;

    public LoginUI() {
        setTitle("ID Card Generator");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        
        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), getWidth(), getHeight(), new Color(142, 68, 173));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Escape key to close
        getRootPane().registerKeyboardAction(e -> {
            System.exit(0);
        }, KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        // Glassmorphism card
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 25, 25);
                // Draw glass
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                // Draw border
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(440, 520));
        cardPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        
        // Icon/Logo stub
        JLabel iconLabel = new JLabel("👤", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        cardPanel.add(iconLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("ID Card Generator Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        cardPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Welcome back! Please login to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(240, 240, 240));
        gbc.gridy = 2; gbc.insets = new Insets(0, 20, 30, 20);
        cardPanel.add(subtitleLabel, gbc);
        
        gbc.insets = new Insets(10, 30, 10, 30);
        
        // Username
        userField = new CustomTextField("  Enter your username");
        gbc.gridy = 3;
        cardPanel.add(userField, gbc);
        
        // Password
        passField = new CustomPasswordField("  Enter your password");
        gbc.gridy = 4;
        cardPanel.add(passField, gbc);
        
        // Message label (error/success)
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 5; gbc.insets = new Insets(0, 30, 10, 30);
        cardPanel.add(lblMessage, gbc);
        
        // Options row
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setForeground(Color.WHITE);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setOpaque(false);
        showPass.setFocusPainted(false);
        showPass.addActionListener(e -> passField.setEchoChar(showPass.isSelected() ? (char)0 : '•'));
        optionsPanel.add(showPass, BorderLayout.WEST);
        
        JLabel forgotPass = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPass.setForeground(Color.WHITE);
        forgotPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        optionsPanel.add(forgotPass, BorderLayout.EAST);
        
        gbc.gridy = 6; gbc.insets = new Insets(0, 30, 15, 30);
        cardPanel.add(optionsPanel, gbc);
        
        // Primary Button
        JButton loginBtn = createGradientButton("Login");
        gbc.gridy = 7; gbc.insets = new Insets(10, 30, 10, 30);
        cardPanel.add(loginBtn, gbc);
        
        // Secondary Button
        JButton registerBtn = createOutlineButton("Create Account");
        gbc.gridy = 8; gbc.insets = new Insets(5, 30, 20, 30);
        cardPanel.add(registerBtn, gbc);
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);
        
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> showRegisterDialog());
    }

    private void showMessage(String text, boolean success) {
        lblMessage.setText(text);
        lblMessage.setForeground(success ? new Color(133, 222, 119) : new Color(255, 105, 97)); // adjusted for dark background
    }

    private void login() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            showMessage("Warning: Username and Password required!", false);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM login WHERE BINARY username=? AND BINARY password=?"
            );
            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                showMessage("Success! Logging in...", true);
                Timer t = new Timer(600, e -> {
                    new UI().setVisible(true);
                    dispose();
                });
                t.setRepeats(false);
                t.start();
            } else {
                showMessage("Error: Invalid Login Credentials!", false);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Database Error!", false);
        }
    }

    private void showRegisterDialog() {
        JTextField newUser = new JTextField();
        JPasswordField newPass = new JPasswordField();
        Object[] fields = { "New Username:", newUser, "New Password:", newPass };
        int option = JOptionPane.showConfirmDialog(this, fields, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            registerUser(newUser.getText(), new String(newPass.getPassword()));
        }
    }

    private void registerUser(String username, String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO login (username, password) VALUES (?, ?)");
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    private JButton createGradientButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isRollover() || getModel().isArmed() ? new Color(130, 48, 160) : new Color(155, 89, 182);
                Color c2 = getModel().isRollover() || getModel().isArmed() ? new Color(31, 98, 145) : new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isArmed()) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
                }
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class CustomTextField extends JTextField {
        private String placeholder;
        public CustomTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 45));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus()) {
                g2.setColor(new Color(255, 255, 255, 160));
                int fm = g.getFontMetrics().getAscent();
                g2.drawString(placeholder, 15, (getHeight() + fm) / 2 - 2);
            }
            g2.dispose();
        }
    }

    class CustomPasswordField extends JPasswordField {
        private String placeholder;
        public CustomPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(300, 45));
            setEchoChar('•');
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
            if (getPassword().length == 0 && !hasFocus()) {
                g2.setColor(new Color(255, 255, 255, 160));
                int fm = g.getFontMetrics().getAscent();
                g2.drawString(placeholder, 15, (getHeight() + fm) / 2 - 2);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
