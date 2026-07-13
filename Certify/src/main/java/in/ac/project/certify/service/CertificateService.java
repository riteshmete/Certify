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
public class CertificateService{

    private List<String> readCSVFile(File csv) throws IOException {
        List<String> students = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String line;
        boolean isFirstLine = true;

        try {
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
        finally {

            br.close();
        }
        return students;


    }

    private void validateInputs(File template,File csv, int nameX,int nameY,int fontSize) throws Exception {
        if (template == null || !template.exists()) {
            throw new FileNotFoundException("Template file not found");
        }
        if(csv == null || !csv.exists()) {
            throw new FileNotFoundException("CSV not found");
        }
        if(nameX < 0 || nameY < 0 ) {
            throw new FileNotFoundException("Coordinates must be positive");
        }
        if (fontSize <= 0 || fontSize > 200) {
            throw new IllegalArgumentException("Font size must be between 1 and 200");
        }

    }

//Crreating genrateCertificates method
public File generateCertificates(CertificateContainer certificate) throws Exception {

    // Get data from CertificateContainer
    MultipartFile template = certificate.getTemplate();
    MultipartFile csv = certificate.getCsv();

    int nameX = certificate.getNameX();
    int nameY = certificate.getNameY();
    int fontSize = certificate.getFontSize();

    // Convert MultipartFile to File
    File templateFile = File.createTempFile("template", ".png");
    template.transferTo(templateFile);

    File csvFile = File.createTempFile("students", ".csv");
    csv.transferTo(csvFile);

    try {

        // Validate inputs
        validateInputs(templateFile, csvFile, nameX, nameY, fontSize);

        // Load template image
        BufferedImage templateImage = ImageIO.read(templateFile);

        if (templateImage == null) {
            throw new IOException("Failed to load template image. Make sure it is a valid PNG or JPG.");
        }

        // Read student names
        List<String> students = readCSVFile(csvFile);

        if (students.isEmpty()) {
            throw new IOException("CSV File is empty.");
        }

        // Store generated certificate paths
        List<String> generatedFiles = new ArrayList<>();

        // Create output folder
        File outputDir = new File("certificate_output");

        if (!outputDir.exists()) {

            boolean created = outputDir.mkdirs();

            if (!created) {
                throw new IOException("Could not create output directory.");
            }
        }

        // Generate certificates one by one
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

                generatedFiles.add(certFile.getAbsolutePath());

                System.out.println("Generated : " + studentName);

            } catch (Exception e) {

                System.out.println("Failed to generate for : "
                        + studentName);

            }
        }

        if (generatedFiles.isEmpty()) {
            throw new Exception("Failed to generate certificates.");
        }

        System.out.println("\nSuccessfully Generated : "
                + generatedFiles.size());

        File zipFile = createZipFile(generatedFiles);

        return zipFile;
    } finally {

        // Delete temporary uploaded files
        templateFile.delete();
        csvFile.delete();
    }
}


    private File generateSingleCertificate(
            BufferedImage originalTemplate,
            String studentName,
            int nameX,
            int nameY,
            int fontSize,
            File outputDir) throws IOException{

        BufferedImage certificate = new BufferedImage(
                originalTemplate.getWidth(),
                originalTemplate.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        // Copy original template to new image
        Graphics2D g2d = certificate.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalTemplate, 0, 0, null);

        // Draw student name on the certificate
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        g2d.setColor(Color.BLACK);
        g2d.drawString(studentName, nameX, nameY);

        g2d.dispose();

        // Save certificate as PNG
        String filename = studentName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "") + "_Certificate.png";
        File certificateFile = new File(outputDir, filename);
        ImageIO.write(certificate, "png", certificateFile);

        return certificateFile;


    }



    private File createZipFile(List<String> generatedFiles) throws IOException {

        File zipFile = new File("certificate_output/certificates.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (String filePath : generatedFiles) {

                File file = new File(filePath);

                try (FileInputStream fis = new FileInputStream(file)) {

                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, length);
                    }

                    zipOut.closeEntry();
                }
            }
        }

        return zipFile;
    }


   }


