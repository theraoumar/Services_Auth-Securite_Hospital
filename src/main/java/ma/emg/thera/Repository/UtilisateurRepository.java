package ma.emg.thera.Repository;

import ma.emg.thera.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Trouver un utilisateur par username (utile pour login)
    Optional<Utilisateur> findByUsername(String username);

    // Vérifier si un utilisateur existe déjà
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}