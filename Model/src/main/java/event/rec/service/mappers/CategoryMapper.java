package event.rec.service.mappers;

import event.rec.service.dto.CategoryDto;
import event.rec.service.entities.CategoryEntity;

public class CategoryMapper {

    public static CategoryDto categoryEntityToDto(CategoryEntity category) {

        return new CategoryDto(category.getCategoryName());
    }
}
