package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.entity.EventImage;
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
  private final AttendanceRepository attendanceRepository;
  private final AttendanceService attendanceService;

  public EventDetailResponse getEvent(Long eventId) {
    Event event = findById(eventId);
    return EventDetailResponse.fromEntity(event);
  }

  public EventBriefResponse getAllBriefs() {
    List<Event> events = eventRepository.findAll();
    List<EventBrief> eventBriefs = EventMapper.mapEventListToEventBriefList(events);
    return EventBriefResponse.builder().eventBriefs(eventBriefs).build();
  }

  @Transactional
  public Event register(EventRegisterRequest registerRequest, List<MultipartFile> imageFiles)
      throws IOException {
    Event newEvent = eventRepository.saveAndFlush(EventRegisterRequest.toEntity(registerRequest));
    uploadImages(newEvent, imageFiles);
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

    uploadImages(event, newImageFiles);
    deleteImages(event, request.getEventImagesToDelete());
  }

  @Transactional
  public void updateRetrospect(Long eventId, String content) {
    Event event = findById(eventId);
    event.updateRetrospectContent(content);
  }

  @Transactional
  public void delete(Long eventId) {
    Event event = findById(eventId);

    attendanceRepository.findByEventId(eventId)
        .ifPresent(attendance -> attendanceService.deleteAttendance(attendance.getId()));
    eventRepository.delete(event);

    List<EventImage> eventImages = event.getEventImageList();
    List<URL> imageUrls = eventImages.stream().map(EventImage::getUrl).toList();
    storageClient.deleteFiles(imageUrls);
  }

  private Event findById(Long eventId) {
    return eventRepository
        .findById(eventId)
        .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));
  }

  private void uploadImages(Event event, List<MultipartFile> imageFiles) throws IOException {
    if (imageFiles == null) return;

    List<URL> eventImageUrls =
        storageClient.uploadFiles(imageFiles, FileValidator::validateFileMimeTypeImage);
    for (URL imageUrl : eventImageUrls) {
      event.addEventImageByUrl(imageUrl);
    }
  }

  private void deleteImages(Event event, List<URL> imageUrls) {
    if (imageUrls == null) return;

    for (URL imageUrl : imageUrls) {
      event.deleteEventImageByUrl(imageUrl);
    }
    storageClient.deleteFiles(imageUrls);
  }
}
