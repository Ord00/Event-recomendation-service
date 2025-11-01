package event.rec.service.repository;

import event.rec.service.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
            SELECT e FROM EventEntity e
                JOIN FETCH e.categoryEvents ce
                JOIN FETCH ce.idCategory c
                JOIN FETCH e.idVenue v
            WHERE e.status = 'PUBLISHED'
                AND (CURRENT_TIMESTAMP > e.startTime OR (e.startTime - CURRENT_TIMESTAMP) <= :interval)
                AND FUNCTION('ST_Distance', v.location, :location) <= :radius
                AND c.id IN :categoryIds
            """)
    List<EventEntity> findEventNearby(List<Long> categoryIds,
                                      Duration interval,
                                      Point location,
                                      Long radius);

    @Query(value = """
            SELECT e.id FROM event e
                JOIN category_event ce ON e.id = ce.id_event
                JOIN category c ON ce.id_category = c.id
                JOIN venue v ON e.id_venue = v.id
            WHERE e.status = 'PUBLISHED'
                AND e.title ~ :regexPattern
                AND :from <= e.start_time AND e.end_time <= :to
                AND c.id IN :categoryIds
            ORDER BY e.start_time
            LIMIT :pageSize
            OFFSET :offset
            """, nativeQuery = true)
    List<Long> searchEventIds(String regexPattern,
                              List<Long> categoryIds,
                              OffsetDateTime from,
                              OffsetDateTime to,
                              int pageSize,
                              int offset);

    @Query("""
            SELECT e FROM EventEntity e
                JOIN FETCH e.categoryEvents ce
                JOIN FETCH ce.idCategory c
                JOIN FETCH e.idVenue v
            WHERE e.id IN :eventIds
            ORDER BY e.startTime
            """)
    List<EventEntity> searchEvent(List<Long> eventIds);
}
