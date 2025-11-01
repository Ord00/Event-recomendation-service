package event.rec.service.repository;

import event.rec.service.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);

    @Query(value = """
        SELECT u.id FROM "user" u
        WHERE u.login = :login
            AND (
                (:role = 'ADMIN' AND EXISTS (SELECT 1 FROM admin a WHERE a.id = u.id)) OR
                (:role = 'ORGANIZER' AND EXISTS (SELECT 1 FROM organizer o WHERE o.id = u.id)) OR
                (:role = 'USER' AND EXISTS (SELECT 1 FROM common_user cu WHERE cu.id = u.id))
            )
        """, nativeQuery = true)
    Optional<Long> findIdByLoginAndRole(String login,
                                        String role);
}
