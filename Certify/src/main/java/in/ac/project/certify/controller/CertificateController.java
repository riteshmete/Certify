package in.ac.project.certify.controller;

import in.ac.project.certify.model.CertificateContainer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CertificateController {

    @GetMapping("/view")
    public String viewForm(Model model){
        CertificateContainer certificate = new CertificateContainer();
        model.addAttribute("certificateContainer",certificate);
        return "view_template";
    }

    @PostMapping("/save")
    public String submitForm(@ModelAttribute CertificateContainer certificate) {

        System.out.println("Template : " +
                certificate.getTemplate().getOriginalFilename());

        System.out.println("CSV : " +
                certificate.getCsv().getOriginalFilename());

        System.out.println("X : " +
                certificate.getNameX());

        System.out.println("Y : " +
                certificate.getNameY());

        System.out.println("Font : " +
                certificate.getFontSize());
        return "view_template";
    }





}
