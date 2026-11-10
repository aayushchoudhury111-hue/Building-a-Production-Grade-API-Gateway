package in.chillitz.gatekeeper.management.Service;

import in.chillitz.gatekeeper.management.Dto.RequestDto.LoginRequest;
import in.chillitz.gatekeeper.management.Dto.ResponseDto.LoginResponse;
import in.chillitz.gatekeeper.management.Entity.ManagementUser;
import in.chillitz.gatekeeper.management.Entity.OrganizationStatus;
import in.chillitz.gatekeeper.management.Repository.ManagementUserRepo;
import lombok.Getter;
import lombok.Setter;
import in.chillitz.gatekeeper.management.Exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Getter
@Setter
public class AuthenticationService {

    private final ManagementUserRepo managementUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public AuthenticationService(ManagementUserRepo managementUserRepo, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        this.managementUserRepo = managementUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        ManagementUser user = managementUserRepo.findByUsername(loginRequest.getUsername()).orElseThrow(() -> invalidCredentials());

        if(!user.isEnabled() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        if(user.getOrganization() !=null && user.getOrganization().getStatus() != OrganizationStatus.ACTIVE){
            throw new ApiException(HttpStatus.FORBIDDEN, "The organizations is not active");
        }

        Instant issuedAt = Instant.now();
        String role = user.getRole().getName();
        Long organizationId = user.getOrganization() == null ? null : user.getOrganization().getId();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("gatekeeper-api-management")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .subject(user.getUsername())
                .claim("role", role);

        if(organizationId != null){
            claims.claim("organizationId", organizationId);
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new LoginResponse(token, "Bearer", expirationSeconds, role, organizationId);
    }

    private RuntimeException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or Password");
    }
}
