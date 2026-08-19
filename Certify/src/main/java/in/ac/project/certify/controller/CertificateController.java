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
    public ResponseEntity<InputStreamResource> submitForm(
            @ModelAttribute CertificateContainer certificate)
            throws Exception {

        File zipFile = certificateService.generateCertificates(certificate);

        InputStreamResource resource =
                new InputStreamResource(new FileInputStream(zipFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificates.zip")
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationError(IllegalArgumentException ex, Model model) {
        model.addAttribute("certificateContainer", new CertificateContainer());
        model.addAttribute("errorMessage", ex.getMessage());
        return "view_template";
    }





}
