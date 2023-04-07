package pkibackend.pkibackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private UUID id;
    private String email;
    private String password;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X500Name x500Name;

    private List<UUID> certificateSerialNumbers;
}
