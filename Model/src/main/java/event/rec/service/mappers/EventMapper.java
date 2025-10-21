package event.rec.service.mappers;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.entities.VenueEntity;

public final class EventMapper {

    public static EventEntity eventDtoToEventEntity(OrganizerEntity organizerEntity,
                                                    VenueEntity venueEntity,
                                                    EventDto event) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setTitle(event.title());
        eventEntity.setDescription(event.description());
        eventEntity.setStartTime(event.startTime());
        eventEntity.setEndTime(event.endTime());
        eventEntity.setRecurrence(event.recurrence());
        eventEntity.setStatus(event.status());
        eventEntity.setIdOrganizer(organizerEntity);
        eventEntity.setIdVenue(venueEntity);
        return eventEntity;
    }
}
