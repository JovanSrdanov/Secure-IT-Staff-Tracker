package pkibackend.pkibackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CertificateEntityInfoDto {
    private String country;
    private String orgUnit;
    private String organization;
    private String commonName;
    private String surname;
    private String givenName;
    private List<String> extensions;

    public CertificateEntityInfoDto(X500Name info, List<String> extensions) {
        this.country = info.getRDNs(BCStyle.C).length > 0 ? info.getRDNs(BCStyle.C)[0].getFirst().getValue().toString() : null;
        this.orgUnit = info.getRDNs(BCStyle.OU).length > 0 ? info.getRDNs(BCStyle.OU)[0].getFirst().getValue().toString() : null;
        this.organization = info.getRDNs(BCStyle.O).length > 0 ? info.getRDNs(BCStyle.O)[0].getFirst().getValue().toString() : null;
        this.commonName = info.getRDNs(BCStyle.CN).length > 0 ? info.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString() : null;
        this.surname = info.getRDNs(BCStyle.SURNAME).length > 0 ? info.getRDNs(BCStyle.SURNAME)[0].getFirst().getValue().toString() : null;
        this.givenName = info.getRDNs(BCStyle.GIVENNAME).length > 0 ? info.getRDNs(BCStyle.GIVENNAME)[0].getFirst().getValue().toString() : null;
        this.extensions = extensions;
    }
}


