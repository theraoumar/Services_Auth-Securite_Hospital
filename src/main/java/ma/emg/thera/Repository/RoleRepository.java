package ma.emg.thera.Repository;

import ma.emg.thera.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Trouver un rôle par son nom (ex: "ADMIN", "USER")
    Optional<Role> findByNomRole(String nomRole);
}
