package jass.security.service.interfaces;

import jass.security.dto.CvAesDto;
import jass.security.exception.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ICvService {
    void save(MultipartFile file, UUID swEngineerId) throws IOException, NotFoundException;
    byte[] read(UUID engineerId) throws IOException, NotFoundException;
    CvAesDto encrypt(byte[] cv);
    byte[] decrypt(CvAesDto dto);
}
