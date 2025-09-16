import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator extends JFrame {
    private JTextField inputField;
    private JButton generateButton;
    private JLabel qrCodeLabel;

    public QRCodeGenerator() {
        setTitle("QR Code Generator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        inputField = new JTextField(20);
        generateButton = new JButton("Generate QR Code");
        qrCodeLabel = new JLabel();

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateQRCode(inputField.getText());
            }
        });

        add(inputField);
        add(generateButton);
        add(qrCodeLabel);
    }

    private void generateQRCode(String data) {
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to generate a QR code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            ImageIcon qrCodeImage = new ImageIcon(MatrixToImageWriter.toBufferedImage(bitMatrix));
            qrCodeLabel.setIcon(qrCodeImage);
        } catch (WriterException e) {
            JOptionPane.showMessageDialog(this, "Error generating QR code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QRCodeGenerator generator = new QRCodeGenerator();
            generator.setVisible(true);
        });
    }
}