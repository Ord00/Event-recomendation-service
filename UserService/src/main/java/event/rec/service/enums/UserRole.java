package event.rec.service.enums;

import event.rec.service.repository.AdminRepository;
import event.rec.service.repository.OrganizerRepository;
import event.rec.service.repository.UserRepository;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Getter
public enum UserRole {
    ADMIN(AdminRepository.class, "ADMIN"),
    USER(UserRepository.class, "USER"),
    ORGANIZER(OrganizerRepository.class, "ORGANIZER");

    private final Class<? extends JpaRepository<?, UUID>> repositoryClass;
    private final String roleName;

    UserRole(Class<? extends JpaRepository<?, UUID>> repositoryClass, String roleName) {
        this.repositoryClass = repositoryClass;
        this.roleName = roleName;
    }
}
