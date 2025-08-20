package event.rec.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_organizer", nullable = false)
    private OrganizerEntity idOrganizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venue")
    private VenueEntity idVenue;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Size(max = 20)
    @Column(name = "recurrence", length = 20)
    private String recurrence;

    @Size(max = 20)
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private Integer viewCount;

    @OneToMany(mappedBy = "idEvent")
    private Set<CategoryEventEntity> categoryEvents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idEvent")
    private Set<EventSubscriptionEntity> eventSubscriptions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idEvent")
    private Set<NotificationLogEntity> notificationLogs = new LinkedHashSet<>();
}