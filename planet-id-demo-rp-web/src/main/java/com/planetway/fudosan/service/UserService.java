package com.planetway.fudosan.service;

import com.planetway.fudosan.domain.*;
import com.planetway.fudosan.exception.PlanetIdNotLinkedException;
import com.planetway.fudosan.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(\\S+)@(\\S+)$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ValidationResult create(SignUpForm request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();
        String confirmedPassword = request.getConfirmPassword();

        ValidationResult validationResult = validateRequest(email, password, confirmedPassword);
        if (validationResult.hasErrors()) {
            return validationResult;
        }

        log.info("Creating new account for user: {}", email);

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(userEntity);

        return validationResult;
    }

    @Transactional
    public UserInfo linkPlanetId(UserInfo user, String planetId) {

        if (user.getPlanetId() != null) {
            log.warn("PlanetID linking failed. The user (id: {}) is already linked to another PlanetID ({}).", user.getId(), user.getPlanetId());
            throw new RuntimeException("PlanetID already linked.");
        }

        UserEntity userEntity = userRepository.findByPlanetId(planetId);
        if (userEntity != null) {
            log.warn("PlanetID linking failed. The planetID ({}) is already linked to another user (id: {}).", planetId, userEntity.getId());

            throw new RuntimeException("PlanetID already linked.");
        }

        userEntity = userRepository.findById(user.getId()).orElseThrow();
        userEntity.setPlanetId(planetId);
        userRepository.save(userEntity);

        user.setPlanetId(planetId);
        return user;
    }

    @Transactional
    public UserInfo unlinkPlanetId(UserInfo user) {
        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow();
        userEntity.setPlanetId(null);
        userRepository.save(userEntity);
        user.setPlanetId(null);
        return user;
    }

    @Transactional
    public void delete(UserInfo user) {
        userRepository.deleteById(user.getId());
    }

    @Override
    public UserInfo loadUserByUsername(String username) {
        username = username.toLowerCase();
        UserEntity user = userRepository.getByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return new UserInfo(user.getId(), user.getEmail(), user.getPassword(), emptyList(), user.getPlanetId());
    }

    public void loginUserWithPlanetId(String planetId) {
        log.info("Logging in user with PlanetID: {}", planetId);

        UserEntity userEntity = userRepository.findByPlanetId(planetId);
        if (userEntity == null) {
            throw new PlanetIdNotLinkedException("PlanetID " + planetId + " not linked to account ");
        }

        UserInfo principal = new UserInfo(userEntity.getId(), userEntity.getEmail(), "", Collections.emptyList(), planetId);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private ValidationResult validateRequest(String email, String password, String confirmedPassword) {
        ValidationResult response = new ValidationResult();
        if (!isValidEmail(email)) {
            response.getErrors().add(RelyingPartyError.INVALID_EMAIL);
        }
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmedPassword)) {
            response.getErrors().add(RelyingPartyError.PASSWORD_REQUIRED);
        }
        if (!password.equals(confirmedPassword)) {
            response.getErrors().add(RelyingPartyError.PASSWORDS_MISMATCH);
        }
        if (userRepository.getByEmail(email) != null) {
            response.getErrors().add(RelyingPartyError.EMAIL_ALREADY_EXISTS);
        }
        return response;
    }

    private boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email.trim().toLowerCase()).matches();
    }
}
