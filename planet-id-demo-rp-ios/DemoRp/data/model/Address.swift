//
//  Address.swift
//  DemoRp
//
//  Created by Toomas Laigna on 19.03.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

struct Address : Codable {
    var postalCode: String?
    var apartmentNo: String?
    var buildingName: String?
    var buildingNo: String?
    var street: String?
    var townOrVillage: String?
    var city: String?
    var prefecture: String?
    
    func toString() -> String {
        return "\(postalCode ?? ""), \(apartmentNo ?? "") \(buildingName ?? "") \(buildingNo ?? ""), \(street ?? ""), \(townOrVillage ?? ""), \(city ?? ""), \(prefecture ?? "")"
    }
}
