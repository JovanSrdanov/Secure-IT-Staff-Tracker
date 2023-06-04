package jass.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jass.security.controller.PrivilegeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String username = request.getUserPrincipal().getName();
        String requestURI = request.getRequestURI();

        logger.warn("Unauthorized access attempt by user: " + username + " to URL: " + requestURI);

        // Return an appropriate response to the client
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
    }
}
