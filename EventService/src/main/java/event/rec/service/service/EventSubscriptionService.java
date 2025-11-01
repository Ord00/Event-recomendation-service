package event.rec.service.service;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventSubscriptionEntity;
import event.rec.service.mappers.EventSubscriptionMapper;
import event.rec.service.repository.EventSubscriptionRepository;
import event.rec.service.requests.ViewFavouriteRequest;
import event.rec.service.responses.EventSubscriptionResponse;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static event.rec.service.utils.KafkaRequestSender.findUserId;

@Service
public class EventSubscriptionService {

    private final EventSubscriptionRepository repository;
    private final EventService eventService;

    private final EntityManager entityManager;

    private final ReplyingKafkaTemplate<String, String, Long> findUserIdTemplate;

    @Value("${kafka.topics.find.by.username.common.request}")
    private String findUserIdRequestTopic;
    @Value("${kafka.topics.find.by.username.common.response}")
    private String findUserIdResponseTopic;

    public EventSubscriptionService(EventSubscriptionRepository repository,
                                    EventService eventService,
                                    EntityManager entityManager,
                                    @Qualifier("findCommonUserTemplate")
                                    ReplyingKafkaTemplate<String, String, Long> findUserIdTemplate) {
        this.repository = repository;
        this.eventService = eventService;
        this.entityManager = entityManager;
        this.findUserIdTemplate = findUserIdTemplate;
    }

    private RequestReplyFuture<String, String, Long> getUserId(String login) {

        return findUserId(
                findUserIdRequestTopic,
                findUserIdResponseTopic,
                login,
                findUserIdTemplate);
    }

    public void addToFavourite(EventSubscriptionDto eventSubscription)
            throws ExecutionException, InterruptedException {

        repository.save(EventSubscriptionMapper.eventSubscriptionDtoToEntity(
                entityManager.getReference(CommonUserEntity.class,
                        getUserId(eventSubscription.username()).get().value()),
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
