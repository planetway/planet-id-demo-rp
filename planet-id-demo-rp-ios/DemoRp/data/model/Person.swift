//
//  Person.swift
//  DemoRp
//
//  Created by Toomas Laigna on 19.03.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

struct Person : Codable {
    var firstNameKatakana: String?
    var lastNameKatakana: String?
    var firstNameKanji: String?
    var lastNameKanji: String?
    var firstNameRomaji: String?
    var lastNameRomaji: String?
    var dateOfBirth: String?
    var address: [Address]
}
