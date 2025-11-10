package ma.emg.thera.Controllers;

import lombok.RequiredArgsConstructor;
import ma.emg.thera.Entity.Role;
import ma.emg.thera.Entity.Utilisateur;
import ma.emg.thera.Repository.RoleRepository;
import ma.emg.thera.Repository.UtilisateurRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔐 Accès réservé à l'Admin
    @PreAuthorize("hasRole('Admin')")
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("utilisateurForm", new Utilisateur());
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/users")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Long roleId,
            Model model) {

        if (utilisateurRepository.existsByUsername(username) || utilisateurRepository.existsByEmail(email)) {
            model.addAttribute("error", "Nom d'utilisateur ou email déjà utilisé.");
            model.addAttribute("utilisateurs", utilisateurRepository.findAll());
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("utilisateurForm", new Utilisateur());
            return "admin/dashboard";
        }

        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

            Utilisateur user = new Utilisateur();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            utilisateurRepository.save(user);
            model.addAttribute("success", "Utilisateur créé avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création : " + e.getMessage());
        }

        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("utilisateurForm", new Utilisateur());
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, Model model) {
        utilisateurRepository.deleteById(id);
        return "redirect:/admin?deleted";
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/users/{id}/update")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam Long roleId,
            Model model) {

        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        user.setRole(role);
        utilisateurRepository.save(user);

        return "redirect:/admin?updated";
    }
}
