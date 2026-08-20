package in.ac.project.certify.model;

import org.springframework.web.multipart.MultipartFile;

public class CertificateContainer {
    private MultipartFile template;
    private MultipartFile csv;
    private int  nameX;
    private int  nameY;
    private String font;
    private int fontSize;
    private boolean bold;
    private boolean italic;
    private String fontColor = "#000000";

    public CertificateContainer() {

    }

    public CertificateContainer(MultipartFile template, MultipartFile csv, int nameX, int nameY, String font, int fontSize, boolean bold, boolean italic, String fontColor) {
        this.template = template;
        this.csv = csv;
        this.nameX = nameX;
        this.nameY = nameY;
        this.font = font;
        this.fontSize = fontSize;
        this.bold = bold;
        this.italic = italic;
        this.fontColor = fontColor;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
}
