package gdsc.konkuk.platformcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PlatformCoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlatformCoreApplication.class, args);
  }
}
