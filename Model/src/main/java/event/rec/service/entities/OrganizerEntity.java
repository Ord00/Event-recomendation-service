package event.rec.service.entities;

import event.rec.service.interfaces.UserHolder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizer")
public class OrganizerEntity implements UserHolder {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "organizer_name", nullable = false, length = 100)
    private String organizerName;

    public OrganizerEntity(String organizerName) {
        this.organizerName = organizerName;
    }

    public OrganizerEntity() {}
}
