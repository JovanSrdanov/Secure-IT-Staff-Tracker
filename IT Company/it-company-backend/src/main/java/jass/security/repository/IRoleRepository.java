package jass.security.repository;

import jass.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(String name);
    @Modifying
    @Query(value = "DELETE FROM users_roles WHERE CAST(user_id AS VARCHAR) = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") String userId);
}
