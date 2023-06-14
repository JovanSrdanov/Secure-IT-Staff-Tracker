package pkibackend.pkibackend.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.Utilities.TokenUtils;
import pkibackend.pkibackend.dto.Auth.Jwt;
import pkibackend.pkibackend.dto.Auth.JwtAuthenticationRequest;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.service.interfaces.IAccountService;


@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IAccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {

        try {

            Account a = accountService.findAccountByEmail(authenticationRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword() + a.getSalt()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Account account = (Account) authentication.getPrincipal();
            String jwt = tokenUtils.generateToken(account.getUsername(), account.getRoles().get(0).getName());

            return ResponseEntity.ok(new Jwt(jwt));
        } catch (DisabledException e) {
            return new ResponseEntity<>("Account is not activated", HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}


