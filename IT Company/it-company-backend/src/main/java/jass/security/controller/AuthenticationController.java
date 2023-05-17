package jass.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import jass.security.dto.JwtAuthenticationRequest;
import jass.security.dto.RefreshRequest;
import jass.security.dto.UserTokenState;
import jass.security.model.Account;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IAccountService accountService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
        }


        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token za tog korisnika
        //TODO Strahinja: Zasto ovde baca error?
        //Account user = (Account) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(authenticationRequest.getEmail());
        String resfresh = tokenUtils.generateRefreshToken(authenticationRequest.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, resfresh, expiresIn));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest token) {
        if(tokenUtils.validateRefreshToken(token.getToken())) {
            String email = tokenUtils.getUsernameFromToken(token.getToken());
            String jwt = tokenUtils.generateToken(email);
            int expiresIn = tokenUtils.getExpiredIn();
            return ResponseEntity.ok(new UserTokenState(jwt, token.getToken(), expiresIn));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token not valid");

    }

    // Endpoint za registraciju novog korisnika
    //Endpoint za registrovanje
    /*
    @PostMapping("/signup")
    public ResponseEntity<Account> addUser(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) {
        User existUser = this.accountService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new ResourceConflictException(userRequest.getId(), "Username already exists");
        }

        User user = this.accountService.save(userRequest);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }*/
}
