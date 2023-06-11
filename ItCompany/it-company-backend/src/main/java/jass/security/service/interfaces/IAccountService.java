package jass.security.service.interfaces;

import com.google.zxing.WriterException;
import jass.security.dto.*;
import jass.security.exception.*;
import jass.security.model.Account;
import jass.security.model.PasswordlessLoginToken;
import jass.security.model.RegistrationRequestStatus;
import jass.security.model.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IAccountService extends ICrudService<Account> {
    Account findByEmail(String email) throws NotFoundException;

    void registerAccount(RegisterAccountDto dto) throws EmailTakenException, NotFoundException, EmailRejectedException, IOException, WriterException;

    UUID registerAdminAccount(RegisterAdminAccountDto dto) throws EmailTakenException, IOException, WriterException;

    void approveAccount(String email, Boolean approve) throws NotFoundException;

    ArrayList<Account> findAllByStatus(RegistrationRequestStatus status);

    ArrayList<AccountApprovalDto> findAllByStatusInfo(RegistrationRequestStatus status);

    void changeAdminPassword(String email, ChangeAdminPasswordDto dto) throws IncorrectPasswordException, NotFoundException;

    void generatePasswordlessLoginToken(String email) throws NotFoundException;

    PasswordlessLoginToken usePLToken(String token) throws NotFoundException, PlTokenUsedException, TokenExpiredException;

    List<Account> findAllAccountsByRole(String role);
    
    void blockUnblockAccount(String email) throws NotFoundException;

    void changePassword(ChangePasswordDto dto) throws NotFoundException, PasswordsDontMatchException;
    byte[] getTwoFactorAuthQr(String email) throws IOException, WriterException;
}
