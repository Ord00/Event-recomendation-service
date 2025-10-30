package event.rec.service.mappers;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.CategoryEntity;
import event.rec.service.entities.CategoryEventEntity;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.entities.VenueEntity;
import event.rec.service.responses.EventResponse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static event.rec.service.mappers.CategoryEventMapper.categoryEventDtoToEntity;
import static event.rec.service.mappers.CategoryMapper.categoryEntityToDto;
import static event.rec.service.mappers.OrganizerMapper.organizerEntityToDTO;
import static event.rec.service.mappers.VenueMapper.venueEntityToDto;

public final class EventMapper {

    public static EventEntity eventDtoToEventEntity(OrganizerEntity organizerEntity,
                                                    VenueEntity venueEntity,
                                                    List<CategoryEntity> categoryEntities,
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

        Set<CategoryEventEntity> categoryEvents = new LinkedHashSet<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            categoryEvents.add(categoryEventDtoToEntity(categoryEntity, eventEntity));
        }
        eventEntity.setCategoryEvents(categoryEvents);

        return eventEntity;
    }

    public static EventResponse eventEntityToResponse(EventEntity event) {
        return new EventResponse(event.getId(),
                event.getTitle(),
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
                        .toList()
        );
    }
}
