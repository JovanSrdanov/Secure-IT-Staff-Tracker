package jass.security.controller;

import jass.security.service.interfaces.ILogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/logs")
public class LogsController {
    private final ILogsService _logsService;

    @Autowired
    public LogsController(ILogsService logsService) {
        _logsService = logsService;
    }

    @GetMapping("/get-all-logs")
    @PreAuthorize("hasAuthority('getAllLogs')")
    public ResponseEntity<?> getAllLogs() {
        try {
            List<String> logs = _logsService.getAllLogs();
            Collections.reverse(logs); // Reversing the list
            return new ResponseEntity<>( logs, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error while reading logs from files", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
