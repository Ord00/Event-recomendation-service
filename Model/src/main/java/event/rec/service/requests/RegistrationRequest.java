package event.rec.service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RegistrationRequest {
    @NotNull
    @JsonProperty("login")
    private String login;

    @NotNull
    @JsonProperty("password")
    private String password;

    @NotNull
    @JsonProperty("user_type")
    private String userType;
}
