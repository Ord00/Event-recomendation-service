package event.rec.service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", nullable = false, length = 50)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "userEntity")
    private AdminEntity admin;

    @OneToOne(mappedBy = "userEntity")
    private CommonUserEntity commonUser;

    @OneToMany(mappedBy = "idUser")
    private Set<EventEntity> events = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUser")
    private Set<EventSubscriptionEntity> eventSubscriptions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUser")
    private Set<NotificationLogEntity> notificationLogs = new LinkedHashSet<>();

    @OneToOne(mappedBy = "user")
    private OrganizerEntity organizer;

}
