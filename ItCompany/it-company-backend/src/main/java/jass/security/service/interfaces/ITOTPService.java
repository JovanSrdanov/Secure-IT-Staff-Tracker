package jass.security.service.interfaces;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.io.IOException;

public interface ITOTPService {
      String generateSecretKey();
      String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer);
      byte[] createQRCode(String barCodeData) throws WriterException, IOException;
      public String getTOTPCode(String secretKey);
}
