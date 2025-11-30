package event.rec.service.enums;

import event.rec.service.interfaces.UserHolder;
import event.rec.service.repository.AdminRepository;
import event.rec.service.repository.CommonUserRepository;
import event.rec.service.repository.OrganizerRepository;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Getter
public enum UserRole {
    ADMIN(AdminRepository.class, "ADMIN"),
    USER(CommonUserRepository.class, "USER"),
    ORGANIZER(OrganizerRepository.class, "ORGANIZER");

    private final Class<? extends JpaRepository<? extends UserHolder, UUID>> repositoryClass;
    private final String roleName;

    UserRole(Class<? extends JpaRepository<? extends UserHolder, UUID>> repositoryClass, String roleName) {
        this.repositoryClass = repositoryClass;
        this.roleName = roleName;
    }
}
