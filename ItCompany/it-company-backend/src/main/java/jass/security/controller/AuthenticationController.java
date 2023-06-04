package jass.security.controller;

import ClickSend.ApiClient;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jass.security.dto.*;
import jass.security.exception.*;
import jass.security.model.Account;
import jass.security.model.RegistrationRequestStatus;
import jass.security.service.implementations.AdministratorService;
import jass.security.service.implementations.MailSenderService;
import jass.security.service.interfaces.IAccountActivationService;
import jass.security.service.interfaces.IAccountService;
import jass.security.utils.IPUtils;
import jass.security.utils.SMSUtils;
import jass.security.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.security.Principal;
import java.util.ArrayList;

//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    //Twilio
//    @Value("${twilioAccountSid}")
//    private String TWILIO_ACCOUNT_SID;
//    @Value("${twilioAuthToken}")
//    private String TWILIO_AUTH_TOKEN;
//    @Value("${twilioPhoneNumber}")
//    private String TWILIO_PHONE_NUMBER;

    //Clicksend
    @Autowired
    private ApiClient clickSendConfig;

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

    @Autowired
    private AdministratorService administratorService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletRequest request) {
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException

        // SMS sending test
        //TWILIO
//        var sid = TWILIO_ACCOUNT_SID;
//        var token = TWILIO_AUTH_TOKEN;
//        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
//
//        Message.creator(new PhoneNumber("0628387347"),
//                        new PhoneNumber(TWILIO_PHONE_NUMBER), "Test").create();
        Account acc;
        try {
            acc = accountService.findByEmail(authenticationRequest.getEmail());
        } catch (NotFoundException e) {
            logger.warn("User failed to log in from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    ", reason: an account with the given email does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        if (acc.getStatus() != RegistrationRequestStatus.APPROVED) {
            logger.warn("User failed to log in from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    ", reason: registration is not yet accepted by an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Registration is not yet accepted by an admin");
        }

        if (!acc.getIsActivated()) {
            logger.warn("User failed to log in from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    ", reason: account not activated");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not activated");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword() + acc.getSalt()));
            logger.info("User with IP: " + IPUtils.getIPAddressFromHttpRequest(request) + " successfully authenticated");
        } catch (BadCredentialsException e) {
            logger.warn("User failed to log in from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    ", reason: authentication failed: incorrect credentials");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Auth failed");
        }


        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token za tog korisnika
        //TODO Strahinja: Zasto ovde baca error?
        //Account user = (Account) authentication.getPrincipal();
        var roles = new ArrayList<>(acc.getRoles());

        String jwt = tokenUtils.generateToken(authenticationRequest.getEmail(), roles.get(0).getName());
        String resfresh = tokenUtils.generateRefreshToken(authenticationRequest.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        logger.info("User successfully logged in from IP: " + IPUtils.getIPAddressFromHttpRequest(request));
        return ResponseEntity.ok(new UserTokenState(jwt, resfresh, expiresIn));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest token, HttpServletRequest request) {
        try {
            if (tokenUtils.validateRefreshToken(token.getToken())) {
                String email = tokenUtils.getUsernameFromToken(token.getToken());
                Account account = accountService.findByEmail(email);
                var roles = new ArrayList<>(account.getRoles());
                String jwt = tokenUtils.generateToken(email, roles.get(0).getName());
                int expiresIn = tokenUtils.getExpiredIn();
                logger.info("User successfully refreshed their authentication token, from IP: " +
                        IPUtils.getIPAddressFromHttpRequest(request));
                return ResponseEntity.ok(new UserTokenState(jwt, token.getToken(), expiresIn));
            }
        } catch (ExpiredJwtException | TokenExpiredException ex) {
            logger.warn("User failed to refresh their authentication token, from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request),
                    " reason: authentication token expired");
            return ResponseEntity.status(HttpStatus.GONE).body("Token expired");
        } catch (NotFoundException e) {
            logger.warn("User failed to refresh their authentication token, from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request),
                    " reason: an account with the given email does not exisst");
            throw new RuntimeException(e);
        }

        logger.warn("User failed to refresh their authentication token, from IP: " +
                        IPUtils.getIPAddressFromHttpRequest(request),
                " reason: authentication token is invalid");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token not valid");

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewAccount(@Valid @RequestBody RegisterAccountDto dto, HttpServletRequest request) {
        try {
            accountService.registerAccount(dto);
            logger.info("User registered successfully, from IP: " + IPUtils.getIPAddressFromHttpRequest(request),
                    " awaiting admin approval");
            return ResponseEntity.ok("Account created, waiting admin approval");
        } catch (EmailTakenException e) {
            logger.warn("User failed to register, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: given email is taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This e-mail is taken!");
        } catch (NotFoundException e) {
            logger.warn("User failed to register, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: given role does not exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This role does not exist!");
        } catch (EmailRejectedException e) {
            logger.warn("User failed to register, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: given email is temporarily blocked");

            //Clicksend
            var admins = administratorService.findAll();
            if (admins != null) {
                for (var admin : admins) {
                    SMSDto smsDto = new SMSDto("IT Company", "User with an IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request)
                            + " tried to register with a blocked email: " + dto.getEmail(), admin.getPhoneNumber());
                    SMSUtils.sendSMS(logger, clickSendConfig, smsDto);
                }
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is blocked temporarily!");
        }
    }

    @PostMapping("register-admin")
    @PreAuthorize("hasAuthority('registerAdmin')")
    public ResponseEntity<?> registerNewAdminAccount(@Valid @RequestBody RegisterAdminAccountDto dto, HttpServletRequest request) {
        try {
            accountService.registerAdminAccount(dto);
            logger.info("Admin successfully registered, from IP: " + IPUtils.getIPAddressFromHttpRequest(request));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EmailTakenException e) {
            logger.warn("User failed to register an admin, from IP: " + IPUtils.getIPAddressFromHttpRequest(request),
                    " reason: provided email is already taken");
            return new ResponseEntity<>("This e-mail is taken!", HttpStatus.CONFLICT);
        }
    }


    @PreAuthorize("hasAuthority('changeAccStatusAccept')")
    @GetMapping("/accept-registration/{mail}")
    public ResponseEntity<?> acceptRegistration(@PathVariable String mail) {
        try {
            accountService.approveAccount(mail, true);
        } catch (NotFoundException e) {
            logger.warn("Failed to accept an account registration with an email: " + mail +
                    " reason: an account with a provided email does not exist");
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        //Posalji mail
        String link;

        Account account = null;
        try {
            account = accountService.findByEmail(mail);
            link = accountActivationService.createAcctivationLink(mail);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.warn("Failed to create an activation link for the account with an ID: " + account.getId()
                   + ", invalid signing key");
            return ResponseEntity.ok("Error while hashing!");
        } catch (NotFoundException e) {
            logger.warn("Failed to create an activation link with an email: " + mail
                    + ", an account with the given email does not exist");
            return new ResponseEntity<>("An account with the given email does not exist", HttpStatus.NOT_FOUND);
        }
        String htmlLink = "Click this <a href="+ link + ">link</a> to activate account";
        mailSenderService.sendHtmlMail(mail, "IT COMPANY", htmlLink);

        logger.info("Email with an registration approved message successfully sent");
        return ResponseEntity.ok("Registration approved");
    }

    @PreAuthorize("hasAuthority('changeAccStatusReject')")
    @PostMapping("/reject-registration")
    public ResponseEntity<?> rejectRegistration(@RequestBody RejectAccountDto dto) {
        try {
            accountService.approveAccount(dto.getMail(), false);
        } catch (NotFoundException e) {
            logger.warn("Failed to accept registration with email: " + dto.getMail() +
                    " reason: an account with the given email does not exist");
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        mailSenderService.sendSimpleEmail(dto.getMail(), "Account rejected", dto.getReason());

        logger.info("Email with an registration rejection message successfully sent");
        return ResponseEntity.ok("Registration rejected");
    }

    @GetMapping("/activate/{hash}")
    public RedirectView activateAccount(@PathVariable String hash, RedirectAttributes attributes) {
        try {
            accountActivationService.activateAccount(hash);
           
        } catch (EmailActivationExpiredException | NotFoundException e) {
            return new RedirectView("https://localhost:4444/error-page");
        }

        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");

        return new RedirectView("https://localhost:4444/login");
    }

    @PatchMapping("/admin-change-password")
    @PreAuthorize("hasAuthority('adminPasswordChange')")
    public ResponseEntity<?> changeAdminPassword(Principal principal, @RequestBody ChangeAdminPasswordDto dto,
                                                 HttpServletRequest request) {
        try {
            accountService.changeAdminPassword(principal.getName(), dto);
            logger.info("Admin user successfully changed the password, from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IncorrectPasswordException e) {
            logger.warn("Admin user failed to change the password, from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + ", reason: provided old password is incorrect");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            logger.warn("Admin user failed to change the password, from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + ", reason: an account with a provided email not found");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login/passwordless/generate")
    public ResponseEntity<?> generatePasswordlessLoginToken(@RequestBody GeneratePasswordlessLoginTokenDto dto,
                                                            HttpServletRequest request) {
        try {
            accountService.generatePasswordlessLoginToken(dto.getEmail());
            logger.info("Request for a passwordless login token successful, from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("Request for a passwordless login token failed, from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + ", reason: an account with a provided email not found");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/passwordless-login/{token}")
    public ResponseEntity<?> passwordlessLogin(@PathVariable String token, HttpServletRequest request) {
        try {
            var plToken = accountService.usePLToken(token);
            Account account = accountService.findByEmail(plToken.getEmail());
            var roles = new ArrayList<>(account.getRoles());

            String jwt = tokenUtils.generateToken(plToken.getEmail(), roles.get(0).getName());
            String resfresh = tokenUtils.generateRefreshToken(plToken.getEmail());
            int expiresIn = tokenUtils.getExpiredIn();

            logger.info("User with IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + " successfully logged in using the passwordless " +
                    "method");
            return new ResponseEntity<>(new UserTokenState(jwt, resfresh, expiresIn), HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to log in using the passwordless method from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + ", reason: invalid token");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (PlTokenUsedException | TokenExpiredException e) {
            logger.warn("User failed to log in using the passwordless method from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request) + ", reason: token expired");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}