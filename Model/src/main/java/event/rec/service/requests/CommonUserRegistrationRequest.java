package event.rec.service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonUserRegistrationRequest extends RegistrationRequest {
    @NotNull
    @JsonProperty("full_name")
    private String fullName;

    @NotNull
    @JsonProperty("phone_number")
    private String phoneNumber;
}
