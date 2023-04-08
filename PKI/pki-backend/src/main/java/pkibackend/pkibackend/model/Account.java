package pkibackend.pkibackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

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

    @Column(nullable = false)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    private Set<KeystoreRowInfo> keyStoreRowsInfo = new HashSet<>();

    public String getRowPasswordByAlias(String alias) {
        for (KeystoreRowInfo rowInfo : keyStoreRowsInfo) {
            if (rowInfo.getAlias().equals(alias)) {
                return rowInfo.getPassword();
            }
        }
        return "";
    }
}
