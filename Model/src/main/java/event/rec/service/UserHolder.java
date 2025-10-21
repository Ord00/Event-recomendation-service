package event.rec.service;

import event.rec.service.entities.UserEntity;

public interface UserHolder {

    UserEntity getUserEntity();
    void setUserEntity(UserEntity userEntity);

    default Long getId() {
        return getUserEntity() != null ? getUserEntity().getId() : null;
    }

}
