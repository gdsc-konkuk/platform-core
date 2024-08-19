package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
import gdsc.konkuk.platformcore.external.s3.StorageClient;
import gdsc.konkuk.platformcore.global.utils.FileValidator;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
  private final EventRepository eventRepository;
  private final StorageClient storageClient;

  public EventDetailResponse getEvent(Long eventId) {
    Event event = findById(eventId);
    return EventDetailResponse.fromEntity(event);
  }

  public EventBriefResponse getAllBriefs() {
    List<Event> events = eventRepository.findAll();
    List<EventBrief> eventBriefs = EventMapper.mapEventListToEventBriefList(events, storageClient);
    return EventBriefResponse.builder().eventBriefs(eventBriefs).build();
  }

  @Transactional
  public Event register(EventRegisterRequest registerRequest, List<MultipartFile> imageFiles)
      throws IOException {
    Event newEvent = eventRepository.saveAndFlush(EventRegisterRequest.toEntity(registerRequest));

    // upload images
    if (imageFiles != null) {
      List<URL> eventImageUrls =
          storageClient.uploadFiles(imageFiles, FileValidator::validateFileMimeTypeImage);
      for (URL imageUrl : eventImageUrls) {
        newEvent.addEventImageByUrl(imageUrl);
      }
    }

    return newEvent;
  }

  public List<EventWithAttendance> getEventsOfTheMonthWithAttendance(LocalDate month) {
    return eventRepository.findAllWithAttendanceByStartAtBetween(
        month.withDayOfMonth(1).atStartOfDay(),
        month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX));
  }

  @Transactional
  public void update(Long eventId, EventUpdateRequest request, List<MultipartFile> newImageFiles)
      throws IOException {
    Event event = findById(eventId);
    event.update(
        request.getTitle(), request.getContent(), request.getLocation(), request.getStartAt(), request.getEndAt());

    // upload new images
    if (newImageFiles != null) {
      List<URL> eventImageUrls =
          storageClient.uploadFiles(newImageFiles, FileValidator::validateFileMimeTypeImage);
      for (URL imageUrl : eventImageUrls) {
        event.addEventImageByUrl(imageUrl);
      }
    }

    // delete images
    if (request.getEventImagesToDelete() != null) {
      for (URL imageUrl : request.getEventImagesToDelete()) {
        event.deleteEventImageByUrl(imageUrl);
      }
      storageClient.deleteFiles(request.getEventImagesToDelete());
    }
  }

  @Transactional
  public void updateRetrospect(Long eventId, String content) {
    Event event = findById(eventId);
    event.updateRetrospectContent(content);
  }

  private Event findById(Long eventId) {
    return eventRepository
        .findById(eventId)
        .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));
  }
}
