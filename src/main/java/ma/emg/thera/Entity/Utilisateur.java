package ma.emg.thera.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "utilisateurs")
@Data
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;


    // Relation avec la table roles (clé étrangère id_role)
    @ManyToOne
    @JoinColumn(name = "id_role", referencedColumnName = "id_role")
    private Role role;

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
