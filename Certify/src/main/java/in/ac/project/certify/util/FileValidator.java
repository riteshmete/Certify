package in.ac.project.certify.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

public final class FileValidator {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");

    private static final Set<String> ALLOWED_CSV_CONTENT_TYPES =
            Set.of("text/csv", "application/csv", "text/plain");

    private FileValidator() {
    }

    public static boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String extension = getExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            String normalizedContentType = contentType.toLowerCase(Locale.ROOT);
            if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(normalizedContentType)) {
                return false;
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return true;
            }
        } catch (IOException ex) {
        }

        return "webp".equals(extension) && hasWebpSignature(file);
    }

    public static boolean isValidCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String extension = getExtension(file.getOriginalFilename());
        if (extension == null || !"csv".equals(extension)) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return true;
        }

        return ALLOWED_CSV_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
    }

    private static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return null;
        }

        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean hasWebpSignature(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(12);
            if (header.length < 12) {
                return false;
            }

            return header[0] == 'R'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == 'F'
                    && header[8] == 'W'
                    && header[9] == 'E'
                    && header[10] == 'B'
                    && header[11] == 'P';
        } catch (IOException ex) {
            return false;
        }
    }
}
