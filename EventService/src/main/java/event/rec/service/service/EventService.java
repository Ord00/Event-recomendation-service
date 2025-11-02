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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static event.rec.service.mappers.EventMapper.eventEntityToResponse;
import static event.rec.service.utils.KafkaRequestSender.findUserId;
import static event.rec.service.utils.RegexPatternBuilder.buildSearchEventPattern;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventCreator eventCreator;

    private final EntityManager entityManager;

    private final ReplyingKafkaTemplate<String, String, Long> findOrganizerIdTemplate;
    @Value("${kafka.topics.find.by.username.organizer.request}")
    private String findOrganizerIdRequestTopic;
    @Value("${kafka.topics.find.by.username.organizer.response}")
    private String findOrganizerIdReplyTopic;

    public EventService(EventRepository eventRepository,
                        EventCreator eventCreator,
                        EntityManager entityManager,
                        @Qualifier("findOrganizerTemplate")
                        ReplyingKafkaTemplate<String, String, Long> findOrganizerIdTemplate) {
        this.eventRepository = eventRepository;
        this.eventCreator = eventCreator;
        this.entityManager = entityManager;
        this.findOrganizerIdTemplate = findOrganizerIdTemplate;
    }

    public EventResponse createEvent(EventDto event, String organizerName)
            throws ExecutionException, InterruptedException {

        RequestReplyFuture<String, String, Long> future = findUserId(
                findOrganizerIdRequestTopic,
                findOrganizerIdReplyTopic,
                organizerName,
                findOrganizerIdTemplate);

        return eventEntityToResponse(eventCreator.createEvent(
                event,
                entityManager.getReference(OrganizerEntity.class, future.get().value()))
        );
    }

    public void deleteEvent(Long eventId) {

        eventRepository.deleteById(eventId);

    }

    public EventResponse updateEvent(Long eventId,
                                     EventDto event,
                                     String organizerName)
            throws
            IllegalArgumentException,
            ExecutionException,
            InterruptedException {

        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found");
        }

        RequestReplyFuture<String, String, Long> future = findUserId(
                findOrganizerIdRequestTopic,
                findOrganizerIdReplyTopic,
                organizerName,
                findOrganizerIdTemplate);

        try {
            EventEntity entity = eventCreator.createEvent(event,
                    entityManager.getReference(OrganizerEntity.class, future.get().value())
            );
            entity.setId(eventId);

            return eventEntityToResponse(eventRepository.save(entity));

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Organizer not found: " + organizerName);
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
