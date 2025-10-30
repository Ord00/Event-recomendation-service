package event.rec.service.repository;

import event.rec.service.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);

    @Query("SELECT u.id FROM UserEntity u " +
            "WHERE u.login = :login " +
            "AND (:role = 'ADMIN' AND u.admin IS NOT NULL OR " +
            ":role = 'ORGANIZER' AND u.organizer IS NOT NULL OR " +
            ":role = 'USER' AND u.commonUser IS NOT NULL)")
    Optional<Long> findIdByLoginAndRole(String login,
                                        String role);
}
