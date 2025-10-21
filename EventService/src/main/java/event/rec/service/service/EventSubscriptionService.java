package event.rec.service.service;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventSubscriptionEntity;
import event.rec.service.mappers.EventSubscriptionMapper;
import event.rec.service.repository.EventSubscriptionRepository;
import event.rec.service.requests.ViewFavouriteRequest;
import event.rec.service.responses.EventSubscriptionResponse;
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

        repository.save(EventSubscriptionMapper.eventSubscriptionDtoToEntity(
                future.get().value(),
                eventService.findById(eventSubscription.eventId()),
                eventSubscription
        ));
    }

    public void deleteFromFavourite(Long userId, Long eventId) {

        repository.findByUserIdAndEventId(userId, eventId)
                .ifPresent(repository::delete);
    }

    public List<EventSubscriptionResponse> viewFavourites(ViewFavouriteRequest request) {

        List<EventSubscriptionEntity> eventSubscriptions =
                repository.findByUserId(
                        request.userId(),
                        PageRequest.of(request.page(), request.size())
                );

        return eventSubscriptions.stream()
                .map(EventSubscriptionMapper::eventSubscriptionEntityToResponse)
                .toList();
    }
}
