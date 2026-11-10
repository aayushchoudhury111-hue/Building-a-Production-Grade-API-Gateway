package in.chillitz.gatekeeper.analysis.Controller;

import in.chillitz.gatekeeper.analysis.Dto.ResponseDto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    private String serviceName;
    private String serviceDesc;

    public HealthController(@Value("${info.app.name}") String serviceName, @Value("${info.app.description}") String serviceDesc ){
        this.serviceName = serviceName;
        this.serviceDesc = serviceDesc;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(serviceName, serviceDesc, "UP"));
    }
}
