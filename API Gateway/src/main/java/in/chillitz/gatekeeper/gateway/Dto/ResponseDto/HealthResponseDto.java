package in.chillitz.gatekeeper.gateway.Dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HealthResponseDto {

    private String serviceName;
    private String serviceDesc;
    private String appStatus;
}
