package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.controller.event.EventBriefResponse;
import gdsc.konkuk.platformcore.controller.event.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.EventUpdateRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
import gdsc.konkuk.platformcore.domain.event.entity.Retrospect;
import gdsc.konkuk.platformcore.external.s3.StorageClient;
import gdsc.konkuk.platformcore.external.s3.StorageObject;
import gdsc.konkuk.platformcore.global.utils.FileValidator;
import java.io.IOException;
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

  public EventDetailResponse getEvent(Long eventId) throws IOException {
    Event event = findById(eventId);
    List<StorageObject> images = storageClient.getObjects(event.getEventImageKeys());

    return EventDetailResponse.builder()
        .id(event.getId())
        .title(event.getTitle())
        .content(event.getContent())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .images(images)
        .build();
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
      List<String> eventImageKeyList =
          storageClient.uploadFiles(imageFiles, FileValidator::validateFileMimeTypeImage);
      for (String imageKey : eventImageKeyList) {
        newEvent.addEventImageByKey(imageKey);
      }
    }

    return newEvent;
  }

  public List<EventWithAttendance> getEventsOfTheMonthWithAttendance(LocalDate month) {
    return eventRepository.findAllWithAttendanceByStartAtBetween(
        month.withDayOfMonth(1).atStartOfDay(),
        month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX));
  }

  public Retrospect getRetrospect(Long eventId) {
    Event event = findById(eventId);
    return event.getRetrospect();
  }

  @Transactional
  public void update(Long eventId, EventUpdateRequest request, List<MultipartFile> newImageFiles)
      throws IOException {
    Event event = findById(eventId);
    event.update(
        request.getTitle(), request.getContent(), request.getStartAt(), request.getEndAt());

    // upload new images
    if (newImageFiles != null) {
      List<String> eventImageKeyList =
          storageClient.uploadFiles(newImageFiles, FileValidator::validateFileMimeTypeImage);
      for (String imageKey : eventImageKeyList) {
        event.addEventImageByKey(imageKey);
      }
    }

    // delete images
    if (request.getEventImageKeysToDelete() != null) {
      for (String imageKey : request.getEventImageKeysToDelete()) {
        event.deleteEventImageByKey(imageKey);
      }
      storageClient.deleteFiles(request.getEventImageKeysToDelete());
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
