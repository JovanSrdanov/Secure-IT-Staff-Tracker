package jass.security.service.implementations;

import jass.security.dto.*;
import jass.security.exception.EmailRejectedException;
import jass.security.exception.EmailTakenException;
import jass.security.exception.IncorrectPasswordException;
import jass.security.exception.NotFoundException;
import jass.security.model.*;
import jass.security.repository.*;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IRejectedMailService;
import jass.security.utils.DateUtils;
import jass.security.utils.ObjectMapperUtils;
import jass.security.utils.RandomPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Primary
public class AccountService implements IAccountService {
    private final IAccountRepository _accountRepository;

    private final IRoleRepository _roleRespository;

    private final IHrManagerRepository hrManagerRepository;

    private final IProjectManagerRepository projectManagerRepository;

    private final ISoftwareEngineerRepository softwareEngineerRepository;

    private final IAddressRepository addressRepository;

    private final IRejectedMailService rejectedMailService;
    private final IAdministratorRepository administratorRepository;
    private final MailSenderService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(IAccountRepository accountRepository, IRoleRepository roleRespository, IHrManagerRepository hrManagerRepository, IProjectManagerRepository projectManagerRepository, ISoftwareEngineerRepository softwareEngineerRepository, IAddressRepository addressRepository, IRejectedMailService rejectedMailService, IAdministratorRepository administratorRepository, MailSenderService mailService) {
        this._accountRepository = accountRepository;
        _roleRespository = roleRespository;
        this.hrManagerRepository = hrManagerRepository;
        this.projectManagerRepository = projectManagerRepository;
        this.softwareEngineerRepository = softwareEngineerRepository;
        this.addressRepository = addressRepository;
        this.rejectedMailService = rejectedMailService;
        this.administratorRepository = administratorRepository;
        this.mailService = mailService;
    }

    @Override
    public List<Account> findAll() {
        return _accountRepository.findAll();
    }

    @Override
    public Account findById(UUID id) {
        return null;
    }

    @Override
    public Account save(Account entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return _accountRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Account findByEmail(String email) {
        return _accountRepository.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UUID registerAccount(RegisterAccountDto dto) throws EmailTakenException, NotFoundException, EmailRejectedException {
        //Check if mail is not rejected
        if (rejectedMailService.isMailRejected(dto.getEmail())) {
            throw new EmailRejectedException();
        }


        //make adres
        Address address = makeAddress(dto.getAddress());
        //make employye
        UUID employeeId;
        Role role;

        addressRepository.save(address);

        if (dto.getRole().equals("hrManager")) {

            var employee = makeHrManager(dto, address);
            employeeId = employee.getId();
            role = _roleRespository.findByName("ROLE_HR_MANAGER");
            hrManagerRepository.save(employee);

        } else if (dto.getRole().equals("projectManager")) {

            var employee = makeProjectManager(dto, address);
            employeeId = employee.getId();
            role = _roleRespository.findByName("ROLE_PROJECT_MANAGER");
            projectManagerRepository.save(employee);

        } else if (dto.getRole().equals("softwareEngineer")) {

            var employee = makeSoftwareEngineer(dto, address);
            employeeId = employee.getId();
            role = _roleRespository.findByName("ROLE_ENGINEER");
            softwareEngineerRepository.save(employee);

        } else {
            throw new NotFoundException("Nepostojeca rola");
        }
        //make acc
        Account newAcc = makeAccount(dto, employeeId);


        //TODO Strahinja: Da li ovo ovako ili nekako bolje da se salju ove role sa fronta?
        var roles = new ArrayList<Role>();
        roles.add(role);
        newAcc.setRoles(roles);
        role.getUsers().add(newAcc);

        save(newAcc);
        _roleRespository.save(role);

        return newAcc.getId();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public UUID registerAdminAccount(RegisterAdminAccountDto dto) throws EmailTakenException {
        Address address = makeAddress(dto.getAddress());
        UUID adminId = UUID.randomUUID();

        Administrator admin = new Administrator(adminId, dto.getName(), dto.getSurname(), address, dto.getPhoneNumber(), dto.getProfession());
        administratorRepository.save(admin);

        var role = _roleRespository.findByName("ROLE_ADMIN_PASSWORD_CHANGE");

        Account newAcc = makeAdminAccount(dto, adminId);


        //TODO Strahinja: Da li ovo ovako ili nekako bolje da se salju ove role sa fronta?
        var roles = new ArrayList<Role>();
        roles.add(role);
        newAcc.setRoles(roles);
        role.getUsers().add(newAcc);

        save(newAcc);
        _roleRespository.save(role);

        return newAcc.getId();
    }

    private SoftwareEngineer makeSoftwareEngineer(RegisterAccountDto dto, Address address) {
        SoftwareEngineer softwareEngineer = new SoftwareEngineer();
        softwareEngineer.setName(dto.getName());
        softwareEngineer.setSurname(dto.getSurname());
        softwareEngineer.setPhoneNumber(dto.getPhoneNumber());
        softwareEngineer.setProfession(dto.getProfession());
        softwareEngineer.setId(UUID.randomUUID());
        softwareEngineer.setAddress(address);
        softwareEngineer.setProfession(dto.getProfession());

        //softwareEngineer.setSkills(new ArrayList<>());
        //softwareEngineer.setProjectStats(new ArrayList<>());

        softwareEngineerRepository.save(softwareEngineer);
        return softwareEngineer;
    }

    private ProjectManager makeProjectManager(RegisterAccountDto dto, Address address) {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setName(dto.getName());
        projectManager.setSurname(dto.getSurname());
        projectManager.setPhoneNumber(dto.getPhoneNumber());
        projectManager.setProfession(dto.getProfession());
        projectManager.setId(UUID.randomUUID());
        projectManager.setAddress(address);
        projectManager.setProfession(dto.getProfession());

        //projectManager.setProjects(new ArrayList<>());

        projectManagerRepository.save(projectManager);
        return projectManager;
    }

    private HrManager makeHrManager(RegisterAccountDto dto, Address address) {
        HrManager hrManager = new HrManager();
        hrManager.setName(dto.getName());
        hrManager.setSurname(dto.getSurname());
        hrManager.setPhoneNumber(dto.getPhoneNumber());
        hrManager.setProfession(dto.getProfession());
        hrManager.setId(UUID.randomUUID());
        hrManager.setAddress(address);
        hrManager.setProfession(dto.getProfession());

        hrManagerRepository.save(hrManager);
        return hrManager;
    }

    private Address makeAddress(AddressDto addressDto) {
        Address address = ObjectMapperUtils.map(addressDto, Address.class);
        address.setId(UUID.randomUUID());
        return address;
    }

    private Account makeAccount(RegisterAccountDto dto, UUID employeeId) throws EmailTakenException {
        if (findByEmail(dto.getEmail()) != null) {
            throw new EmailTakenException();
        }

        Account newAcc = new Account();

        String salt = genereteSalt();

        newAcc.setEmail(dto.getEmail());
        newAcc.setPassword(passwordEncoder.encode(dto.getPassword() + salt));
        newAcc.setSalt(salt);
        newAcc.setId(UUID.randomUUID());
        newAcc.setEmployeeId(employeeId);
        newAcc.setStatus(RegistrationRequestStatus.PENDING);
        newAcc.setIsActivated(false);

        return newAcc;
    }

    private Account makeAdminAccount(RegisterAdminAccountDto dto, UUID adminId) throws EmailTakenException {
        if (findByEmail(dto.getEmail()) != null) {
            throw new EmailTakenException();
        }

        Account newAcc = new Account();

        String salt = genereteSalt();

        newAcc.setEmail(dto.getEmail());
        String password = RandomPasswordGenerator.generatePassword(15);
        newAcc.setPassword(passwordEncoder.encode(password + salt));
        newAcc.setSalt(salt);
        newAcc.setId(UUID.randomUUID());
        newAcc.setEmployeeId(adminId);
        newAcc.setStatus(RegistrationRequestStatus.APPROVED);
        newAcc.setIsActivated(true);

        String mailBody = "Your password is: " +password + "\n You will need to change it after first login.";
        mailService.sendSimpleEmail(dto.getEmail(), "New registration password", mailBody);

        return newAcc;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveAccount(String email, Boolean approve) throws NotFoundException {
        Account account = findByEmail(email);
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        if (approve) {
            account.setStatus(RegistrationRequestStatus.APPROVED);

            var softwareEngineer = softwareEngineerRepository.findById(account.getEmployeeId());
            if (softwareEngineer.isPresent()) {
                softwareEngineer.get().setDateOfEmployment(new Date());
                softwareEngineerRepository.save(softwareEngineer.get());
            }

            save(account);
        } else {
            //account.setStatus(RegistrationRequestStatus.REJECTED);

            //Del acc
            Account acc = _accountRepository.findByEmail(email);
            _accountRepository.deleteById(acc.getId());
            //Del employee
            UUID addressId = UUID.randomUUID();
            if (hrManagerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = hrManagerRepository.findById(acc.getEmployeeId()).get();
                addressId = employee.getAddress().getId();
                hrManagerRepository.deleteById(employee.getId());
            } else if (projectManagerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = projectManagerRepository.findById(acc.getEmployeeId()).get();
                addressId = employee.getAddress().getId();
                projectManagerRepository.deleteById(employee.getId());
            } else if (softwareEngineerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = softwareEngineerRepository.findById(acc.getEmployeeId()).get();
                addressId = employee.getAddress().getId();
                softwareEngineerRepository.deleteById(employee.getId());
            }
            //Del adr
            addressRepository.deleteById(addressId);

            //Add to rejected table
            Date date = DateUtils.addHoursToDate(new Date(), 72);
            RejectedMail rejectedMail = new RejectedMail(UUID.randomUUID(), email, date);
            rejectedMailService.save(rejectedMail);

        }
    }

    @Override
    public ArrayList<Account> findAllByStatus(RegistrationRequestStatus status) {
        var accs = _accountRepository.findAllByStatus(status);
        return accs;
    }

    @Override
    public ArrayList<AccountApprovalDto> findAllByStatusInfo(RegistrationRequestStatus status) {
        var accs = findAllByStatus(status);
        ArrayList<AccountApprovalDto> infos = new ArrayList<>();

        for (var acc : accs) {
            AccountApprovalDto info = new AccountApprovalDto();

            if (hrManagerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = hrManagerRepository.findById(acc.getEmployeeId()).get();

                info.setEmail(acc.getEmail());
                info.setName(employee.getName());
                info.setSurname(employee.getSurname());
                info.setAddress(ObjectMapperUtils.map(employee.getAddress(), AddressDto.class));
                info.setPhoneNumber(employee.getPhoneNumber());
                info.setProfession(employee.getProfession());
                var roles = new ArrayList<Role>(acc.getRoles());
                info.setRole(roles.get(0).getName());

            } else if (projectManagerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = projectManagerRepository.findById(acc.getEmployeeId()).get();

                info.setEmail(acc.getEmail());
                info.setName(employee.getName());
                info.setSurname(employee.getSurname());
                info.setAddress(ObjectMapperUtils.map(employee.getAddress(), AddressDto.class));
                info.setPhoneNumber(employee.getPhoneNumber());
                info.setProfession(employee.getProfession());
                var roles = new ArrayList<Role>(acc.getRoles());
                info.setRole(roles.get(0).getName());
            } else if (softwareEngineerRepository.findById(acc.getEmployeeId()).isPresent()) {
                var employee = softwareEngineerRepository.findById(acc.getEmployeeId()).get();

                info.setEmail(acc.getEmail());
                info.setName(employee.getName());
                info.setSurname(employee.getSurname());
                info.setAddress(ObjectMapperUtils.map(employee.getAddress(), AddressDto.class));
                info.setPhoneNumber(employee.getPhoneNumber());
                info.setProfession(employee.getProfession());
                var roles = new ArrayList<Role>(acc.getRoles());
                info.setRole(roles.get(0).getName());
            }

            infos.add(info);
        }
        return infos;
    }

    private boolean passwordValid(String inputPassword, String dbPassword, String dbSalt){
        return passwordEncoder.matches(inputPassword + dbSalt, dbPassword);
    }

    @Override
    public void changeAdminPassword(String email, ChangeAdminPasswordDto dto) throws IncorrectPasswordException, NotFoundException {
        var account = _accountRepository.findByEmail(email);

        if(account == null){
            throw new NotFoundException("Account with given email not found");
        }

        if(!passwordValid(dto.getOldPassword(), account.getPassword(), account.getSalt())){
           throw new IncorrectPasswordException("Old password is incorrect");
        }


        String newSalt = genereteSalt();
        String newPassword = passwordEncoder.encode(dto.getNewPassword() + newSalt);

        account.setSalt(newSalt);
        account.setPassword(newPassword);


        //Role change

        var oldRole = _roleRespository.findByName("ROLE_ADMIN_PASSWORD_CHANGE");
        var newRole = _roleRespository.findByName("ROLE_ADMIN");

        //TODO Strahinja: Da li ovo ovako ili nekako bolje da se salju ove role sa fronta?
        var roles = new ArrayList<Role>();
        roles.add(newRole);
        account.setRoles(roles);

        oldRole.getUsers().remove(account);
        newRole.getUsers().add(account);

        _accountRepository.save(account);
        _roleRespository.save(newRole);
    }


    private String genereteSalt() {
        int length = 8; // Desired length of the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }


}
