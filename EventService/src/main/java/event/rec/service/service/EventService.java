package event.rec.service.service;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.mappers.EventMapper;
import event.rec.service.repository.EventRepository;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.responses.EventResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static event.rec.service.mappers.EventMapper.eventDtoToEventEntity;
import static event.rec.service.utils.RegexPatternBuilder.buildSearchEventPattern;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueService venueService;
    private final CategoryService categoryService;

    private final ReplyingKafkaTemplate<String, Long, OrganizerEntity> findOrganizerTemplate;
    @Value("${kafka.topics.find.by.id.organizer.request}")
    private String findOrganizerRequestTopic;
    @Value("${kafka.topics.find.by.id.organizer.response}")
    private String findOrganizerReplyTopic;

    public EventEntity createEvent(EventDto event) throws ExecutionException, InterruptedException {

        ProducerRecord<String, Long> record = new ProducerRecord<>(
                findOrganizerRequestTopic,
                event.organizerId()
        );

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                findOrganizerReplyTopic.getBytes()
        ));

        RequestReplyFuture<String, Long, OrganizerEntity> future =
                findOrganizerTemplate.sendAndReceive(record, Duration.ofSeconds(5));

        return eventRepository.save(eventDtoToEventEntity(
                future.get().value(),
                venueService.findById(event.venueId()),
                event.categoryIds().stream().map(categoryService::findById).toList(),
                event));
    }

    public void deleteEvent(Long eventId) {

        eventRepository.deleteById(eventId);

    }

    public EventEntity updateEvent(Long eventId, EventDto event)
            throws
            ExecutionException,
            InterruptedException,
            IllegalArgumentException {

        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found");
        }

        ProducerRecord<String, Long> record = new ProducerRecord<>(
                findOrganizerRequestTopic,
                event.organizerId()
        );

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                findOrganizerReplyTopic.getBytes()
        ));

        RequestReplyFuture<String, Long, OrganizerEntity> future =
                findOrganizerTemplate.sendAndReceive(record, Duration.ofSeconds(5));

        EventEntity entity = eventDtoToEventEntity(
                future.get().value(),
                venueService.findById(event.venueId()),
                event.categoryIds().stream().map(categoryService::findById).toList(),
                event);
        entity.setId(eventId);

        return eventRepository.save(entity);
    }

    public EventEntity findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<EventEntity> searchEvents(SearchEventRequest request) {

        return eventRepository.searchEvent(buildSearchEventPattern(request.query()),
                request.categoryIds(),
                request.from(),
                request.to(),
                PageRequest.of(request.page(), request.size())
        );
    }

    public List<EventResponse> viewEventNearby(ViewEventNearbyRequest request) {

        return eventRepository.findEventNearby(request.categoryIds(),
                        request.interval(),
                        request.location(),
                        request.radius()).stream()
                .map(EventMapper::eventEntityToResponse)
                .toList();
    }
}
