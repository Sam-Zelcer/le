package sam.dev.le.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.UserRepository;
import sam.dev.le.repository.entitys.MyUserDetails;
import sam.dev.le.repository.entitys.User;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        return optionalUser.map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with name:" + username + " not found"));
    }
}

