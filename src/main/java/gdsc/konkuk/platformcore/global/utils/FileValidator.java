package gdsc.konkuk.platformcore.global.utils;

import jakarta.activation.MimetypesFileTypeMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileValidator {
  public static void validateFileMimeType(MultipartFile file, String mimeType) {
    String fileName = file.getOriginalFilename();
    String mimetype = new MimetypesFileTypeMap().getContentType(fileName);
    if (!mimetype.startsWith(mimeType + "/")) {
      throw new IllegalArgumentException(fileName + " must be " + mimeType + " file");
    }
  }

  public static void validateFileMimeTypeImage(MultipartFile file) {
    validateFileMimeType(file, "image");
  }
}
