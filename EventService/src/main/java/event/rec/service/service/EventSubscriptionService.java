package event.rec.service.service;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventSubscriptionEntity;
import event.rec.service.mappers.EventSubscriptionMapper;
import event.rec.service.repository.EventSubscriptionRepository;
import event.rec.service.requests.ViewFavouriteRequest;
import event.rec.service.responses.EventSubscriptionResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSubscriptionService {

    private final EventSubscriptionRepository repository;
    private final EventService eventService;

    private final EntityManager entityManager;

    public void addToFavourite(EventSubscriptionDto eventSubscription,
                               UUID userId) {

        repository.save(EventSubscriptionMapper.eventSubscriptionDtoToEntity(
                entityManager.getReference(CommonUserEntity.class, userId),
                eventService.findById(eventSubscription.eventId()),
                eventSubscription
        ));
    }

    public void deleteFromFavourite(UUID userId, Long eventId) {

        repository.findByUserIdAndEventId(userId, eventId)
                .ifPresent(repository::delete);
    }

    public List<EventSubscriptionResponse> viewFavourites(ViewFavouriteRequest request,
                                                          UUID userId) {

        List<EventSubscriptionEntity> eventSubscriptions =
                repository.findByUserId(
                        userId,
                        PageRequest.of(request.page(), request.size())
                );

        return eventSubscriptions.stream()
                .map(EventSubscriptionMapper::eventSubscriptionEntityToResponse)
                .toList();
    }
}
