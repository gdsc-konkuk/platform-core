package gdsc.konkuk.platformcore.external.s3;

import static java.util.UUID.randomUUID;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  public List<String> uploadFiles(
      List<MultipartFile> files, Consumer<MultipartFile> fileValidator) {
    List<String> objectKeyList = new ArrayList<>();

    for (MultipartFile file : files) {
      fileValidator.accept(file);
      String objectKey = randomUUID().toString();
      s3Template.store(s3Config.getBucket(), objectKey, file);
      objectKeyList.add(objectKey);
    }
    return objectKeyList;
  }

  public File downloadFile(String objectKey) throws IOException {
    S3Resource s3Resource = s3Template.download(s3Config.getBucket(), objectKey);
    return s3Resource.getFile();
  }

  public List<File> downloadFiles(List<String> objectKeyList) throws IOException {
    List<File> files = new ArrayList<>();
    for (String objectKey : objectKeyList) {
      S3Resource s3Resource = s3Template.download(s3Config.getBucket(), objectKey);
      files.add(s3Resource.getFile());
    }
    return files;
  }

  public void deleteFiles(List<String> objectKeyList) {
    for (String objectKey : objectKeyList) {
      s3Template.deleteObject(s3Config.getBucket(), objectKey);
    }
  }
}
