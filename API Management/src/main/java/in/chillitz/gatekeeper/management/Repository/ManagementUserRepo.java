package in.chillitz.gatekeeper.management.Repository;

import in.chillitz.gatekeeper.management.Entity.ManagementUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ManagementUserRepo extends JpaRepository<ManagementUser, Long> {
    Optional<ManagementUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<ManagementUser> findByOrganization_IdAndRole_Name(Long organizationId, String roleName);
}
