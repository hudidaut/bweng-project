package at.technikum.springrestbackend.security.jwt;

import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByUsername(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or username: " + identifier)));

        // Convert Role enum to a String with "ROLE_" prefix
        String roleName = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                identifier, // Use email or username
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleName)) // Pass the role as a string
        );
    }

}


