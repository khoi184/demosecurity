package com.thuanthanhtech.demosecurity.service.impl;

import com.thuanthanhtech.demosecurity.config.CustomUserDetails;
import com.thuanthanhtech.demosecurity.dto.SignUpDto;
import com.thuanthanhtech.demosecurity.entity.Role;
import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.exception.FailOTPException;
import com.thuanthanhtech.demosecurity.exception.UserNotFoundException;
import com.thuanthanhtech.demosecurity.exception.UsernameExistedException;
import com.thuanthanhtech.demosecurity.repository.RoleRepository;
import com.thuanthanhtech.demosecurity.repository.UserRepository;
import com.thuanthanhtech.demosecurity.service.EmailService;
import com.thuanthanhtech.demosecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

    @Override
    public UserDetails loadUserByUserId(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Can not found user with id: " + id)
        );
        return new CustomUserDetails(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsernameOrEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Can not found username: " + username);
        }
        return new CustomUserDetails(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void changeAccountPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public String forgotPassword(String email) {

        Optional<User> userOptional = Optional
                .ofNullable(userRepository.findByEmail(email));

        if (userOptional.isEmpty()) {
            return "Invalid email id.";
        }

        User user = userOptional.get();
        user.setToken(generateToken());
        user.setExpiredToken(LocalDateTime.now());

        user = userRepository.save(user);

        return user.getToken();
    }

    @Override
    public String resetPassword(String token, String password) {

        Optional<User> userOptional = Optional
                .ofNullable(userRepository.findByToken(token));

        if (userOptional.isEmpty()) {
            return "Invalid token.";
        }

        LocalDateTime tokenCreationDate = userOptional.get().getExpiredToken();

        if (isTokenExpired(tokenCreationDate)) {
            return "Token expired.";
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(password));
        user.setToken(null);
        user.setExpiredToken(null);

        userRepository.save(user);

        return "Your password successfully updated.";
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID())
                .append(UUID.randomUUID()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }

    @Override
    public User findByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setToken(token);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException(email);
        }
    }

    @Override
    public void registerUser(SignUpDto signUpDto) throws UsernameExistedException {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new UsernameExistedException();
        } else {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER");
            roles.add(userRole);

            User user = new User();
            user.setUsername(signUpDto.getUsername());
            user.setEmail(signUpDto.getEmail());
            user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
            user.setRole(roles);
            userRepository.save(user);
        }
    }

    @Override
    public void validateOTP(Integer otpNumber, String email) throws FailOTPException, UserNotFoundException {
        Optional<User> optional = Optional.ofNullable(findByEmail(email));
        if (optional.isPresent()) {
            User u = optional.get();
            //Validate the Otp
            if (otpNumber >= 0) {
                int serverOtp = u.getOneTimePassword();

                if (serverOtp > 0) {
                    if (otpNumber == serverOtp) {
                        emailService.clearOTP(u);
                        u.setPassword(passwordEncoder.encode("123456"));
                        userRepository.save(u);
                    } else {
                        throw new FailOTPException();
                    }
                }
            }
        } else {
            throw new UserNotFoundException(email);
        }
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}


