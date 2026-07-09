package in.ac.project.certify.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateService{

    private List<String> read


//Crreating genrateCertificates method
public List<String> genrateCertificates(
        File template,
        File csv,
        int nameX,
        int nameY,
        int fontSize)throws Exception {

    //Validate inputs
    validateInputs(template, csv, nameX, nameY, fontSize);

    //Load template img
    BufferedImage templateImage = ImageIO.read(template);
    if(templateImage == null) {
        throw new IOException("Failed to load template image make sure that it is a valid PNG or JPG");
    }

    //Read student names from csv
    List<String> students = readCSVFile(csv);
    if (students.isEmpty()) {
        throw new IOException("CSV File is empty");
    }

    //Genarate certificate for each student

    //this List will not store the IMG this will store the paths of the genrated certificates
    List<String> genratedFiles = new ArrayList<>();

    File outputDir = new File("certificate_output");
    if (!outputDir.exists()) {
        boolean created = outputDir.mkdirs();

        if (!created) {
            throw new IOException("Could not create output directory.");
        }
    }

    for (String studentName : students){
        try {
            File certFile = genrateSingleCertificate(
                    template,
                    studentName,
                    nameX,
                    nameY,
                    fontSize,
                    outputDir
            );
            genratedFiles.add(certFile.getAbsolutePath());
            System.out.println("Genrated" + studentName);
            } catch (Exception e) {
                 System.out.println("Failed to genrate for" + studentName);
            }
        }
    if (genratedFiles.isEmpty()) {
        throw new Exception("failed to genrate");
    }

    System.out.println("\n Successfully genrated" + genratedFiles.size());
    return genratedFiles;


    }




   }

}
