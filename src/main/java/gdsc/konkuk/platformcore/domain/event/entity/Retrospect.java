package gdsc.konkuk.platformcore.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Retrospect {
  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Builder
  public Retrospect(String content) {
    this.content = validateNotNull(content, "content");
  }

  public void updateContent(String content) {
    this.content = validateNotNull(content, "content");
  }
}
