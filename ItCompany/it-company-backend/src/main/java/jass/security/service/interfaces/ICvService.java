package jass.security.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ICvService {
    void save(MultipartFile file, UUID swEngineerId) throws IOException;
    byte[] read(UUID engineerId) throws IOException;
}
