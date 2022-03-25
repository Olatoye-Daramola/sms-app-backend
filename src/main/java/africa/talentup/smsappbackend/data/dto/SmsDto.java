package africa.talentup.smsappbackend.data.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SmsDto {

//    @Size(min=6, max=16, message="From is invalid")
    private String smsSender;

//    @Size(min=6, max=16, message="To is invalid")
    private String smsReceiver;

//    @Size(min=1, max=120, message="Message body is invalid")
    private String smsBody;
}
