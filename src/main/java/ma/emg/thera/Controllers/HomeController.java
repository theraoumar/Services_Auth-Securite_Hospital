package ma.emg.thera.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        model.addAttribute("username", username);

        // Déterminer le rôle principal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .orElse("Patient");

        model.addAttribute("role", role);

        // Rediriger ou afficher une vue spécifique
        switch (role) {
            case "Admin":
                return "redirect:/admin";
            case "Medecin":
                return "home/medecin";
            case "Infirmier":
                return "home/infirmier";
            default:
                return "home/patient";
        }
    }
}