package com.planetway.fudosan.web.rest.html;

import com.planetway.fudosan.domain.Address;
import com.planetway.fudosan.domain.Person;
import com.planetway.fudosan.service.RaService;
import com.planetway.fudosan.web.WithMockRpUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LraPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RaService raService;

    @WithMockRpUser
    @Test
    public void getVerifiedPersonPage() throws Exception {
        Person person = testPerson();
        when(raService.getPerson("123456789000")).thenReturn(person);
        this.mockMvc.perform(get("/lra/person"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test@test")));
    }

    private Person testPerson() {
        Address address = new Address();
        address.setCity("Tallinn");

        Person person = new Person();
        person.setAddress(Collections.singletonList(address));
        person.setFirstNameKanji("kanji");

        return person;
    }

}
