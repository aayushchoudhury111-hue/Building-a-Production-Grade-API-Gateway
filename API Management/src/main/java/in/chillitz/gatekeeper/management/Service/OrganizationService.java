package in.chillitz.gatekeeper.management.Service;

import in.chillitz.gatekeeper.management.Dto.RequestDto.OrganizationRequest;
import in.chillitz.gatekeeper.management.Dto.RequestDto.OrganizationStatusRequest;
import in.chillitz.gatekeeper.management.Dto.RequestDto.TenantAdminRequest;
import in.chillitz.gatekeeper.management.Dto.ResponseDto.OrganizationResponse;
import in.chillitz.gatekeeper.management.Dto.ResponseDto.TenantAdminResponse;
import in.chillitz.gatekeeper.management.Entity.Organization;
import in.chillitz.gatekeeper.management.Entity.OrganizationStatus;
import in.chillitz.gatekeeper.management.Entity.ManagementUser;
import in.chillitz.gatekeeper.management.Entity.Role;
import in.chillitz.gatekeeper.management.Exception.ApiException;
import in.chillitz.gatekeeper.management.Repository.OrganizationRepo;
import in.chillitz.gatekeeper.management.Repository.ManagementUserRepo;
import in.chillitz.gatekeeper.management.Repository.RoleRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final ManagementUserRepo managementUserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public OrganizationService(OrganizationRepo organizationRepo, ManagementUserRepo managementUserRepo,
                               RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.organizationRepo = organizationRepo;
        this.managementUserRepo = managementUserRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public OrganizationResponse create(@Valid OrganizationRequest request) {
        long startedAt = System.nanoTime();

        checkUnique(request.getName(), request.getSlug(), null);

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());

        return OrganizationResponse.from(organizationRepo.save(organization));
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {

        long startedAt = System.nanoTime();

        return organizationRepo.findAll().stream()
                .filter(organization -> organization.getDeletedAt() == null)
                .map(OrganizationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(Long id) {
        return OrganizationResponse.from(getActiveOrganization(id));
    }

    @Transactional
    public OrganizationResponse update(Long id, @Valid OrganizationRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = getActiveOrganization(id);
        checkUnique(request.getName(), request.getSlug(), id);

        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setUpdatedAt(LocalDateTime.now());
        return OrganizationResponse.from(organizationRepo.save(organization));
    }

    @Transactional
    public OrganizationResponse changeStatus(Long id, @Valid OrganizationStatusRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = getActiveOrganization(id);
        if(request.getStatus() == OrganizationStatus.DELETED){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Use DELETE to soft-delete an organization");
        }

        organization.setStatus(request.getStatus());
        organization.setUpdatedAt(LocalDateTime.now());
        return OrganizationResponse.from(organizationRepo.save(organization));
    }

    @Transactional
    public void delete(Long id) {
        long startedAt = System.nanoTime();

        Organization organization = getActiveOrganization(id);
        LocalDateTime now = LocalDateTime.now();
        organization.setDeletedAt(now);
        organization.setUpdatedAt(now);
        organizationRepo.save(organization);
    }

    private Organization getActiveOrganization(Long id) {
        return organizationRepo.findById(id)
                .filter(organization -> organization.getDeletedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "organization not found"));
    }

    @Transactional
    public TenantAdminResponse createTenantadmin(Long organizationId, TenantAdminRequest request) {
        Organization organization = getActiveOrganization(organizationId);
        if (!managementUserRepo.findByOrganization_IdAndRole_Name(organizationId, "TENANT_ADMIN").isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "organization already has a tenant admin");
        }
        if (managementUserRepo.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "username already exists");
        }
        Role role = roleRepo.findByName("TENANT_ADMIN")
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "TENANT_ADMIN role is not configured"));
        LocalDateTime now = LocalDateTime.now();
        ManagementUser user = new ManagementUser();
        user.setOrganization(organization);
        user.setRole(role);
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return TenantAdminResponse.from(managementUserRepo.save(user));
    }

    @Transactional(readOnly = true)
    public TenantAdminResponse getTenantAdmin(Long organizationId) {
        getActiveOrganization(organizationId);
        return managementUserRepo.findByOrganization_IdAndRole_Name(organizationId, "TENANT_ADMIN").stream()
                .findFirst().map(TenantAdminResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "tenant admin not found"));
    }

    @Transactional
    public TenantAdminResponse updateTenantAdmin(Long organizationId, TenantAdminRequest request) {
        getActiveOrganization(organizationId);
        ManagementUser user = managementUserRepo.findByOrganization_IdAndRole_Name(organizationId, "TENANT_ADMIN").stream()
                .findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "tenant admin not found"));
        if (!user.getUsername().equals(request.getUsername()) && managementUserRepo.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "username already exists");
        }
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());
        return TenantAdminResponse.from(managementUserRepo.save(user));
    }

    @Transactional
    public void deleteTenantAdmin(Long organizationId) {
        getActiveOrganization(organizationId);
        ManagementUser user = managementUserRepo.findByOrganization_IdAndRole_Name(organizationId, "TENANT_ADMIN").stream()
                .findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "tenant admin not found"));
        managementUserRepo.delete(user);
    }

    private void checkUnique(String name, String slug, Long currentId) {
        boolean nameExists = currentId == null
                ? organizationRepo.existsByName(name)
                : organizationRepo.existsByNameAndIdNot(name, currentId);
        if (nameExists) {
            throw new ApiException(HttpStatus.CONFLICT, "organization name already exists");
        }

        boolean slugExists = currentId == null
                ? organizationRepo.existsBySlug(slug)
                : organizationRepo.existsBySlugAndIdNot(slug, currentId);
        if (slugExists) {
            throw new ApiException(HttpStatus.CONFLICT, "organization slug already exists");
        }
    }

}
