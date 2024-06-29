package gdsc.konkuk.platformcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlatformCoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlatformCoreApplication.class, args);
    System.out.println("TESTCI");
  }
}
