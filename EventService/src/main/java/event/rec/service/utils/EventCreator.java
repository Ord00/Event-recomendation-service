package event.rec.service.utils;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.service.CategoryService;
import event.rec.service.service.VenueService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static event.rec.service.mappers.EventMapper.eventDtoToEventEntity;

@Component
@RequiredArgsConstructor
public class EventCreator {

    private final VenueService venueService;
    private final CategoryService categoryService;

    private final EntityManager entityManager;

    public EventEntity createEvent(EventDto event) {

        return eventDtoToEventEntity(
                entityManager.getReference(OrganizerEntity.class, event.organizerId()),
                venueService.findById(event.venueId()),
                event.categoryIds().stream().map(categoryService::findById).toList(),
                event);
    }
}
