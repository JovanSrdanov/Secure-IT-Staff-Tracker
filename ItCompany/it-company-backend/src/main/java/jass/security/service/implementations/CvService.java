package jass.security.service.implementations;

import jass.security.service.interfaces.ICvService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Primary
public class CvService implements ICvService {

    private String getCvDir(){
        String runtimeDir = getClass().getResource("").getPath();
        String rootDir = runtimeDir.substring(0, runtimeDir.indexOf("target/classes"));
        return rootDir + "/cv/";
    }

    @Override
    public void save(MultipartFile file, UUID engineerId) throws IOException {
        String fileName = "cv_" + engineerId.toString();
        file.transferTo(new File(getCvDir() + fileName));
    }

    @Override
    public byte[] read(UUID engineerId) throws IOException {
        String fileName = "cv_" + engineerId.toString();
        return Files.readAllBytes(Path.of(getCvDir() + fileName));
    }
}
