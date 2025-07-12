package event.rec.service.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record RegistrationRequest(
        @NotNull
        @JsonProperty("login") String login,

        @NotNull
        @JsonProperty("password") String password
) {
}