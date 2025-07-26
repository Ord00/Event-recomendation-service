package event.rec.service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegistrationRequest extends RegistrationRequest {
    @NotNull
    @JsonProperty("full_name")
    private String fullName;
}
