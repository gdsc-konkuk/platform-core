package gdsc.konkuk.platformcore.external.s3;

import static java.util.UUID.randomUUID;

import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageClient {
  private final S3Properties s3Properties;
  private final S3Template s3Template;

  public List<URL> uploadFiles(List<MultipartFile> files, Consumer<MultipartFile> fileValidator)
      throws IOException {
    List<URL> objectUrls = new ArrayList<>();

    if (files == null) throw new IllegalArgumentException("files is null");

    for (MultipartFile file : files) {
      fileValidator.accept(file);
      String objectKey = genObjectKey(file.getOriginalFilename());
      s3Template.upload(s3Properties.getBucket(), objectKey, file.getInputStream());
      objectUrls.add(genObjectUrl(objectKey));
    }
    return objectUrls;
  }

  public void deleteFiles(List<URL> objectUrls) {
    if (objectUrls == null) throw new IllegalArgumentException("objectUrls is null");

    for (URL objectUrl : objectUrls) {
      s3Template.deleteObject(s3Properties.getBucket(), extractObjectKeyFromUrl(objectUrl));
    }
  }

  private String extractObjectKeyFromUrl(URL url) {
    String baseUrl =
        "https://"
            + s3Properties.getBucket()
            + ".s3."
            + s3Properties.getRegion()
            + ".amazonaws.com/";

    if (url.toString().startsWith(baseUrl)) {
      return url.toString().substring(baseUrl.length());
    } else {
      throw new IllegalArgumentException("주어진 URL이 S3 Object URL이 아닙니다.");
    }
  }

  private URL genObjectUrl(String objectKey) throws MalformedURLException {
    return new URL(
        "https://"
            + s3Properties.getBucket()
            + ".s3."
            + s3Properties.getRegion()
            + ".amazonaws.com/"
            + objectKey);
  }

  private String genObjectKey(String filename) {
    return randomUUID() + extractExtension(filename);
  }

  private String extractExtension(String filename) {
    return Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
  }
}
