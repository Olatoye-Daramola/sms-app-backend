package africa.talentup.smsappbackend.data.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@Getter
public class SmsDto {

    @Size(min=6, max=16, message="Sender's phone number has to be between 6-16 numbers")
    private String smsSender;

    @Size(min=6, max=16, message="Receiver's phone number has to be between 6-16 numbers")
    private String smsReceiver;

    @Size(min=1, max=120, message="Message body has to be between 1-120 characters")
    private String smsBody;
}
