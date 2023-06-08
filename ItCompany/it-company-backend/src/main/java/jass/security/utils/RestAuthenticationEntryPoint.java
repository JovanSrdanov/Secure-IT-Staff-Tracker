package jass.security.utils;

import ClickSend.ApiClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jass.security.controller.PrivilegeController;
import jass.security.dto.SMSDto;
import jass.security.service.interfaces.IAdministratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);
    @Autowired
    private ApiClient clickSendConfig;

    @Autowired
    private IAdministratorService administratorService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //Metoda koja se izvrsava ukoliko za prosledjene kredencijale korisnik pokusa da pristupi zasticenom REST servisu
    //Metoda vraca 401 Unauthorized response, ukoliko postoji Login Page u aplikaciji, pozeljno je da se korisnik redirektuje na tu stranicu
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.warn("Unauthorized access attempt from IP: " + request.getRemoteAddr());

        //Clicksend

        this.simpMessagingTemplate.convertAndSend("/socket-publisher", "Unauthorized access attempt from IP: " + request.getRemoteAddr());
        var admins = administratorService.findAll();
        if (admins != null) {
            for (var admin : admins) {
                SMSDto smsDto = new SMSDto("IT Company",
                        "Unauthorized access attempt from IP: " + request.getRemoteAddr(),
                        admin.getPhoneNumber());
                SMSUtils.sendSMS(logger, clickSendConfig, smsDto);
            }
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
