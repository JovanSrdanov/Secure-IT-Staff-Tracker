package jass.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Privilege {
    @jakarta.persistence.Id
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    //@ManyToMany(mappedBy = "privileges", fetch = FetchType.EAGER)
    //private Collection<Role> roles;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
