package gdsc.konkuk.platformcore.external.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("spring.cloud.aws.s3")
public class S3Config {
  private final String bucket;
}
