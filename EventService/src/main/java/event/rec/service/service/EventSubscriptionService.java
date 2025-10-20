package event.rec.service.service;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.repository.EventSubscriptionRepository;
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

import static event.rec.service.mappers.EventSubscriptionMapper.EventSubscriptionDtoToEntity;

@Service
@RequiredArgsConstructor
public class EventSubscriptionService {

    private final EventSubscriptionRepository repository;
    private final EventService eventService;

    private final ReplyingKafkaTemplate<String, Long, CommonUserEntity> findUserTemplate;
    @Value("${kafka.topics.find.by.id.common.request}")
    private String findUserRequestTopic;
    @Value("${kafka.topics.find.by.id.common.response}")
    private String findUserResponseTopic;

    private RequestReplyFuture<String, Long, CommonUserEntity> getUser(Long userId) {
        ProducerRecord<String, Long> record = new ProducerRecord<>(
                findUserRequestTopic,
                userId
        );

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                findUserResponseTopic.getBytes()
        ));

        return findUserTemplate.sendAndReceive(record, Duration.ofSeconds(5));
    }

    public void addToFavourite(EventSubscriptionDto eventSubscription) throws ExecutionException, InterruptedException {

        RequestReplyFuture<String, Long, CommonUserEntity> future = getUser(eventSubscription.userId());

        repository.save(EventSubscriptionDtoToEntity(
                future.get().value(),
                eventService.findById(eventSubscription.eventId()),
                eventSubscription
        ));
    }

    public void deleteFromFavourite(Long userId, Long eventId) throws ExecutionException, InterruptedException {

        RequestReplyFuture<String, Long, CommonUserEntity> future = getUser(userId);

        repository.delete(EventSubscriptionDtoToEntity(
                future.get().value(),
                eventService.findById(eventId)
        ));
    }
}
