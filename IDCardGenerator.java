import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class IDCardGenerator extends JPanel {
    public static final int CARD_WIDTH = 360;
    public static final int CARD_HEIGHT = 580;

    private String id = "";
    private String name = "";
    private String department = "";
    private String designation = "";
    private String email = "";
    private String phone = "";
    private String address = "";
    private String bloodGroup = "";
    private String validUntil = "";
    private String photoPath = null;
    
    // Front view configurations
    private String header = "UNIVERSITY OF EXCELLENCE";
    private String footer = "Property of Company | If found return to issuer";
    private int templateStyle = 0; // 0 = Student Blue, 1 = Employee Dark, 2 = Minimal White
    
    // Dual-side configurations
    private boolean isFront = true;
    private String backTemplate = "DEFAULT"; 

    public void updateDetails(String id, String name, String dept, String desig, 
                              String email, String phone, String addr, String bg, 
                              String valid, String photoPath, int templateStyle) {
        this.id = id;
        this.name = name;
        this.department = dept;
        this.designation = desig;
        this.email = email;
        this.phone = phone;
        this.address = addr;
        this.bloodGroup = bg;
        this.validUntil = valid;
        this.photoPath = photoPath;
        this.templateStyle = templateStyle;
        repaint();
    }

    public void setHeader(String header) { this.header = header; repaint(); }
    public void setFooter(String footer) { this.footer = footer; repaint(); }
    public void setFront(boolean value) { this.isFront = value; repaint(); }
    public boolean isFront() { return isFront; }
    public void setBackTemplate(String t) { this.backTemplate = t; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (isFront) {
            drawFront(g2);
        } else {
            drawBack(g2);
        }
    }
    
    private void drawFront(Graphics2D g2) {
        int startX = (getWidth() - CARD_WIDTH) / 2;
        int startY = (getHeight() - CARD_HEIGHT) / 2;

        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(startX + 8, startY + 8, CARD_WIDTH, CARD_HEIGHT, 30, 30);

        Color cardBg = Color.WHITE;
        Color headerStartBg = new Color(41, 128, 185);
        Color headerEndBg = new Color(52, 152, 219);
        Color textColor = new Color(44, 62, 80);
        Color subTextColor = new Color(127, 140, 141);
        Color accentColor = new Color(41, 128, 185);
        Color avatarBgColor = new Color(236, 240, 241);
        
        String headerSubText = "IDENTITY CARD";
        
        if (templateStyle == 0) {
            headerStartBg = new Color(21, 101, 192);
            headerEndBg = new Color(30, 136, 229);
            accentColor = new Color(21, 101, 192);
        } else if (templateStyle == 1) {
            cardBg = new Color(30, 30, 30);
            headerStartBg = new Color(18, 18, 18);
            headerEndBg = new Color(45, 45, 45);
            textColor = new Color(230, 230, 230);
            subTextColor = new Color(170, 170, 170);
            accentColor = new Color(235, 180, 52);
            avatarBgColor = new Color(60, 60, 60);
        } else if (templateStyle == 2) {
            cardBg = new Color(250, 250, 250);
            headerStartBg = new Color(240, 240, 240);
            headerEndBg = new Color(245, 245, 245);
            textColor = new Color(20, 20, 20);
            subTextColor = new Color(100, 100, 100);
            accentColor = new Color(40, 40, 40);
        }

        g2.setColor(cardBg);
        g2.fillRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
        
        if (templateStyle == 1) g2.setColor(new Color(55, 55, 55));
        else g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);

        GradientPaint gp = new GradientPaint(startX, startY, headerStartBg, startX + CARD_WIDTH, startY + 120, headerEndBg);
        g2.setPaint(gp);
        Shape headerClip = new RoundRectangle2D.Float(startX, startY, CARD_WIDTH, 120, 30, 30);
        
        Shape oldClip = g2.getClip();
        g2.setClip(headerClip);
        g2.fillRect(startX, startY, CARD_WIDTH, 120);
        
        g2.setClip(oldClip);
        g2.fillRect(startX, startY + 60, CARD_WIDTH, 60);

        if (templateStyle == 2) {
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(startX, startY + 120, startX + CARD_WIDTH, startY + 120);
        }

        if (templateStyle == 2) g2.setColor(new Color(40, 40, 40));
        else g2.setColor(Color.WHITE);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(header);
        g2.drawString(header, startX + (CARD_WIDTH - textWidth) / 2, startY + 40);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        int subTextX = startX + (CARD_WIDTH - g2.getFontMetrics().stringWidth(headerSubText)) / 2;
        g2.drawString(headerSubText, subTextX, startY + 65);

        int photoWidth = 140;
        int photoHeight = 140;
        int photoX = startX + (CARD_WIDTH - photoWidth) / 2;
        int photoY = startY + 95;

        g2.setColor(cardBg);
        g2.fillOval(photoX - 6, photoY - 6, photoWidth + 12, photoHeight + 12);
        
        Shape circularClip = new java.awt.geom.Ellipse2D.Float(photoX, photoY, photoWidth, photoHeight);
        g2.setClip(circularClip);
        
        g2.setColor(avatarBgColor);
        g2.fillRect(photoX, photoY, photoWidth, photoHeight);
        
        if (photoPath != null && !photoPath.isEmpty()) {
            photoPath = photoPath.toLowerCase();
            System.out.println("Loading image: " + photoPath);
            try {
                BufferedImage img = ImageIO.read(new File(photoPath));
                if (img != null) {
                    double imgAspect = (double) img.getWidth() / img.getHeight();
                    double targetAspect = (double) photoWidth / photoHeight;
                    int drawW, drawH, drawX, drawY;
                    if (imgAspect > targetAspect) {
                        drawH = photoHeight;
                        drawW = (int) (drawH * imgAspect);
                        drawY = photoY;
                        drawX = photoX - (drawW - photoWidth) / 2;
                    } else {
                        drawW = photoWidth;
                        drawH = (int) (drawW / imgAspect);
                        drawX = photoX;
                        drawY = photoY - (drawH - photoHeight) / 2;
                    }
                    g2.drawImage(img, drawX, drawY, drawW, drawH, null);
                }
            } catch (Exception e) {}
        }
        g2.setClip(oldClip); 
        
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(photoX, photoY, photoWidth, photoHeight);
        g2.setStroke(new BasicStroke(1));

        int currentY = photoY + photoHeight + 35;
        g2.setColor(textColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String displayName = (name == null || name.trim().isEmpty()) ? "JOHN DOE" : name.toUpperCase();
        int nameX = startX + (CARD_WIDTH - g2.getFontMetrics().stringWidth(displayName)) / 2;
        g2.drawString(displayName, nameX, currentY);

        currentY += 25;
        g2.setColor(subTextColor);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        String displayDesig = (designation == null || designation.trim().isEmpty()) ? "Title / Designation" : designation;
        int desigX = startX + (CARD_WIDTH - g2.getFontMetrics().stringWidth(displayDesig)) / 2;
        g2.drawString(displayDesig, desigX, currentY);

        currentY += 20;
        if (templateStyle == 1) g2.setColor(new Color(60, 60, 60));
        else g2.setColor(new Color(236, 240, 241));
        g2.drawLine(startX + 40, currentY, startX + CARD_WIDTH - 40, currentY);

        currentY += 30;
        int leftMargin = startX + 50;

        drawDetailField(g2, "ID NO", (id == null || id.isEmpty()) ? "-" : id, leftMargin, currentY, subTextColor, textColor);
        currentY += 26;
        drawDetailField(g2, "DEPT", (department == null || department.isEmpty()) ? "-" : department, leftMargin, currentY, subTextColor, textColor);
        currentY += 26;
        drawDetailField(g2, "BLOOD", (bloodGroup == null || bloodGroup.isEmpty()) ? "-" : bloodGroup, leftMargin, currentY, subTextColor, textColor);
        currentY += 26;
        drawDetailField(g2, "PHONE", (phone == null || phone.isEmpty()) ? "-" : phone, leftMargin, currentY, subTextColor, textColor);
        currentY += 26;
        drawDetailField(g2, "VALID", (validUntil == null || validUntil.isEmpty()) ? "-" : validUntil, leftMargin, currentY, subTextColor, textColor);

        g2.setColor(headerStartBg); 
        if (templateStyle == 2) {
             g2.setColor(new Color(245, 245, 245));
             g2.drawLine(startX, startY + CARD_HEIGHT - 60, startX + CARD_WIDTH, startY + CARD_HEIGHT - 60);
        }
        Shape footerClip = new RoundRectangle2D.Float(startX, startY + CARD_HEIGHT - 60, CARD_WIDTH, 60, 30, 30);
        g2.setClip(footerClip);
        g2.fillRect(startX, startY + CARD_HEIGHT - 60, CARD_WIDTH, 60);
        g2.setClip(oldClip);
        g2.fillRect(startX, startY + CARD_HEIGHT - 60, CARD_WIDTH, 30); 
        
        if (templateStyle == 2) g2.setColor(new Color(40, 40, 40));
        else g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        int footX = startX + (CARD_WIDTH - g2.getFontMetrics().stringWidth(footer)) / 2;
        g2.drawString(footer, footX, startY + CARD_HEIGHT - 25);
    }

    private void drawBack(Graphics2D g2) {
        int startX = (getWidth() - CARD_WIDTH) / 2;
        int startY = (getHeight() - CARD_HEIGHT) / 2;

        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(startX + 8, startY + 8, CARD_WIDTH, CARD_HEIGHT, 30, 30);

        if (backTemplate.equals("DEFAULT")) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(Color.BLACK);
        } else if (backTemplate.equals("DARK")) {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(new Color(55, 55, 55));
            g2.drawRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(Color.WHITE);
        } else if (backTemplate.equals("MINIMAL")) {
            g2.setColor(new Color(250, 250, 250));
            g2.fillRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(startX, startY, CARD_WIDTH, CARD_HEIGHT, 30, 30);
            g2.setColor(Color.BLACK);
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.drawString("ID CARD DETAILS", startX + 90, startY + 60);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString("Address:", startX + 40, startY + 120);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString((address == null || address.isEmpty()) ? "-" : address, startX + 40, startY + 145);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString("Instructions:", startX + 40, startY + 200);
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        g2.drawString("If found, please return to the issuing office.", startX + 40, startY + 225);
        g2.drawString("This card is non-transferable.", startX + 40, startY + 245);

        String qrData = "https://wa.me/91" + phone;
        BufferedImage qr = generateQR(qrData);
        if (qr != null) {
            g2.drawImage(qr, startX + 220, startY + 120, 100, 100, null);
        } else {
            g2.drawRect(startX + 220, startY + 120, 100, 100);
            g2.drawString("QR Error", startX + 240, startY + 175);
        }
    }

    public BufferedImage generateQR(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, 150, 150);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (Exception e) {
            System.err.println("QR Generation Failed: " + e.getMessage());
            return null;
        }
    }

    private void drawDetailField(Graphics2D g2, String label, String value, int x, int y, Color lblColor, Color valColor) {
        g2.setColor(lblColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString(label, x, y);
        g2.setColor(valColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString(value, x + 80, y);
    }
}
