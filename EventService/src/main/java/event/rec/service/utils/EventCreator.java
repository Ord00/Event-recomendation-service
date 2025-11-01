package event.rec.service.utils;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.CategoryEntity;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.repository.EventRepository;
import event.rec.service.service.CategoryEventService;
import event.rec.service.service.CategoryService;
import event.rec.service.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static event.rec.service.mappers.CategoryEventMapper.categoryEventDtoToEntity;
import static event.rec.service.mappers.EventMapper.eventDtoToEventEntity;

@Component
@RequiredArgsConstructor
public class EventCreator {

    private final EventRepository eventRepository;

    private final VenueService venueService;
    private final CategoryService categoryService;
    private final CategoryEventService categoryEventService;

    @Transactional
    public EventEntity createEvent(EventDto event, OrganizerEntity organizer) {

        List<CategoryEntity> categoryEntities = event.categoryIds()
                .stream()
                .map(categoryService::findById)
                .toList();

        EventEntity eventEntity = eventRepository.save(
                eventDtoToEventEntity(
                        organizer,
                        venueService.findById(event.venueId()),
                        event)
        );

        eventEntity.setCategoryEvents(categoryEntities
                .stream()
                .map(categoryEntity -> categoryEventService
                        .save(categoryEventDtoToEntity(categoryEntity, eventEntity)))
                .collect(Collectors.toSet()));

        return eventEntity;
    }
}
