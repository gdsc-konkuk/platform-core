package gdsc.konkuk.platformcore.external.s3;

import java.net.URL;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StorageObject {
  private String key;
  private URL url;
}
