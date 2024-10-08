package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.RetrospectUpdateRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  @GetMapping
  public ResponseEntity<SuccessResponse> getAll() {
    EventBriefResponse eventBriefs = eventService.getAllBriefs();
    return ResponseEntity.ok(SuccessResponse.of(eventBriefs));
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<SuccessResponse> getEvent(@PathVariable Long eventId) {
    EventDetailResponse eventDetailResponse = eventService.getEvent(eventId);
    return ResponseEntity.ok(SuccessResponse.of(eventDetailResponse));
  }

  @PostMapping
  public ResponseEntity<SuccessResponse> register(
      @RequestPart("detail") @Valid EventRegisterRequest registerRequest,
      @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles)
      throws IOException {
    Event newEvent = eventService.register(registerRequest, imageFiles);
    return ResponseEntity.created(getCreatedURI(newEvent.getId()))
        .body(SuccessResponse.messageOnly());
  }

  @PatchMapping("/{eventId}")
  public ResponseEntity<SuccessResponse> update(
      @PathVariable Long eventId,
      @RequestPart(value = "detail") @Valid EventUpdateRequest updateRequest,
      @RequestPart(value = "new-images", required = false) List<MultipartFile> files)
      throws IOException {
    eventService.update(eventId, updateRequest, files);
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }

  @PatchMapping("/{eventId}/retrospect")
  public ResponseEntity<SuccessResponse> updateRetrospect(
      @PathVariable Long eventId, @RequestBody @Valid RetrospectUpdateRequest updateRequest) {
    eventService.updateRetrospect(eventId, updateRequest.getContent());
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<SuccessResponse> delete(@PathVariable Long eventId) {
    eventService.delete(eventId);
    return ResponseEntity.noContent().build();
  }

  private URI getCreatedURI(Long memberId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(memberId)
        .toUri();
  }
}
