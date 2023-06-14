package pkibackend.pkibackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OcspTable {
    @Id
    private UUID id;
    @Column(nullable = false)
    private BigInteger caSerialNumber;
    @ElementCollection
    @CollectionTable(name = "ocsp_revoked_certs",
            joinColumns = @JoinColumn(name = "ocsptb_id"))
    //So same certificate can't be revoked twice
    @Column(name = "revoked_certificate_serial_num", nullable = false, unique = true)
    private Set<BigInteger> revokedCertificateSerialNums = new HashSet();
}
