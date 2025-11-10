package ma.emg.thera.Controllers;

import lombok.extern.slf4j.Slf4j;
import ma.emg.thera.Configuration.JwtUtils;
import ma.emg.thera.Entity.Utilisateur;
import ma.emg.thera.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // ou restreignez à votre frontend
@Slf4j
public class ApiAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // DTO pour login (sécurité + clarté)
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByUsername(request.getUsername());
                if (utilisateurOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non trouvé");
                }

                Utilisateur user = utilisateurOpt.get();
                String token = jwtUtils.generateToken(request.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("role", user.getRole() != null ? user.getRole().getNomRole() : "Patient");

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides");
        } catch (AuthenticationException e) {
            log.warn("Échec de l'authentification pour : {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides");
        }
    }
}