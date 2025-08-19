package event.rec.service.controller;

import event.rec.service.dto.EventDto;
import event.rec.service.dto.EventSubscriptionDto;
import event.rec.service.requests.SearchEventRequest;
import event.rec.service.requests.ViewEventNearbyRequest;
import event.rec.service.requests.ViewFavouriteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDto event) {
        return null;
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        return null;
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId) {
        return null;
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
