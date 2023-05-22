package jass.security.service.interfaces;

import jass.security.dto.AccountApprovalDto;
import jass.security.dto.ChangeAdminPasswordDto;
import jass.security.dto.RegisterAccountDto;
import jass.security.dto.RegisterAdminAccountDto;
import jass.security.exception.*;
import jass.security.model.Account;
import jass.security.model.PasswordlessLoginToken;
import jass.security.model.RegistrationRequestStatus;

import java.util.ArrayList;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email);
    UUID registerAccount(RegisterAccountDto dto) throws EmailTakenException, NotFoundException, EmailRejectedException;
    UUID registerAdminAccount(RegisterAdminAccountDto dto) throws EmailTakenException;

    void approveAccount(String email, Boolean approve) throws NotFoundException;

    ArrayList<Account> findAllByStatus(RegistrationRequestStatus status);
    ArrayList<AccountApprovalDto> findAllByStatusInfo(RegistrationRequestStatus status);
     void changeAdminPassword(String email, ChangeAdminPasswordDto dto) throws IncorrectPasswordException, NotFoundException;

     void generatePasswordlessLoginToken(String email) throws NotFoundException;
    PasswordlessLoginToken usePLToken(String token) throws NotFoundException, PlTokenUsedException, TokenExpiredException;


    }
