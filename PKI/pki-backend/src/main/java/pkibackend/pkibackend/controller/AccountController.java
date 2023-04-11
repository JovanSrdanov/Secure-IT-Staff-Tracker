package pkibackend.pkibackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pkibackend.pkibackend.service.interfaces.IAccountService;

@RestController
@RequestMapping("account")
public class AccountController {
    private final IAccountService _accountService;

    @Autowired
    public AccountController(IAccountService accountService) {
        _accountService = accountService;
    }
}
