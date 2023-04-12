package pkibackend.pkibackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.dto.BooleanResponse;
import pkibackend.pkibackend.service.interfaces.IAccountService;

import java.math.BigInteger;

@RestController
@RequestMapping("account")
public class AccountController {
    private final IAccountService _accountService;

    @Autowired
    public AccountController(IAccountService accountService) {
        _accountService = accountService;
    }

    @GetMapping("exists-by-email/{email}")
    public ResponseEntity<BooleanResponse> existsByEmail(@PathVariable("email") String email){
        boolean exists = _accountService.existsByEmail(email);
        return new ResponseEntity<>(new BooleanResponse(exists), HttpStatus.OK);
    }
}
