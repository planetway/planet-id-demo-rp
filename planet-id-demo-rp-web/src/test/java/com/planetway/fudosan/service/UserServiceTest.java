package com.planetway.fudosan.service;

import com.planetway.fudosan.domain.RelyingPartyError;
import com.planetway.fudosan.domain.UserEntity;
import com.planetway.fudosan.domain.SignUpForm;
import com.planetway.fudosan.domain.ValidationResult;
import com.planetway.fudosan.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    @Test
    public void create() {
        UserService userService = new UserService(userRepository, passwordEncoder);
        SignUpForm request = SignUpForm.builder().email("test2@test").password("plainpass").confirmPassword("plainpass").build();
        when(passwordEncoder.encode("plainpass")).thenReturn("encryptPass");

        userService.create(request);

        ArgumentCaptor<UserEntity> userTableArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userTableArgumentCaptor.capture());

        UserEntity userEntity = userTableArgumentCaptor.getValue();
        assertThat(userEntity.getEmail()).isEqualTo("test2@test");
        assertThat(userEntity.getPassword()).isEqualTo("encryptPass");
    }

    @Test
    public void create_emailAlreadyExists() {
        UserService userService = new UserService(userRepository, passwordEncoder);
        String email = "alreadyexist@test";
        String password = "plainpass";
        SignUpForm request = SignUpForm.builder().email(email).password(password).confirmPassword(password).build();
        when(userRepository.getByEmail(email)).thenReturn(mock(UserEntity.class));

        ValidationResult validationResult = userService.create(request);
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).contains(RelyingPartyError.EMAIL_ALREADY_EXISTS);
    }
}
