package jass.security.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import jass.security.dto.*;
import jass.security.exception.EmailActivationExpiredException;
import jass.security.exception.EmailTakenException;
import jass.security.exception.NotFoundException;
import jass.security.exception.TokenExpiredException;
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

import java.util.ArrayList;
import java.util.UUID;

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Registration is not accepted by admin");
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
    public ResponseEntity<?> registerNewAccount(@RequestBody RegisterAccountDto dto) {
        try {
            accountService.registerAccount(dto);
            return ResponseEntity.ok("Account created, waiting admin approval");

        } catch (EmailTakenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This e-mail is taken");
        }
    }

    @PreAuthorize("hasAuthority('chagneAccStatus')")
    @GetMapping("/accept-registration/{mail}")
    public ResponseEntity<?> acceptRegistration(@PathVariable String mail) {
        try {
            accountService.approveAccount(mail, true);
        } catch (NotFoundException e) {
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        //Posalji mail
        String link = accountActivationService.createAcctivationLink(mail);

        mailSenderService.sendSimpleEmail(mail, "GAS", link);
        return ResponseEntity.ok("Registration approved");
    }

    @PreAuthorize("hasAuthority('chagneAccStatus')")
    @PostMapping("/reject-registration")
    public ResponseEntity<?> rejectRegistration(@RequestBody RejectAccountDto dto) {
        try {
            accountService.approveAccount(dto.getMail(), false);
        } catch (NotFoundException e) {
            return ResponseEntity.ok("Account with this mail does not exist");
        }

        mailSenderService.sendSimpleEmail(dto.getMail(), "GAS", dto.getReason());

        return ResponseEntity.ok("Registration rejected");
    }

    @GetMapping("/activate/{id}")
    public RedirectView activateAccount(@PathVariable UUID id, RedirectAttributes attributes) {
        try {
            accountActivationService.activateAccount(id);
        } catch (EmailActivationExpiredException | NotFoundException e) {
            return new RedirectView("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAhFBMVEX+/v7///8AAAD7+/v4+Pj09PSWlpba2tpra2sZGRnCwsLz8/Pw8PB2dnY3NzfLy8vq6uqEhIRqamrR0dGqqqoeHh7k5OS5ubkUFBRxcXFAQEA0NDRcXFx9fX2ysrIQEBBiYmKfn5+RkZEoKChPT09HR0ctLS2VlZVMTEwlJSWMjIxDQ0PFG9/yAAAJSElEQVR4nO2bjV/iPg/Al7RzsBcYbAwH42WKqPj//39Pkw70BNbx4p2/55Pvnac3tq5p0zRJo+cJgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgnAJ6l934MfQLJsCDe33KQ+0uRMYz37Z6xePjVJgvjygJ+lhakxRa44uXIlSGtT+fe03fploFrPp0cUSgpWN26A3q0Zk89OlTXVAUdNa6cjRU0XdUOrwv+Zhuq4vfqem1vhhI5Q+aAEJen/oHTyocOj3OcBoMhx+ttqqlXPuT7TD4sEn5qImLfoZNTUr0NNppJTfOoI8VaAjHn2lDTQ2prP64m6xMgD4xbAeJWVvtBwGsdVc+BkJzVew2RQuDTGWxdP5bGCZvWyqcfkUFrG6uFs0V1GYDPCTwXpZmNH6EYNO6uJNcRU4JFRsTHeIK/zK8zjPYrjQ2oDOEvPs+6xaj3vj9Wb2bP43DqO9nt57OZpJCLEfeOCYDDOJ+gFf8nxp/hjqp1FF8zBfDCO7spzwbQrieoZY1dOsSNM4LYIsrHvPOH+IteJpdG1cl/IpobODRsKeb3dDWoFxEIQL09nZQ9rRntKO6ul6gu95oeyWyn91Ov3A1YMPtD9fbrscXCqhYluo7JaodLp8QUwK6KJabNW8vI9m2SveJwB4KzRNFSPEmvdIde9tsauWNhKSCu1tvOmw+R6NViRih1cZA6pg+I5VDBEvbLs1mqZiH4oFYgjsXf1bLY3MVtE8qFhXjduXb3HRZeMn/VNjrALgm+0bgfdCiCDt4UcBP+DaXCbhOIL9Bsj6xMqp60ca/g7vMlOI70tNbpSylofXnWLnMZzjTv+olrrutOvQ/vh5kdSsqDDp4DqbvkOC4wLYg2Ed4NfSNyOafsVBxt7AbRIdv/aidUgSfrvRzGON26KD12UMynZVW0/x2yc0pdlsYFTh7v73FXP4XUIFwRZzOP7kewMe5DgLaPke3ck2JgxIb//Vjn9eQtO5aIMjlrC1DfOqBBOviQ6/feQ1gaLnDFUv5fY5JGuY4AI8z7GEFEQV1uCdikZJOzVYh/7fW5ojCQEWZm6cEgIEHzgFG0B9b4NjFQpW7x5i3GEdmq6VWCqnhB4M55g1yY9TH1v39t4x1G0S2k1bq8quQ9er8sdJ2naDs4lruNnS0MgHA1pf7leZXSX6mXxT62tv01JaU5CvJlmXnu/w44cyai3cOofkhMdrXHewD6AXWP11Ae9hS1U+wbDDGiIJez+SMmzlJgnpIaWnM6y6WAnQZsP/b82hDRTrLb41AZGjAZ8cg//WOtQasjXiSwZN2rrVjpCE3zeVfeK7yaMr+FU+jXGyghJxtUht11xNnJKQAkRKaXC8b/Oyv8qnoQnEeR3YQw1XWvGUhDZXwJ+CDa5/U/SkVDx83ZhJ3OTaa2Rs6945CXUc+3EcRbFB3T8vfMM6pEBAF8vFFp+Toon6LpbQBBR5WSaloVf2RsGxV34rt+0WtP6iYYW4pnMBR+x60tJoiBdf8vsh/KYsBuehqEd+3cdxrE6E7n82cFJC5YW7p93r064evVGiRv+CTNT3OxVlJ7Dm4MmlpUf74SH/aihxMAT3UF/IPeJDD+IEPzKXnloJT3/E//R+q4SUUMyesdbQfqpCfml5Wgd/t4S8Dfo7HKeuEP98bPF3JLw+X0raOcRZ4bCDoHb4ctoY/e455ENBCCb9zHUCBQ+49f+6hJ6VsFMAe86W0toqBpxGa2+B8jTnb7IS3pAEYA/3+0XKprOE7sPlsxLyJSPh0mufRKPKWwxOTxJn6ljCG3ZD8JsKqD+vgpric9Dh+Py8hOQ8s4SuFoIZhnxEcZzUp4tjI6Ercd6C8RVOJMzp4pQO7q6XkDRDQTDHoTO4iCubkzuWkK9aCa8+t4AsKI5zzZRozrFyGXq+97yWQgxZ36h6e+fMy0tyauBYRHvYXeJ8CFefW5AaGUFOSKhHmMQ3WRpSkBw3BTgKtyhh+lJ8qYk7YA/PezjLrpfQg1ech8fpFCNhhU/22NnRwDkJqVwqqnDkO/sGwftjboOsU+7pBjfBDZsFFIi7eD987CNb7S/mmO8rIVsb4EoFTx085X0BpjZTt8T3qauqhhbsGMsUPoPlva/O39M5KdMNETAkOJ/axw/rkQpActwOuUatg4TjiAfn8DiJa4SmJVAWHQo4YYrbKTW1v9R8YBayUjWurB26SjzaLIotFXpQNsu386fYax6wR+keO5Kw9KGpumgKTkxbKoKUbITnOWJ8RYf1G1ynJijUjSCqcRfNCA8HdEB8QybKzFG9wkXE5gC0rfpRtPjfl5z+cRbv0RxqsHaPSxRtuYEJLDa4WioOZ9vbMHoaTswwKZ9rN20nuMQU1PANV7m96UoJNeh4hLhIad/37dI2vTIjl9Ar3O3aehrdFArtUXGwWOFkCSSebtUDWsNamWGuYrZOh0JqCjCX70jBozq2s50hGYoEcR1GnNu0xUjLGX4E3Qofm5oo+JRPp8WwLt9xVYXWJXSouiK3Ix494kuY8vhy2olGabg2ozSKweuQdj0PJUCKMeJ8NM2K2Ndxmk0Xc95kuaLOERewhG/L6dT8JZbL/Cmp5oj49lQcMmStWQxaihrSh2fsJ8thkEZa+3ERhPViiziofXXjITCbqfR1gvj8ti6TpFx/PCP29jFPu5ryEfsTrib998mkb3hsKk0H42ng7zced0UNpbf1cI34ONiMTSeSstoMHhG3u0xrcFVidxLTD5PJZ/auytNOzbJJiB76j5aV+er3Z5vydRrE+pQb1tIDM5bpct3/UojbH9TDCNz7VTfMIKZZvhivq/UuLPwOHrftF23rfhofiHzts2XXNhfcWUI+pTBLOFiOStOJ3tMyS9kk3y1RyvVkB7fE9M3vNHRUEArfaXaz4wqg1pYoW/5nM4qr/u9UKsTKThWU/Iscza/MdOgV0FnalxubXymwhYUXj76ydaXe4Zjijrl8LvJsRourfDutbut2fN7ZzGDTSAeH6CvKjoodXGVtLG8b9xESvlQkWQPYsQgdvhjyxvFuNu390dpF8PK38tk2OpbDC4IgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCIIgCILwf8n/AID5XEDi2eOhAAAAAElFTkSuQmCC");
        }

        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    }

    @GetMapping("/gascina")
    public RedirectView redirectWithUsingRedirectView(
            RedirectAttributes attributes) {
        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView("https://www.youtube.com/");
    }
}
