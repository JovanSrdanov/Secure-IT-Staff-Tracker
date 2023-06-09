package jass.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Account {
    @Id
    private UUID id;
    @ColumnTransformer( read = "pgp_sym_decrypt( email, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(unique = true, nullable = false, columnDefinition = "bytea")
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private RegistrationRequestStatus status;

    private UUID employeeId;
    private Boolean isActivated;
    private Boolean isBlocked;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;


    @ColumnTransformer( read = "pgp_sym_decrypt(totp_secret_key, current_setting('encrypt.key') )",
            write = " pgp_sym_encrypt( ?, current_setting('encrypt.key') )")
    @Column(columnDefinition = "bytea")
   private String totpSecretKey;
}





















