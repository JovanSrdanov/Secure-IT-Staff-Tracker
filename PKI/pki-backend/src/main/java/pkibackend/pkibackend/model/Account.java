package pkibackend.pkibackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import javax.persistence.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Transient
    private PrivateKey privateKey;

    @JsonIgnore
    @Transient
    private PublicKey publicKey;

    @Transient
    private X500Name x500Name;

    @ElementCollection  // TODO Stefan: promeni
    @Column(nullable = false)
    private List<String> certificateAliases;
}
