package wad.authentication;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import wad.domain.Account;
import wad.repository.AccountRepository;

/**
 * This class is for authenticating user on logging.
 * 
 * @author Ensio
 */
@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        String nickname = a.getPrincipal().toString();
        String password = a.getCredentials().toString();

        Account account = accountRepository.findByNickname(nickname);

        if (account == null) {
            throw new AuthenticationException("Unable to authenticate user " + nickname) {
            };
        }

        if (!BCrypt.hashpw(password, account.getSalt()).equals(account.getPassword())) {
            throw new AuthenticationException("Unable to authenticate user " + nickname) {
            };
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("USER"));

        return new UsernamePasswordAuthenticationToken(account.getNickname(), password, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

}