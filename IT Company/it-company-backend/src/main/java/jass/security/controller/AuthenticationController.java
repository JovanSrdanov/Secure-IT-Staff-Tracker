package jass.security.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jass.security.dto.*;
import jass.security.exception.*;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.model.Role;
import jass.security.service.implementations.MailSenderService;
import jass.security.service.interfaces.IAccountActivationService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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

    @Autowired
    private IAccountActivationService accountActivationService;

    @Autowired
    private MailSenderService mailSenderService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
        Account acc = accountService.findByEmail(authenticationRequest.getEmail());
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found");
        }

        if (acc.getStatus() != RegistrationRequestStatus.APPROVED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Registration is not yet accepted by admin");
        }

        if (!acc.getIsActivated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not activated");
        }

        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword() + acc.getSalt()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Auth failed");
        }


        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token za tog korisnika
        //TODO Strahinja: Zasto ovde baca error?
        //Account user = (Account) authentication.getPrincipal();
        Account account = accountService.findByEmail(authenticationRequest.getEmail());
        var roles = new ArrayList<Role>(account.getRoles());

        String jwt = tokenUtils.generateToken(authenticationRequest.getEmail(), roles.get(0).getName());
        String resfresh = tokenUtils.generateRefreshToken(authenticationRequest.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, resfresh, expiresIn));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest token) {
        try {
            if (tokenUtils.validateRefreshToken(token.getToken())) {
                String email = tokenUtils.getUsernameFromToken(token.getToken());
                Account account = accountService.findByEmail(email);
                var roles = new ArrayList<>(account.getRoles());
                String jwt = tokenUtils.generateToken(email, roles.get(0).getName());
                int expiresIn = tokenUtils.getExpiredIn();
                return ResponseEntity.ok(new UserTokenState(jwt, token.getToken(), expiresIn));
            }
        } catch (ExpiredJwtException | TokenExpiredException ex) {
            return ResponseEntity.status(HttpStatus.GONE).body("Token expired");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token not valid");

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewAccount(@Valid @RequestBody RegisterAccountDto dto) {
        try {
            accountService.registerAccount(dto);
            return ResponseEntity.ok("Account created, waiting admin approval");
        } catch (EmailTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This e-mail is taken!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This role does not exist!");
        } catch (EmailRejectedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is blocked temporarily!");
        }
    }

    @PreAuthorize("hasAuthority('changeAccStatusAccept')")
    @GetMapping("/accept-registration/{mail}")
    public ResponseEntity<?> acceptRegistration(@PathVariable String mail) {
        try {
            accountService.approveAccount(mail, true);
        } catch (NotFoundException e) {
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        //Posalji mail
        String link = "nolink";
        try {
            link = accountActivationService.createAcctivationLink(mail);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return ResponseEntity.ok("Error while hashing!");
        }

        mailSenderService.sendSimpleEmail(mail, "IT COMPANY", link);
        return ResponseEntity.ok("Registration approved");
    }

    @PreAuthorize("hasAuthority('changeAccStatusReject')")
    @PostMapping("/reject-registration")
    public ResponseEntity<?> rejectRegistration(@RequestBody RejectAccountDto dto) {
        try {
            accountService.approveAccount(dto.getMail(), false);
        } catch (NotFoundException e) {
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        mailSenderService.sendSimpleEmail(dto.getMail(), "Account rejected", dto.getReason());

        return ResponseEntity.ok("Registration rejected");
    }

    @GetMapping("/activate/{hash}")
    public RedirectView activateAccount(@PathVariable String hash, RedirectAttributes attributes) {
        try {
            accountActivationService.activateAccount(hash);
            //Todo  JOVAN dodaj https
        } catch (EmailActivationExpiredException | NotFoundException e) {
            return new RedirectView("http://localhost:4444/error-page");
        }

        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        //Todo  JOVAN dodaj https
        return new RedirectView("http://localhost:4444/login");
    }

}
