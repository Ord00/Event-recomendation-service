package event.rec.service.repository;

import event.rec.service.entities.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {
}
