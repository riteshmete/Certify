package in.ac.project.certify.service;

import in.ac.project.certify.model.CertificateContainer;
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

@Service
public class CertificateService {

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
                                int fontSize) {

        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Template image is required.");
        }

        if (csv == null || csv.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required.");
        }

        if (nameX < 0 || nameY < 0) {
            throw new IllegalArgumentException("Coordinates must be positive.");
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
        int fontSize = certificate.getFontSize();

        validateInputs(template, csv, nameX, nameY, fontSize);

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
                        fontSize,
                        outputDir
                );

                generatedFiles.add(certFile);

                System.out.println("Generated: " + studentName);

            } catch (Exception e) {

                System.out.println(
                        "Failed to generate certificate for "
                                + studentName
                                + " : "
                                + e.getMessage()
                );
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
            int fontSize,
            File outputDir) throws IOException {

        BufferedImage certificate = new BufferedImage(
                originalTemplate.getWidth(),
                originalTemplate.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = certificate.createGraphics();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.drawImage(originalTemplate, 0, 0, null);

        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        g2d.setColor(Color.BLACK);

        g2d.drawString(studentName, nameX, nameY);

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