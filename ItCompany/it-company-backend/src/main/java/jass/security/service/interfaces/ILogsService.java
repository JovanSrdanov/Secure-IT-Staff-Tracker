package jass.security.service.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
public interface ILogsService {
    public List<String> getAllLogs() throws IOException;
}
