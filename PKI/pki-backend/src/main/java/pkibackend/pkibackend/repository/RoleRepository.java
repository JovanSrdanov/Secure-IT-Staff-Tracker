package pkibackend.pkibackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pkibackend.pkibackend.model.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByName(String name);
}
