Insert into management_users(
                             organization_id, role_id, username, password_hash, enabled, created_at, updated_at
)
SELECT NULL, roles.id, 'platform_admin',
    '$2a$12$j6Gg1K/A3A96FBz0sorCEeLN4zest3DvwmuJxaeG6uKwf.FftVlRG', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM roles where roles.name = 'PLATFORM_ADMIN';

Insert into management_users(
    organization_id, role_id, username, password_hash, enabled, created_at, updated_at
)
SELECT NULL, roles.id, 'gateway_service',
       '$2a$12$j6Gg1K/A3A96FBz0sorCEeLN4zest3DvwmuJxaeG6uKwf.FftVlRG', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM roles where roles.name = 'GATEWAY_SERVICE';
