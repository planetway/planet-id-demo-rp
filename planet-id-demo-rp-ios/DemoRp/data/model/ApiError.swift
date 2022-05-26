//
//  ApiError.swift
//  DemoRp
//
//  Created by Toomas Laigna on 17.03.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

struct ApiError : Codable {
    var errorCode: String
    var message: String
}
