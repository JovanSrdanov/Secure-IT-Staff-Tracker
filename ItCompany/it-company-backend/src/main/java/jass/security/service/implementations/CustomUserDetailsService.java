package jass.security.service.implementations;

import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Privilege;
import jass.security.model.Role;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IAccountService accountService;
    
    /*@Autowired
    private IRoleService roleService;*/

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Account account = null;
        try {
            account = accountService.findByEmail(email);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("This account odes not exist!");
        }

        //TODO Strahinja: Ako je acc pendind da li to proveriti ovde ili?
        if (account == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
            /*return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true,
                    getAuthorities(Arrays.asList(
                            roleService.findByName("ROLE_USER"))));*/
        }

        if(account.getIsBlocked()) {
            throw new UsernameNotFoundException("User is blocked!");
        }

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(), account.getPassword(), true, true, true,
                true, getAuthorities(account.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
