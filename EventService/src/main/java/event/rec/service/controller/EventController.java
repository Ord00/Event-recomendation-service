package event.rec.service.controller;

import event.rec.service.dto.EventDto;
import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.requests.ViewFavouriteRequest;
import event.rec.service.service.EventService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDto event) {
        try {
            return ResponseEntity.ok(eventService.createEvent(event));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
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
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody EventDto event) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(eventId, event));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add/to/favourite")
    public ResponseEntity<?> addToFavourite(@RequestBody EventSubscriptionDto eventSubscription) {
        return null;
    }

    @GetMapping("/view/favourite")
    public ResponseEntity<?> viewFavourite(@RequestBody ViewFavouriteRequest request) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchEventRequest request) {
        return null;
    }

    @GetMapping("/view/nearby")
    public ResponseEntity<?> viewNearby(@RequestBody ViewEventNearbyRequest request) {
        return null;
    }

}
