package event.rec.service.service;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static event.rec.service.mappers.EventMapper.EventDtoToEventEntity;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueService venueService;

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

        return eventRepository.save(EventDtoToEventEntity(
                future.get().value(),
                venueService.findById(event.venueId()),
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

        EventEntity entity = EventDtoToEventEntity(
                future.get().value(),
                venueService.findById(event.venueId()),
                event);
        entity.setId(eventId);

        return eventRepository.save(entity);
    }

    public EventEntity findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }
}
