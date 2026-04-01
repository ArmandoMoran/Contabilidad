package com.contabilidad.identity;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCompanyIdAndEmail(UUID companyId, String email);
    Optional<User> findByEmail(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findWithLockByEmail(String email);
    boolean existsByCompanyIdAndEmail(UUID companyId, String email);
}
