package pkibackend.pkibackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeystoreRowInfo {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String alias;
    @Column(nullable = false)
    private String password;
}