package in.chillitz.gatekeeper.management.Dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HealthResponse {
    private String serviceStatus;
    private String dbStaus;
    private LocalDateTime timeStamp;

}
