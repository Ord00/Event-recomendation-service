package event.rec.service.service;

import event.rec.service.entities.CategoryEntity;
import event.rec.service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryEntity findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}
