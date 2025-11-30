package event.rec.service.service;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.mappers.EventMapper;
import event.rec.service.repository.EventRepository;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.responses.EventResponse;
import event.rec.service.utils.EventCreator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static event.rec.service.mappers.EventMapper.eventEntityToResponse;
import static event.rec.service.utils.RegexPatternBuilder.buildSearchEventPattern;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventCreator eventCreator;

    private final EntityManager entityManager;

    public EventResponse createEvent(EventDto event, UUID organizerId) {

        return eventEntityToResponse(eventCreator.createEvent(
                event,
                entityManager.getReference(OrganizerEntity.class, organizerId)
                )
        );
    }

    public void deleteEvent(Long eventId) {

        eventRepository.deleteById(eventId);

    }

    public EventResponse updateEvent(Long eventId,
                                     EventDto event,
                                     UUID organizerId) throws IllegalArgumentException {

        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found");
        }

        try {
            EventEntity entity = eventCreator.createEvent(event,
                    entityManager.getReference(OrganizerEntity.class, organizerId)
            );
            entity.setId(eventId);

            return eventEntityToResponse(eventRepository.save(entity));

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Organizer not found: " + organizerId);
        }
    }

    public EventEntity findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<EventResponse> searchEvents(SearchEventRequest request) {

        List<Long> eventIds = eventRepository.searchEventIds(
                buildSearchEventPattern(request.query()),
                request.categoryIds(),
                request.from(),
                request.to(),
                request.size(),
                (request.page() - 1) * request.size());

        return eventRepository.searchEvent(eventIds).stream()
                .map(EventMapper::eventEntityToResponse)
                .toList();
    }

    public List<EventResponse> viewEventNearby(ViewEventNearbyRequest request) {

        return eventRepository.findEventNearby(request.categoryIds(),
                        request.interval(),
                        request.location(),
                        request.radius()).stream()
                .map(EventMapper::eventEntityToResponse)
                .toList();
    }
}
