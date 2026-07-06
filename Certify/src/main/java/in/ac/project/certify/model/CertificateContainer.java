package in.ac.project.certify.model;

import org.springframework.web.multipart.MultipartFile;

public class CertificateContainer {
    private MultipartFile template;
    private MultipartFile csv;
    private int  nameX;
    private int  nameY;
    private int fontSize;

    public CertificateContainer() {

    }

    public CertificateContainer(MultipartFile template, MultipartFile csv, int nameX, int nameY, int fontSize) {
        this.template = template;
        this.csv = csv;
        this.nameX = nameX;
        this.nameY = nameY;
        this.fontSize = fontSize;
    }

    public MultipartFile getTemplate() {
        return template;
    }

    public void setTemplate(MultipartFile template) {
        this.template = template;
    }

    public MultipartFile getCsv() {
        return csv;
    }

    public void setCsv(MultipartFile csv) {
        this.csv = csv;
    }

    public int getNameX() {
        return nameX;
    }

    public void setNameX(int nameX) {
        this.nameX = nameX;
    }

    public int getNameY() {
        return nameY;
    }

    public void setNameY(int nameY) {
        this.nameY = nameY;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
