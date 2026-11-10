package in.chillitz.gatekeeper.management.Dto.ResponseDto;

import in.chillitz.gatekeeper.management.Entity.ManagementUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TenantAdminResponse {

    private Long id;
    private Long organizationId;
    private String username;
    private boolean enabled;

    public static TenantAdminResponse from(ManagementUser user) {
        return new TenantAdminResponse(
                user.getId(),
                user.getOrganization() == null ? null : user.getOrganization().getId(),
                user.getUsername(),
                user.isEnabled());
    }
}
