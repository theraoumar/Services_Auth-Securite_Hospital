package ma.emg.thera.Controllers;

import ma.emg.thera.Entity.Utilisateur;
import ma.emg.thera.Entity.Role;
import ma.emg.thera.Repository.UtilisateurRepository;
import ma.emg.thera.Repository.RoleRepository;
import ma.emg.thera.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class WebAuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============== LOGIN ==============
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Nom d'utilisateur ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Vous avez été déconnecté avec succès.");
        }
        return "login";
    }

    // ============== REGISTER ==============
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // ✅ Sécurité : on n'autorise que le rôle "Patient" à l'inscription publique
        Role patientRole = roleRepository.findByNomRole("Patient")
                .orElseThrow(() -> new RuntimeException("Rôle 'Patient' manquant en base de données !"));

        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("roleId", patientRole.getIdRole()); // valeur par défaut
        return "register";
    }

    @PostMapping("/perform_register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validation simple
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            model.addAttribute("utilisateur", new Utilisateur());
            return "register";
        }

        if (utilisateurRepository.existsByUsername(username)) {
            model.addAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            model.addAttribute("utilisateur", new Utilisateur());
            return "register";
        }

        if (utilisateurRepository.existsByEmail(email)) {
            model.addAttribute("error", "Cet email est déjà utilisé.");
            model.addAttribute("utilisateur", new Utilisateur());
            return "register";
        }

        try {
            Role patientRole = roleRepository.findByNomRole("Patient")
                    .orElseThrow(() -> new RuntimeException("Rôle 'Patient' introuvable"));

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUsername(username);
            utilisateur.setEmail(email);
            utilisateur.setPassword(passwordEncoder.encode(password));
            utilisateur.setRole(patientRole);

            utilisateurRepository.save(utilisateur);
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès ! Connectez-vous.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur interne. Veuillez réessayer.");
            model.addAttribute("utilisateur", new Utilisateur());
            return "register";
        }
    }
}