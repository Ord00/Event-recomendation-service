package event.rec.service.repository;

import event.rec.service.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.time.Duration;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e " +
            "JOIN FETCH e.categoryEvents ce " +
            "JOIN FETCH ce.idCategory c " +
            "JOIN FETCH e.idVenue v " +
            "WHERE e.status = 'PUBLISHED' " +
            "AND (CURRENT_TIMESTAMP > e.startTime OR (e.startTime - CURRENT_TIMESTAMP) <= :interval) " +
            "AND FUNCTION('ST_Distance', v.location, :location) <= :radius " +
            "AND c.id IN :categoryIds")
    List<EventEntity> findEventNearby(List<Long> categoryIds,
                                             Duration interval,
                                             Point location,
                                             Long radius);
}
