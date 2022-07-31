package com.nisum.userApi.service;

import com.nisum.userApi.config.JwtUtils;
import com.nisum.userApi.exception.UserApiBussinesException;
import com.nisum.userApi.model.dto.UserDto;
import com.nisum.userApi.model.dto.UserDtoSaved;
import com.nisum.userApi.model.entity.User;
import com.nisum.userApi.repository.UserRepository;
import com.nisum.userApi.util.RegExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserApiServiceImpl implements UserApiService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserApiServiceImpl(UserRepository userRepository,
                              JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public User getUserById(final UUID prId) throws UserApiBussinesException {
        return userRepository.findById(prId).orElseThrow(
                () -> new UserApiBussinesException("User not found", HttpStatus.NOT_FOUND));
    }

    public Optional<User> getUserByEmail(final String email) throws UserApiBussinesException {
        return userRepository.findByEmail(email);
    }

    public UserDto getUserDtoById(final UUID prId) throws UserApiBussinesException {
        return new UserDto(this.getUserById(prId));
    }

    public UserDto getUserDtoByEmail(final String email) throws UserApiBussinesException {
        User user = this.getUserByEmail(email).orElseThrow(
                () -> new UserApiBussinesException("User not found", HttpStatus.NOT_FOUND));
        return new UserDto(user);
    }

    public List<UserDto> getAllUser() {
        List<UserDto> lstUsers = new ArrayList<>();
        userRepository.findAll().forEach(x -> lstUsers.add(new UserDto(x)));
        return lstUsers;
    }

    public UserDtoSaved saveUser(final UserDto prUserDto) throws UserApiBussinesException {
        try {
            User prNewUser = new User(prUserDto);
            validUser(prNewUser);
            if (userRepository.findByEmail(prNewUser.getEmail()).isPresent()) {
                throw new UserApiBussinesException("Email exist", HttpStatus.FOUND);
            }
            prNewUser.setPassword(passwordEncoder.encode(prNewUser.getPassword()));
            prNewUser.setCreated(LocalDateTime.now());
            prNewUser.setIsActive(true);
            prNewUser.setToken(jwtUtils.generateToken(prNewUser.getEmail()));
            userRepository.save(prNewUser);
            return new UserDtoSaved(prNewUser);
        } catch (Exception e) {
            if (e instanceof UserApiBussinesException) {
                throw e;
            } else {
                throw new UserApiBussinesException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validUser(User prUser) throws UserApiBussinesException {
        if (prUser == null) {
            throw new UserApiBussinesException("Error getting user data", HttpStatus.PARTIAL_CONTENT);
        }
        if (prUser.getName() == null || prUser.getName().trim().isEmpty()) {
            throw new UserApiBussinesException("name: required", HttpStatus.PARTIAL_CONTENT);
        }
        if (prUser.getEmail() == null || prUser.getEmail().trim().isEmpty()) {
            throw new UserApiBussinesException("email: required", HttpStatus.PARTIAL_CONTENT);
        } else if (!validReg(prUser.getEmail(), RegExpression.regExpEmail)) {
            throw new UserApiBussinesException("email format: invalid, required format example aaaaaaa@dominio.cl",
                    HttpStatus.BAD_REQUEST);
        }
        if (prUser.getPassword() == null || prUser.getPassword().trim().isEmpty()) {
            throw new UserApiBussinesException("password: required", HttpStatus.PARTIAL_CONTENT);
        } else if (!validReg(prUser.getPassword(), RegExpression.regExpPassword)) {
            throw new UserApiBussinesException("password format: invalid, "
                    + "required minimum 1 uppercase letter, lowercase letters and minimum 2 numbers",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Boolean validReg(final String value, final String expression) {
        Pattern pat = Pattern.compile(expression);
        Matcher mat = pat.matcher(value);
        return mat.find();
    }

    public UserDto updateUser(final UserDto prUserUpdate) throws UserApiBussinesException {
        User user = this.getUserById(prUserUpdate.getId());

        if (prUserUpdate.getEmail() != null && !user.getEmail().equals(prUserUpdate.getEmail())) {
            throw new UserApiBussinesException("cannot update email", HttpStatus.CONFLICT);
        }

        user.setName(prUserUpdate.getName());

        if (prUserUpdate.getPassword() != null) {
            String passNew = passwordEncoder.encode(prUserUpdate.getPassword());
            if (!BCrypt.checkpw(passNew, user.getPassword())) {
                user.setPassword(passNew);
            }
        }
        user.setModified(LocalDateTime.now());
        validUser(user);
        userRepository.save(user);
        return new UserDto(user);
    }

    @Override
    public UserDto desactivateUser(UUID prId) throws UserApiBussinesException {
        User user = this.getUserById(prId);
        user.setIsActive(false);
        user.setModified(LocalDateTime.now());
        validUser(user);
        userRepository.save(user);
        return new UserDto(user);
    }

    @Override
    public UserDto activateUser(UUID prId) throws UserApiBussinesException {
        User user = this.getUserById(prId);
        user.setIsActive(true);
        user.setModified(LocalDateTime.now());
        validUser(user);
        userRepository.save(user);
        return new UserDto(user);
    }

    @Override
    public void saveLoginUser(final UserDto prUserUpdate) throws UserApiBussinesException {
        User user = this.getUserById(prUserUpdate.getId());
        user.setToken(prUserUpdate.getToken());
        user.setLastLogin(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        userRepository.save(user);
    }
}
