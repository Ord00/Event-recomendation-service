package event.rec.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    @PostMapping("/create")
    public ResponseEntity<?> createEvent() {
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
    public ResponseEntity<?> addToFavourite() {
        return null;
    }

    @GetMapping("/view/favourite")
    public ResponseEntity<?> viewFavourite() {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search() {
        return null;
    }

    @GetMapping("/view/nearby")
    public ResponseEntity<?> viewNearby() {
        return null;
    }

}
