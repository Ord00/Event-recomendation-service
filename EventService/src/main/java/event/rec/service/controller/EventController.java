package event.rec.service.controller;

import event.rec.service.dto.EventDto;
import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.requests.ViewFavouriteRequest;
import event.rec.service.service.EventService;
import event.rec.service.service.EventSubscriptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final EventSubscriptionService eventSubscriptionService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDto event,
                                         @AuthenticationPrincipal Jwt jwt) {
        try {

            return ResponseEntity.ok(eventService.createEvent(event, UUID.fromString(jwt.getSubject())));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId,
                                         @RequestBody EventDto event,
                                         @AuthenticationPrincipal Jwt jwt) {
        try {

            return ResponseEntity.ok(eventService.updateEvent(eventId, event, UUID.fromString(jwt.getSubject())));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/favourite")
    public ResponseEntity<?> addToFavourite(@RequestBody EventSubscriptionDto eventSubscription,
                                            @AuthenticationPrincipal Jwt jwt) {
        try {

            eventSubscriptionService.addToFavourite(eventSubscription, UUID.fromString(jwt.getSubject()));
            return ResponseEntity.noContent().build();

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/favourite/{eventId}")
    public ResponseEntity<?> deleteFromFavourite(@PathVariable Long eventId,
                                                 @AuthenticationPrincipal Jwt jwt) {
        try {

            eventSubscriptionService.deleteFromFavourite(UUID.fromString(jwt.getSubject()), eventId);
            return ResponseEntity.noContent().build();

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/favourite")
    public ResponseEntity<?> viewFavourite(@ModelAttribute ViewFavouriteRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        try {

            return ResponseEntity.ok(eventSubscriptionService.viewFavourites(
                            request,
                            UUID.fromString(jwt.getSubject())
                    )
            );

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@ModelAttribute SearchEventRequest request) {
        log.info("Started searching for events");
        try {

            return ResponseEntity.ok(eventService.searchEvents(request));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/view/nearby")
    public ResponseEntity<?> viewNearby(@ModelAttribute ViewEventNearbyRequest request) {
        try {

            return ResponseEntity.ok(eventService.viewEventNearby(request));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
