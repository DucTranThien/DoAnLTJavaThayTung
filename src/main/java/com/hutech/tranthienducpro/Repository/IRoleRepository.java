package com.hutech.tranthienducpro.Repository;

import com.hutech.tranthienducpro.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String name);
}
