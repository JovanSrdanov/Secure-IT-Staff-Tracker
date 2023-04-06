package pkibackend.pkibackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String email;
    private String password;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X500Name x500Name;
}
