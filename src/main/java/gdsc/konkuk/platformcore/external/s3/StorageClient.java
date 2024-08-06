package gdsc.konkuk.platformcore.external.s3;

import static java.util.UUID.randomUUID;

import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
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
  private final S3Config s3Config;
  private final S3Template s3Template;

  public List<String> uploadFiles(List<MultipartFile> files, Consumer<MultipartFile> fileValidator)
      throws IOException {
    List<String> objectKeyList = new ArrayList<>();
    if (files == null) {
      throw new IllegalArgumentException("files is null");
    }

    for (MultipartFile file : files) {
      fileValidator.accept(file);
      String extension =
          Objects.requireNonNull(file.getOriginalFilename())
              .substring(file.getOriginalFilename().lastIndexOf("."));

      String objectKey = randomUUID() + extension;
      s3Template.upload(s3Config.getBucket(), objectKey, file.getInputStream());
      objectKeyList.add(objectKey);
    }
    return objectKeyList;
  }

  public URL getDownloadUrl(String objectKey) throws IOException {
    return s3Template.createSignedGetURL(s3Config.getBucket(), objectKey, Duration.ofMinutes(10));
  }

  public List<StorageObject> getObjects(List<String> objectKeyList) throws IOException {
    List<StorageObject> preSignedUrls = new ArrayList<>();
    for (String objectKey : objectKeyList) {
      preSignedUrls.add(
          StorageObject.builder()
              .key(objectKey)
              .url(
                  s3Template.createSignedGetURL(
                      s3Config.getBucket(), objectKey, Duration.ofMinutes(10)))
              .build());
    }
    return preSignedUrls;
  }

  public void deleteFiles(List<String> objectKeyList) {
    for (String objectKey : objectKeyList) {
      s3Template.deleteObject(s3Config.getBucket(), objectKey);
    }
  }
}
