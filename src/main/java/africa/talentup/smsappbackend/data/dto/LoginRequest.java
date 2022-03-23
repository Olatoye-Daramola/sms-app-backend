package africa.talentup.smsappbackend.data.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class LoginRequest {
    @NotNull(message="username field cannot be null")
    private String username;

    @NotNull(message="authId field cannot be null")
    private String authId;
}
