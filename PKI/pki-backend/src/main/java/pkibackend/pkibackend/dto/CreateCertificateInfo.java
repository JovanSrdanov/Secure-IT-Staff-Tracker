package pkibackend.pkibackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class CreateCertificateInfo {
    private EntityInfo subjectInfo;
    private EntityInfo issuerInfo;
    private Date startDate;
    private Date endDate;
    String alias;
}
