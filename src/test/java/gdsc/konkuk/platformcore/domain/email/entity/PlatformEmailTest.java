package gdsc.konkuk.platformcore.domain.email.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlatformEmailTest {

  @Test
  @DisplayName("이메일 수신자 리스트 추가 성공")
  void should_add_all_receivers() {
      //given
      PlatformEmail platformEmail = new PlatformEmail("example", "example");
      List<String> receiverList = List.of("example@gmail.com", "example2@gmail.com");
      //when
      platformEmail.addReceivers(receiverList);
      //then
      assertEquals(receiverList.size(), platformEmail.getReceivers().size());
  }

}