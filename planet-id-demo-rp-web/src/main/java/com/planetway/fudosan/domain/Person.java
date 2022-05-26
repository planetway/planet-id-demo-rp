package com.planetway.fudosan.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String firstNameKatakana;
    private String lastNameKatakana;
    private String firstNameKanji;
    private String lastNameKanji;
    private String firstNameRomaji;
    private String lastNameRomaji;
    private String dateOfBirth;
    private List<Address> address;
}
