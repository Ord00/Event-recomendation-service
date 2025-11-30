package event.rec.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "login", nullable = false, length = 50)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "userEntity")
    private AdminEntity admin;

    @OneToOne(mappedBy = "userEntity")
    private CommonUserEntity commonUser;

    @OneToOne(mappedBy = "userEntity")
    private OrganizerEntity organizer;

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
