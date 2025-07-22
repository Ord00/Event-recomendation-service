package event.rec.service.repository;

import event.rec.service.entities.CommonUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonUserRepository extends JpaRepository<CommonUserEntity, Long> {
}
