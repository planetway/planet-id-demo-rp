//
//  AuthResponse.swift
//  PlanetIDRP
//
//  Created by Toomas Laigna on 13.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

/**
AuthenticationResponse parameters in OpenID Connect 1.0 specification.
 */
public struct AuthResponse : Codable {
    var code: String
    var state: String?
    var callback: String
}
