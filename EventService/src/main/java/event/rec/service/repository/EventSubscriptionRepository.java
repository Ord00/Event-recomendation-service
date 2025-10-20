package event.rec.service.repository;

import event.rec.service.entities.EventSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscriptionEntity, Long> {
}
