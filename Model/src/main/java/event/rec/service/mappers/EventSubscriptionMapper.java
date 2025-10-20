package event.rec.service.mappers;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.EventSubscriptionEntity;

public final class EventSubscriptionMapper {

    private static final String status = "ACTIVE";

    public static EventSubscriptionEntity EventSubscriptionDtoToEntity(CommonUserEntity commonUserEntity,
                                                                       EventEntity eventEntity) {

        EventSubscriptionEntity eventSubscriptionEntity = new EventSubscriptionEntity();
        eventSubscriptionEntity.setIdUser(commonUserEntity);
        eventSubscriptionEntity.setIdEvent(eventEntity);
        return eventSubscriptionEntity;
    }

    public static EventSubscriptionEntity EventSubscriptionDtoToEntity(CommonUserEntity commonUserEntity,
                                                                       EventEntity eventEntity,
                                                                       EventSubscriptionDto eventSubscription) {

        EventSubscriptionEntity eventSubscriptionEntity = EventSubscriptionDtoToEntity(commonUserEntity, eventEntity);
        eventSubscriptionEntity.setStatus(status);
        eventSubscriptionEntity.setNotifyTime(eventSubscription.notifyTime());
        return eventSubscriptionEntity;
    }
}
