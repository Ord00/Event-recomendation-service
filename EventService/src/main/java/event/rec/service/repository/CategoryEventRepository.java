package event.rec.service.repository;

import event.rec.service.entities.CategoryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryEventRepository extends JpaRepository<CategoryEventEntity, Long> {
}
