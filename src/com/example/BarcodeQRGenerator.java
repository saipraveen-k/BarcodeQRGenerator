package com.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

/**
 * Barcode & QR Code Generator.
 *
 * GUI:  java -jar BarcodeQRGenerator.jar
 * CLI:  java -jar BarcodeQRGenerator.jar --cli "text" [FORMAT] [output.png]
 *       FORMAT is one of the names in {@link Format} (default QR_CODE).
 */
public class BarcodeQRGenerator extends JFrame {

    /** Supported formats, with display name, output size and input rules. */
    public enum Format {
        QR_CODE("QR Code", BarcodeFormat.QR_CODE, 300, 300, "Any text or URL"),
        DATA_MATRIX("Data Matrix", BarcodeFormat.DATA_MATRIX, 300, 300, "Any text"),
        AZTEC("Aztec", BarcodeFormat.AZTEC, 300, 300, "Any text"),
        PDF_417("PDF417", BarcodeFormat.PDF_417, 400, 160, "Any text"),
        CODE_128("Code 128", BarcodeFormat.CODE_128, 400, 150, "Printable ASCII"),
        CODE_39("Code 39", BarcodeFormat.CODE_39, 400, 150, "A-Z, 0-9, space and - . $ / + %"),
        EAN_13("EAN-13", BarcodeFormat.EAN_13, 400, 150, "12 or 13 digits"),
        UPC_A("UPC-A", BarcodeFormat.UPC_A, 400, 150, "11 or 12 digits");

        final String label;
        final BarcodeFormat zxing;
        final int width, height;
        final String hint;

        Format(String label, BarcodeFormat zxing, int width, int height, String hint) {
            this.label = label;
            this.zxing = zxing;
            this.width = width;
            this.height = height;
            this.hint = hint;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Encodes text in the given format. Throws IllegalArgumentException with a readable message on bad input. */
    public static BufferedImage generate(String data, Format format) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Please enter some text.");
        }
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        if (format.width == format.height) {
            // 2D codes: small quiet zone. 1D barcodes keep ZXing's default (10 modules),
            // which scanners need to find EAN/UPC codes.
            hints.put(EncodeHintType.MARGIN, 2);
        }
        if (format == Format.QR_CODE) {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        }
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, format.zxing, format.width, format.height, hints);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException | IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Cannot encode this text as " + format.label + " (" + format.hint + "): " + e.getMessage(), e);
        }
    }

    private final JTextField inputField = new JTextField(24);
    private final JComboBox<Format> formatBox = new JComboBox<>(Format.values());
    private final JLabel hintLabel = new JLabel();
    private final JLabel imageLabel = new JLabel("Your code will appear here", SwingConstants.CENTER);
    private final JButton saveButton = new JButton("Save PNG…");
    private BufferedImage current;

    public BarcodeQRGenerator() {
        super("Barcode & QR Code Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate");
        controls.add(new JLabel("Text:"));
        controls.add(inputField);
        controls.add(formatBox);
        controls.add(generateButton);
        controls.add(saveButton);

        hintLabel.setForeground(Color.GRAY);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        updateHint();

        imageLabel.setPreferredSize(new Dimension(440, 340));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        saveButton.setEnabled(false);

        generateButton.addActionListener(e -> onGenerate());
        inputField.addActionListener(e -> onGenerate()); // Enter key
        formatBox.addActionListener(e -> updateHint());
        saveButton.addActionListener(e -> onSave());

        add(controls, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        add(hintLabel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void updateHint() {
        hintLabel.setText("Allowed input: " + ((Format) formatBox.getSelectedItem()).hint);
    }

    private void onGenerate() {
        Format format = (Format) formatBox.getSelectedItem();
        try {
            current = generate(inputField.getText().trim(), format);
            imageLabel.setText(null);
            imageLabel.setIcon(new ImageIcon(current));
            saveButton.setEnabled(true);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));
        chooser.setSelectedFile(new File("code.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) file = new File(file.getPath() + ".png");
        try {
            ImageIO.write(current, "png", file);
            JOptionPane.showMessageDialog(this, "Saved to " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static int runCli(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: --cli \"text\" [FORMAT] [output.png]");
            System.err.println("FORMAT: " + java.util.Arrays.toString(Format.values()).replace(", ", "|"));
            return 2;
        }
        String text = args[1];
        Format format = Format.QR_CODE;
        if (args.length >= 3) {
            try {
                format = Format.valueOf(args[2].toUpperCase().replace('-', '_'));
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown format: " + args[2]);
                return 2;
            }
        }
        File out = new File(args.length >= 4 ? args[3] : "code.png");
        try {
            ImageIO.write(generate(text, format), "png", out);
            System.out.println("Wrote " + format.label + " to " + out.getAbsolutePath());
            return 0;
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            System.exit(runCli(args));
        }
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("No display available. Use: --cli \"text\" [FORMAT] [output.png]");
            System.exit(2);
        }
        SwingUtilities.invokeLater(() -> new BarcodeQRGenerator().setVisible(true));
    }
}
