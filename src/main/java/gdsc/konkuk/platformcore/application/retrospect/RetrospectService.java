package gdsc.konkuk.platformcore.application.retrospect;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.retrospect.entity.Retrospect;
import gdsc.konkuk.platformcore.domain.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectService {
  private final RetrospectRepository retrospectRepository;

  @Transactional
  public Retrospect register(Event event, String content) {
    Retrospect newRetrospect = Retrospect.builder().eventId(event.getId()).content(content).build();
    return retrospectRepository.save(newRetrospect);
  }
}
