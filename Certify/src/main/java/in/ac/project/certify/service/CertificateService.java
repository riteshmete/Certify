package in.ac.project.certify.service;

import in.ac.project.certify.model.CertificateContainer;
import in.ac.project.certify.util.FileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.annotation.PostConstruct;

@Service
public class CertificateService {

    private static final long MAX_TEMPLATE_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;
    private static final long MAX_CSV_SIZE_BYTES = 5L * 1024 * 1024;

    @PostConstruct
    public void registerCustomFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontFiles = {"GreatVibes-Regular.ttf", "AlexBrush-Regular.ttf", "Allura-Regular.ttf"};
            for (String file : fontFiles) {
                try (InputStream is = getClass().getResourceAsStream("/fonts/" + file)) {
                    if (is != null) {
                        Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                        ge.registerFont(customFont);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom fonts: " + e.getMessage());
        }
    }

    private List<String> readCSVFile(MultipartFile csv) throws IOException {

        List<String> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(csv.getInputStream()))) {

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                if (data.length > 0) {
                    String studentName = data[0].trim();

                    if (!studentName.isEmpty()) {
                        students.add(studentName);
                    }
                }
            }
        }

        return students;
    }

    private void validateInputs(MultipartFile template,
            MultipartFile csv,
            int nameX,
            int nameY,
            String font,
            int fontSize) throws Exception {

        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Template image is required.");
        }

        if (!FileValidator.isValidImageFile(template)) {
            throw new IllegalArgumentException(
                    "Invalid template image. Please upload a JPG, PNG, GIF, BMP, or WEBP image.");
        }

        if (template.getSize() > MAX_TEMPLATE_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Template image is too large. Maximum size is 10 MB.");
        }

        if (csv == null || csv.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required.");
        }

        if (!FileValidator.isValidCsvFile(csv)) {
            throw new IllegalArgumentException("Invalid CSV file. Please upload a valid .csv file.");
        }

        if (csv.getSize() > MAX_CSV_SIZE_BYTES) {
            throw new IllegalArgumentException("CSV file is too large. Maximum size is 5 MB.");
        }

        if (nameX < 0 || nameY < 0) {
            throw new IllegalArgumentException("Coordinates must be positive.");
        }

        if (font == null || font.trim().isEmpty()) {
            throw new IllegalArgumentException("Valid font is required.");
        }

        if (fontSize <= 0 || fontSize > 200) {
            throw new IllegalArgumentException("Font size must be between 1 and 200.");
        }
    }

    public File generateCertificates(CertificateContainer certificate) throws Exception {

        MultipartFile template = certificate.getTemplate();
        MultipartFile csv = certificate.getCsv();

        int nameX = certificate.getNameX();
        int nameY = certificate.getNameY();
        String font = certificate.getFont();
        int fontSize = certificate.getFontSize();

        validateInputs(template, csv, nameX, nameY, font,fontSize);

        BufferedImage templateImage = ImageIO.read(template.getInputStream());

        if (templateImage == null) {
            throw new IOException("Invalid template image.");
        }

        List<String> students = readCSVFile(csv);

        if (students.isEmpty()) {
            throw new IOException("CSV contains no student names.");
        }

        List<File> generatedFiles = new ArrayList<>();

        File outputDir = new File("certificate_output");

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Unable to create output directory.");
        }

        for (String studentName : students) {

            try {

                File certFile = generateSingleCertificate(
                        templateImage,
                        studentName,
                        nameX,
                        nameY,
                        font,
                        fontSize,
                        outputDir);

                generatedFiles.add(certFile);

                System.out.println("Generated: " + studentName);

            } catch (Exception e) {

                System.out.println(
                        "Failed to generate certificate for "
                                + studentName
                                + " : "
                                + e.getMessage());
            }
        }

        if (generatedFiles.isEmpty()) {
            throw new Exception("No certificates were generated.");
        }

        System.out.println("Successfully generated "
                + generatedFiles.size()
                + " certificates.");

        return createZipFile(generatedFiles);
    }

    private File generateSingleCertificate(
            BufferedImage originalTemplate,
            String studentName,
            int nameX,
            int nameY,
            String font,
            int fontSize,
            File outputDir) throws IOException {

        BufferedImage certificate = new BufferedImage(
                originalTemplate.getWidth(),
                originalTemplate.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = certificate.createGraphics();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalTemplate, 0, 0, null);

        g2d.setFont(new Font(font, Font.PLAIN,fontSize));
        g2d.setColor(Color.BLACK);

        FontMetrics metrics = g2d.getFontMetrics();

        int textWidth = metrics.stringWidth(studentName);

        int textX = nameX - (textWidth / 2);

        int textY = nameY + (metrics.getAscent() - metrics.getDescent()) / 2;

        g2d.drawString(studentName, textX, textY);
        g2d.dispose();

        String filename = studentName
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "")
                + "_Certificate.png";

        File certificateFile = new File(outputDir, filename);

        ImageIO.write(certificate, "png", certificateFile);

        return certificateFile;
    }

    private File createZipFile(List<File> generatedFiles) throws IOException {

        File zipFile = new File("certificate_output/certificates.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (File file : generatedFiles) {

                try (FileInputStream fis = new FileInputStream(file)) {

                    ZipEntry zipEntry = new ZipEntry(file.getName());

                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = fis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, length);
                    }

                    zipOut.closeEntry();
                }
            }
        }

        return zipFile;
    }
}