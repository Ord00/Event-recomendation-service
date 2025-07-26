package event.rec.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.locationtech.jts.geom.Point;


import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "venue")
@Data
public class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "address", nullable = false, length = Integer.MAX_VALUE)
    private String address;

    @OneToMany(mappedBy = "idVenue")
    private Set<EventEntity> events = new LinkedHashSet<>();

    @Column(name = "location", columnDefinition = "geography(Point,4326) not null")
    private Point location;
}
