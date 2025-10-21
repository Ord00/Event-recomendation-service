package event.rec.service.mappers;

import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.EventSubscriptionEntity;
import event.rec.service.responses.EventSubscriptionResponse;

import static event.rec.service.mappers.CategoryMapper.categoryEntityToDto;
import static event.rec.service.mappers.OrganizerMapper.organizerEntityToDTO;
import static event.rec.service.mappers.VenueMapper.venueEntityToDto;

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

    public static EventSubscriptionResponse eventSubscriptionEntityToResponse(EventSubscriptionEntity eventSubscription) {
        EventEntity event = eventSubscription.getIdEvent();
        return new EventSubscriptionResponse(event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getRecurrence(),
                event.getStatus(),
                organizerEntityToDTO(event.getIdOrganizer()),
                venueEntityToDto(event.getIdVenue()),
                event.getCategoryEvents()
                        .stream()
                        .map(categoryEvent -> categoryEntityToDto(categoryEvent.getIdCategory()))
                        .toList(),
                eventSubscription.getStatus(),
                eventSubscription.getNotifyTime()
                );
    }
}
