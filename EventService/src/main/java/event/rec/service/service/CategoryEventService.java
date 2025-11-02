package event.rec.service.service;

import event.rec.service.entities.CategoryEventEntity;
import event.rec.service.repository.CategoryEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryEventService {

    private final CategoryEventRepository categoryEventRepository;

    public CategoryEventEntity save(CategoryEventEntity categoryEventEntity) {
        return categoryEventRepository.save(categoryEventEntity);
    }
}
