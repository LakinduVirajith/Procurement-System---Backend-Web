package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.User;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Registered
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
