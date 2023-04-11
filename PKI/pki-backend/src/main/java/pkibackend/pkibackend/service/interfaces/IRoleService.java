package pkibackend.pkibackend.service.interfaces;


import pkibackend.pkibackend.model.Role;

import java.util.List;

public interface IRoleService {
    Role findById(Long id);

    List<Role> findByName(String name);


}
