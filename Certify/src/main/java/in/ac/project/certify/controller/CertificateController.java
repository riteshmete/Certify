package in.ac.project.certify.controller;

import in.ac.project.certify.service.CertificateService;
import in.ac.project.certify.model.CertificateContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.FileInputStream;

@Controller
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/view")
    public String viewForm(Model model){
        CertificateContainer certificate = new CertificateContainer();
        model.addAttribute("certificateContainer",certificate);
        return "view_template";
    }

    @PostMapping("/save")
    public ResponseEntity<?> submitForm(
            @ModelAttribute("certificateContainer") CertificateContainer certificate) {

        try {
            File zipFile = certificateService.generateCertificates(certificate);

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(zipFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=certificates.zip")
                    .contentLength(zipFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception ex) {
            String errorMsg = ex.getMessage() != null ? ex.getMessage() : "An error occurred while processing your request.";
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleValidationError(Exception ex) {
        String errorMsg = ex.getMessage() != null ? ex.getMessage() : "An error occurred while processing your request.";
        return ResponseEntity.badRequest().body(errorMsg);
    }





}
