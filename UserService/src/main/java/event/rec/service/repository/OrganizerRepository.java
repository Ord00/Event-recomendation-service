package event.rec.service.repository;

import event.rec.service.entities.OrganizerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizerRepository extends JpaRepository<OrganizerEntity, UUID> {
}
