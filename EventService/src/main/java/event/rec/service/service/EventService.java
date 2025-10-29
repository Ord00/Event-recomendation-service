package event.rec.service.service;

import event.rec.service.dto.EventDto;
import event.rec.service.entities.EventEntity;
import event.rec.service.mappers.EventMapper;
import event.rec.service.repository.EventRepository;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.responses.EventResponse;
import event.rec.service.utils.EventCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static event.rec.service.utils.RegexPatternBuilder.buildSearchEventPattern;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventCreator eventCreator;

    public EventEntity createEvent(EventDto event) throws ExecutionException, InterruptedException {
        return eventCreator.createEvent(event);
    }

    public void deleteEvent(Long eventId) {

        eventRepository.deleteById(eventId);

    }

    public EventEntity updateEvent(Long eventId, EventDto event)
            throws
            ExecutionException,
            InterruptedException,
            IllegalArgumentException {

        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found");
        }

        EventEntity entity = eventCreator.createEvent(event);
        entity.setId(eventId);

        return eventRepository.save(entity);
    }

    public EventEntity findById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<EventEntity> searchEvents(SearchEventRequest request) {

        return eventRepository.searchEvent(buildSearchEventPattern(request.query()),
                request.categoryIds(),
                request.from(),
                request.to(),
                PageRequest.of(request.page(), request.size())
        );
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
