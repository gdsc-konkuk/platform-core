package gdsc.konkuk.platformcore.application.image;

import gdsc.konkuk.platformcore.external.image.S3Config;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
  private final S3Config s3Config;
  private final S3Template s3Template;

  public URL getUploadUrl(String fileName) {
    return s3Template.createSignedPutURL(s3Config.getBucket(), fileName, Duration.ofMinutes(10));
  }

  public URL getDownloadUrl(String fileName) {
    return s3Template.createSignedGetURL(s3Config.getBucket(), fileName, Duration.ofMinutes(10));
  }
}
