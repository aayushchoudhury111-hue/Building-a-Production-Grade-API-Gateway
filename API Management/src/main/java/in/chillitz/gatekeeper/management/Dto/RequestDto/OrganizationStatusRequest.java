package in.chillitz.gatekeeper.management.Dto.RequestDto;

import in.chillitz.gatekeeper.management.Entity.OrganizationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationStatusRequest {

    @NotNull
    private OrganizationStatus status;
}
