UPDATE users
SET
    password_hash = '$2a$10$l6Jf/GRwL1CRRDneMlrkVe29wcs6j2CiN5wO1SOCRlBN5EnSKvAcm',
    failed_attempts = 0,
    locked = false,
    active = true,
    updated_at = now()
WHERE email = 'admin@demo.com';
