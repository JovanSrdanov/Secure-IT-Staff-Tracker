package pkibackend.pkibackend.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pkibackend.pkibackend.Utilities.AESUtilities;
import pkibackend.pkibackend.dto.AESPasswordDto;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class KeystoreRowInfo {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String keystoreName;


    @Column(nullable = false)
    // Omitting get and set for this field
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private byte[] password;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    //Initialization vector for AES block cipher
    private byte[] aesInitVector;
    @Column(nullable = false)
    private BigInteger certificateSerialNumber;
    @Column(nullable = false)
    private BigInteger issuingCertificateSerialNumber;
    @Column(nullable = false)
    private String alias;
    @Column(nullable = false)
    private String rowPassword;


    public KeystoreRowInfo(UUID id, String keystoreName, String clearTextPassword, BigInteger certificateSerialNumber, BigInteger issuingCertificateSerialNumber, String alias, String rowPassword) {
        this.id = id;
        this.keystoreName = keystoreName;
        this.certificateSerialNumber = certificateSerialNumber;
        this.issuingCertificateSerialNumber = issuingCertificateSerialNumber;
        this.alias = alias;
        this.rowPassword = rowPassword;

        AESUtilities aes = new AESUtilities();
        AESPasswordDto aesPasswordDto = aes.encrypt(clearTextPassword);

        this.password = aesPasswordDto.getPassword();
        this.aesInitVector = aesPasswordDto.getIv();
    }

    public String getPassword() {
        AESUtilities aes = new AESUtilities();
        return aes.decrypt(new AESPasswordDto(password, this.aesInitVector));
    }
}
