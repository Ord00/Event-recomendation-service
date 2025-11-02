package event.rec.service.mappers;

import event.rec.service.entities.CategoryEntity;
import event.rec.service.entities.CategoryEventEntity;
import event.rec.service.entities.EventEntity;

public class CategoryEventMapper {

    public static CategoryEventEntity categoryEventDtoToEntity(CategoryEntity category,
                                                               EventEntity event) {
        CategoryEventEntity categoryEvent = new CategoryEventEntity();
        categoryEvent.setIdCategory(category);
        categoryEvent.setIdEvent(event);
        return categoryEvent;
    }
}
