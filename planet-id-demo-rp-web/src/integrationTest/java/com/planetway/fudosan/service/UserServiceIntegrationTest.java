package com.planetway.fudosan.service;

import com.planetway.fudosan.domain.SignUpForm;
import com.planetway.fudosan.domain.UserInfo;
import com.planetway.fudosan.domain.ValidationResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("integration-test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void createAccount() {
        SignUpForm request = SignUpForm.builder().email("test2@test").password("plainpass").confirmPassword("plainpass").build();
        ValidationResult account = userService.create(request);

        assertThat(account.hasErrors()).isFalse();

        // tear down
        UserInfo user = userService.loadUserByUsername("test2@test");
        if (user != null) {
            userService.delete(user);
        }
    }
}
