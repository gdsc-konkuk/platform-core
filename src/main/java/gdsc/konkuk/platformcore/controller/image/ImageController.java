package gdsc.konkuk.platformcore.controller.image;

import gdsc.konkuk.platformcore.application.image.ImageService;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
  private final ImageService imageService;

  @PostMapping("/{fileName}/upload-url")
  public ResponseEntity<SuccessResponse> getUploadUrl(@PathVariable String fileName)
      throws URISyntaxException {
    URL uploadUrl = imageService.getUploadUrl(fileName);
    return ResponseEntity.created(uploadUrl.toURI()).body(SuccessResponse.messageOnly());
  }

  @PostMapping("/{fileName}/download-url")
  public ResponseEntity<SuccessResponse> getDownloadUrl(@PathVariable String fileName)
      throws URISyntaxException {
    URL downloadUrl = imageService.getDownloadUrl(fileName);
    return ResponseEntity.created(downloadUrl.toURI()).body(SuccessResponse.messageOnly());
  }
}
