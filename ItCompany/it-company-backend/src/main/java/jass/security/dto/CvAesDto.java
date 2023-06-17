package jass.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CvAesDto {
    private byte[] encryptedCv;
    private byte[] secretKey;
    private byte[] aesInitVector;
}
