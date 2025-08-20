package event.rec.service.service;

import event.rec.service.entities.VenueEntity;
import event.rec.service.repository.VenueRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VenueService {

    private final VenueRepository repository;

    public VenueEntity findById(Long id) {

        return repository.findById(id).orElse(null);

    }
}
