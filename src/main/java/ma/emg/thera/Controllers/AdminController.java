package ma.emg.thera.Controllers;

import lombok.RequiredArgsConstructor;
import ma.emg.thera.Entity.Role;
import ma.emg.thera.Entity.Utilisateur;
import ma.emg.thera.Repository.RoleRepository;
import ma.emg.thera.Repository.UtilisateurRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔐 Seul l'Admin peut accéder à ces endpoints
    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/users")
    public Utilisateur createUser(@RequestBody CreateUserRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (utilisateurRepository.existsByUsername(request.getUsername()) ||
                utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Nom d'utilisateur ou email déjà utilisé");
        }

        // Charger le rôle
        Role role = roleRepository.findByNomRole(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé : " + request.getRoleName()));

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(request.getUsername());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));
        utilisateur.setRole(role);

        return utilisateurRepository.save(utilisateur);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/users")
    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }

    // Classe interne DTO (sécurité + clarté)
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String roleName; // ex: "Medecin", "Infirmier", "Patient"

        // Getters & Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
    }
}