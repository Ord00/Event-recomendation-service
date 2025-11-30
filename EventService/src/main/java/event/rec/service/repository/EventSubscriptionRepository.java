package event.rec.service.repository;

import event.rec.service.entities.EventSubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscriptionEntity, Long> {

    @Query("""
            SELECT es FROM EventSubscriptionEntity es
                JOIN FETCH es.idEvent e
                JOIN FETCH e.idOrganizer
                JOIN FETCH e.idVenue
                LEFT JOIN FETCH e.categoryEvents ce
                LEFT JOIN FETCH ce.idCategory
            WHERE es.idUser.id = :userId
            """)
    List<EventSubscriptionEntity> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT es FROM EventSubscriptionEntity es
            WHERE es.idUser.id = :userId AND es.idEvent.id = :eventId
            """)
    Optional<EventSubscriptionEntity> findByUserIdAndEventId(@Param("userId") UUID userId,
                                                             @Param("eventId") Long eventId);
}
