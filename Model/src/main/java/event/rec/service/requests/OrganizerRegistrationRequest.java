package event.rec.service.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizerRegistrationRequest extends RegistrationRequest {
        @NotNull
        @JsonProperty("organizer_name")
        private String organizerName;
}