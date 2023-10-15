package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.enums.UserRole;
import jdk.jfr.Registered;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Registered
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findBySiteSiteId(Long siteId, Pageable pageable);

    Optional<User> findFirstByRole(UserRole admin);

    List<User> findByRoleAndSiteSiteIdAndIsActive(UserRole supplier, Long siteId, boolean b);
}
