package in.chillitz.gatekeeper.analysis.Dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HealthResponse {
    private String serviceName;
    private String serviceDesc;
    private String appStatus;
}
