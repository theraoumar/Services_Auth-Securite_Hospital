package ma.emg.thera.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "nom_role", length = 50)
    private String nomRole;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Relation inverse avec les utilisateurs
    @OneToMany(mappedBy = "role")
    private List<Utilisateur> utilisateurs;
}
