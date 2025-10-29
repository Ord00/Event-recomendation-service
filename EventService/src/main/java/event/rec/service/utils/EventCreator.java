package event.rec.service.utils;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.service.CategoryService;
import event.rec.service.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static event.rec.service.mappers.EventMapper.eventDtoToEventEntity;

@Component
@RequiredArgsConstructor
public class EventCreator {

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

        return eventDtoToEventEntity(
                future.get().value(),
                venueService.findById(event.venueId()),
                event.categoryIds().stream().map(categoryService::findById).toList(),
                event);
    }
}
