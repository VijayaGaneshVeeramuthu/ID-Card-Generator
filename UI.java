import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class UI extends JFrame {
    private JTextField txtId, txtName, txtDepartment, txtDesignation;
    private JTextField txtEmail, txtPhone, txtAddress, txtValidUntil;
    private JTextField txtHeader, txtFooter;
    private JComboBox<String> bloodBox;
    private JButton btnUpload, btnGenerate, btnSave, btnExport, btnBulkCsv, btnFlip;
    private JLabel lblPhotoStatus;
    private JComboBox<String> cmbTemplate, cmbBackTemplate;
    private String selectedPhotoPath = "";
    
    private IDCardGenerator cardPanel;

    public UI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        setTitle("NextGen ID Card Creator");
        setSize(1200, 880);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        
        JLabel headerLabel = new JLabel("Employee ID Card Studio");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JPanel templatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        templatePanel.setOpaque(false);
        JLabel lblTheme = new JLabel("Front:");
        lblTheme.setForeground(Color.WHITE);
        lblTheme.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] templates = { "Student ID (Blue)", "Employee ID (Dark)", "Minimal Clean (White)" };
        cmbTemplate = new JComboBox<>(templates);
        cmbTemplate.addActionListener(e -> updatePreview());
        templatePanel.add(lblTheme);
        templatePanel.add(cmbTemplate);

        JLabel lblBackTheme = new JLabel("  Back:");
        lblBackTheme.setForeground(Color.WHITE);
        lblBackTheme.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String[] backTemplates = {"DEFAULT", "DARK", "MINIMAL"};
        cmbBackTemplate = new JComboBox<>(backTemplates);
        cmbBackTemplate.addActionListener(e -> {
            cardPanel.setBackTemplate((String) cmbBackTemplate.getSelectedItem());
        });
        templatePanel.add(lblBackTheme);
        templatePanel.add(cmbBackTemplate);

        templatePanel.add(new JLabel("  "));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(231, 76, 60)); // Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        templatePanel.add(btnLogout);

        templatePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        headerPanel.add(templatePanel, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Enter Details (Real-time Preview)"));
        formPanel.setPreferredSize(new Dimension(420, 600));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        txtId = new JTextField(15);
        txtName = new JTextField(15);
        txtDepartment = new JTextField(15);
        txtDesignation = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPhone = new JTextField(15);
        txtPhone.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) e.consume();
            }
        });
        txtAddress = new JTextField(15);
        txtHeader = new JTextField("UNIVERSITY OF EXCELLENCE", 15);
        txtFooter = new JTextField("Property of Company | If found return to issuer", 15);
        
        String[] bloodGroups = { "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-" };
        bloodBox = new JComboBox<>(bloodGroups);
        txtValidUntil = new JTextField(15);
        
        btnUpload = new JButton("Browse Photo");
        lblPhotoStatus = new JLabel("No photo selected");

        int row = 0;
        addField(formPanel, "Employee ID:", txtId, gbc, row++);
        addField(formPanel, "Full Name:", txtName, gbc, row++);
        addField(formPanel, "Department:", txtDepartment, gbc, row++);
        addField(formPanel, "Designation:", txtDesignation, gbc, row++);
        addField(formPanel, "Email ID:", txtEmail, gbc, row++);
        addField(formPanel, "Phone No:", txtPhone, gbc, row++);
        addField(formPanel, "Address:", txtAddress, gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lblBlood = new JLabel("Blood Group:");
        lblBlood.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblBlood, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        bloodBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bloodBox.setBackground(Color.WHITE);
        formPanel.add(bloodBox, gbc);
        bloodBox.addActionListener(e -> updatePreview());
        row++;
        
        addField(formPanel, "Valid Until:", txtValidUntil, gbc, row++);
        addField(formPanel, "Header Text:", txtHeader, gbc, row++);
        addField(formPanel, "Footer Text:", txtFooter, gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Profile Photo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
        JPanel photoPanel = new JPanel(new BorderLayout(5, 0));
        photoPanel.add(btnUpload, BorderLayout.WEST);
        lblPhotoStatus.setPreferredSize(new Dimension(100, 20)); 
        photoPanel.add(lblPhotoStatus, BorderLayout.CENTER);
        formPanel.add(photoPanel, gbc);
        row++;

        formWrapper.add(formPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        actionPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnGenerate = createStyledButton("Refresh Preview", new Color(46, 204, 113));
        btnSave = createStyledButton("Save Data to System", new Color(52, 152, 219));
        btnExport = createStyledButton("Export Dual PDF", new Color(155, 89, 182));
        btnBulkCsv = createStyledButton("Bulk Import & Export", new Color(230, 126, 34));
        btnFlip = createStyledButton("Flip Card", new Color(52, 73, 94));

        actionPanel.add(btnGenerate);
        actionPanel.add(btnSave);
        actionPanel.add(btnExport);
        actionPanel.add(btnBulkCsv);
        actionPanel.add(btnFlip);

        formWrapper.add(actionPanel, BorderLayout.CENTER);
        
        scrollPane.setViewportView(formWrapper);
        scrollPane.setPreferredSize(new Dimension(480, 0));
        contentPane.add(scrollPane, BorderLayout.WEST);

        cardPanel = new IDCardGenerator();
        cardPanel.setBackground(new Color(236, 240, 241));
        contentPane.add(cardPanel, BorderLayout.CENTER);

        setupListeners();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setEnabled(true);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        
        return btn;
    }

    private void addField(JPanel panel, String label, JTextField textField, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(textField, gbc);
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updatePreview(); }
            public void removeUpdate(DocumentEvent e) { updatePreview(); }
            public void insertUpdate(DocumentEvent e) { updatePreview(); }
        });
    }
    
    private void updatePreview() {
        if(cardPanel != null) {
            cardPanel.setHeader(txtHeader.getText());
            cardPanel.setFooter(txtFooter.getText());
            cardPanel.updateDetails(
                txtId.getText(), txtName.getText(), txtDepartment.getText(), 
                txtDesignation.getText(), txtEmail.getText(), txtPhone.getText(), 
                txtAddress.getText(), (String) bloodBox.getSelectedItem(), txtValidUntil.getText(), 
                selectedPhotoPath, cmbTemplate.getSelectedIndex()
            );
        }
    }

    private void setupListeners() {
        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedPhotoPath = selectedFile.getAbsolutePath();
                lblPhotoStatus.setText(selectedFile.getName());
                updatePreview(); 
            }
        });
        btnGenerate.addActionListener(e -> updatePreview());
        btnSave.addActionListener(e -> saveToDatabase());
        btnExport.addActionListener(e -> exportPDF(cardPanel));
        btnBulkCsv.addActionListener(e -> handleBulkCSV());
        btnFlip.addActionListener(e -> {
            cardPanel.setFront(!cardPanel.isFront());
        });
    }

    private void saveToDatabase() {
        String id = txtId.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID is required to save details!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String phone = txtPhone.getText();
        if (!phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain only digits!");
            return;
        }
        if (phone.length() != 10) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits!");
            return;
        }
        if (insertOrUpdate(id, txtName.getText(), txtDepartment.getText(), txtDesignation.getText(), 
                           txtEmail.getText(), phone, txtAddress.getText(), 
                           (String) bloodBox.getSelectedItem(), txtValidUntil.getText(), selectedPhotoPath)) {
            JOptionPane.showMessageDialog(this, "Employee data has been successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private boolean insertOrUpdate(String id, String name, String dept, String desig, String email, 
                                   String phone, String addr, String bg, String valid, String sPhotoPath) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return false;

            String checkSql = "SELECT count(*) FROM users WHERE id = ?";
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, id);
                var rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) exists = true;
            }

            if (exists) {
                String sql = "UPDATE users SET name=?, dept=?, designation=?, email=?, phone=?, address=?, blood=?, valid=?, photo=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name); stmt.setString(2, dept); stmt.setString(3, desig);
                    stmt.setString(4, email); stmt.setString(5, phone); stmt.setString(6, addr);
                    stmt.setString(7, bg); stmt.setString(8, valid); stmt.setString(9, sPhotoPath);
                    stmt.setString(10, id);
                    stmt.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO users (id, name, dept, designation, email, phone, address, blood, valid, photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, id); stmt.setString(2, name); stmt.setString(3, dept); 
                    stmt.setString(4, desig); stmt.setString(5, email); stmt.setString(6, phone); 
                    stmt.setString(7, addr); stmt.setString(8, bg); stmt.setString(9, valid); 
                    stmt.setString(10, sPhotoPath);
                    stmt.executeUpdate();
                }
            }
            conn.close();
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return false;
        }
    }

    public void exportPDF(IDCardGenerator panel) {
        try {
            updatePreview();
            
            String id = txtId.getText();
            if (id == null || id.trim().isEmpty()) {
                id = "unknown_user";
            }
            
            File dir = new File("exports");
            dir.mkdirs();
            
            String filePath = "exports/" + id + ".pdf";
            
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            doc.open();

            panel.setFront(true);
            BufferedImage frontImg = capture(panel);
            Image front = Image.getInstance(frontImg, null);
            
            float documentWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
            float documentHeight = doc.getPageSize().getHeight() - doc.topMargin() - doc.bottomMargin();
            front.scaleToFit(documentWidth, documentHeight);
            doc.add(front);

            doc.newPage();

            panel.setFront(false);
            BufferedImage backImg = capture(panel);
            Image back = Image.getInstance(backImg, null);
            back.scaleToFit(documentWidth, documentHeight);
            doc.add(back);

            doc.close();
            // Reset to front view
            panel.setFront(true);
            
            System.out.println("Saved at: " + new File(filePath).getAbsolutePath());
            JOptionPane.showMessageDialog(this, "PDF Exported successfully to:\n" + new File(filePath).getAbsolutePath());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public BufferedImage capture(JPanel panel) {
        int cardWidth = IDCardGenerator.CARD_WIDTH;
        int cardHeight = IDCardGenerator.CARD_HEIGHT;
        int startX = (panel.getWidth() - cardWidth) / 2;
        int startY = (panel.getHeight() - cardHeight) / 2;

        if (startX < 0 || startY < 0) {
             JOptionPane.showMessageDialog(this, "Preview panel too small. Resize window.", "Warning", JOptionPane.WARNING_MESSAGE);
             return null;
        }

        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        g2.dispose();

        return image.getSubimage(startX, startY, cardWidth, cardHeight);
    }
    
    private void handleBulkCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File for Bulk Processing");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Step 1: Use LOCAL project folder specifically to avoid OneDrive confusion
            File exportDir = new File("exports");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            
            System.out.println("Export folder: " + exportDir.getAbsolutePath());
            
            int successCnt = 0;
            int errorCnt = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) { firstLine = false; continue; } 
                    
                    String[] data = line.split(",", -1);
                    if (data.length >= 10) {
                        try {
                            String mId = data[0].trim();
                            String mName = data[1].trim();
                            String mOrg = data[2].trim();
                            String mDept = data[3].trim();
                            String mDesig = data[4].trim();
                            String mEmail = data[5].trim();
                            String mPhone = data[6].trim();
                            String mValid = data[7].trim();
                            String mAddr = data[8].trim();
                            String mBlood = data[9].trim();
                            String mPhoto = (data.length > 10) ? data[10].trim() : "";
                            
                            insertOrUpdate(mId, mName, mDept, mDesig, mEmail, mPhone, mAddr, mBlood, mValid, mPhoto);
                            
                            IDCardGenerator bulkPanel = new IDCardGenerator();
                            bulkPanel.setBounds(0, 0, IDCardGenerator.CARD_WIDTH + 50, IDCardGenerator.CARD_HEIGHT + 50);
                            bulkPanel.setHeader(txtHeader.getText());
                            bulkPanel.setFooter(txtFooter.getText());
                            bulkPanel.updateDetails(mId, mName, mDept, mDesig, mEmail, mPhone, mAddr, mBlood, mValid, mPhoto, cmbTemplate.getSelectedIndex());
                            bulkPanel.setBackTemplate((String) cmbBackTemplate.getSelectedItem());
                            
                            // Step 2 & EXPLICIT PATH Step 5
                            String filePath = new File("exports", mId + ".pdf").getAbsolutePath();
                            
                            Document doc = new Document();
                            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
                            doc.open();

                            bulkPanel.setFront(true);
                            BufferedImage frontImg = capture(bulkPanel);
                            Image front = Image.getInstance(frontImg, null);
                            float documentWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
                            float documentHeight = doc.getPageSize().getHeight() - doc.topMargin() - doc.bottomMargin();
                            front.scaleToFit(documentWidth, documentHeight);
                            doc.add(front);

                            doc.newPage();
                            bulkPanel.setFront(false);
                            BufferedImage backImg = capture(bulkPanel);
                            Image back = Image.getInstance(backImg, null);
                            back.scaleToFit(documentWidth, documentHeight);
                            doc.add(back);

                            doc.close();

                            System.out.println("Saved: " + filePath);
                            successCnt++;
                        } catch (Exception err) {
                            err.printStackTrace();
                            errorCnt++;
                        }
                    }
                }
                updatePreview();
                JOptionPane.showMessageDialog(this, 
                    "Bulk processing complete!\nSuccess: " + successCnt + "\nErrors: " + errorCnt + "\nPDFs exported to: " + exportDir.getAbsolutePath(), 
                    "CSV Processing", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading CSV: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
