package africa.talentup.smsappbackend.data.dto.request;


import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotNull(message="username field cannot be null")
    private String username;

    @NotNull(message="authId field cannot be null")
    private String authId;
}
