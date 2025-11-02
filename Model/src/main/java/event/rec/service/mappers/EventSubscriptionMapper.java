package event.rec.service.mappers;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.EventSubscriptionEntity;
import event.rec.service.responses.EventSubscriptionResponse;

import static event.rec.service.mappers.EventMapper.eventEntityToResponse;

public final class EventSubscriptionMapper {

    private static final String status = "ACTIVE";

    public static EventSubscriptionEntity eventSubscriptionDtoToEntity(CommonUserEntity commonUserEntity,
                                                                       EventEntity eventEntity) {

        EventSubscriptionEntity eventSubscriptionEntity = new EventSubscriptionEntity();
        eventSubscriptionEntity.setIdUser(commonUserEntity);
        eventSubscriptionEntity.setIdEvent(eventEntity);
        return eventSubscriptionEntity;
    }

    public static EventSubscriptionEntity eventSubscriptionDtoToEntity(CommonUserEntity commonUserEntity,
                                                                       EventEntity eventEntity,
                                                                       EventSubscriptionDto eventSubscription) {

        EventSubscriptionEntity eventSubscriptionEntity = eventSubscriptionDtoToEntity(commonUserEntity, eventEntity);
        eventSubscriptionEntity.setStatus(status);
        eventSubscriptionEntity.setNotifyTime(eventSubscription.notifyTime());
        return eventSubscriptionEntity;
    }

    public static EventSubscriptionResponse eventSubscriptionEntityToResponse(
            EventSubscriptionEntity eventSubscription) {
        return new EventSubscriptionResponse(eventEntityToResponse(eventSubscription.getIdEvent()),
                eventSubscription.getStatus(),
                eventSubscription.getNotifyTime()
                );
    }
}
