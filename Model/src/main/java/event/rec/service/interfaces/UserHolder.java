package event.rec.service.interfaces;

import event.rec.service.entities.UserEntity;

import java.util.UUID;

public interface UserHolder {

    UserEntity getUserEntity();
    void setUserEntity(UserEntity userEntity);

    default UUID getId() {
        return getUserEntity() != null ? getUserEntity().getId() : null;
    }
}
