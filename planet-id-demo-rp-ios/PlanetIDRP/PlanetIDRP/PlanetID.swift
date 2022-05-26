//
//  PlanetID.swift
//  PlanetIDRP
//
//  Created by Toomas Laigna on 13.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

open class PlanetID {
    public static func getAuthResponse(_ urlComponents: URLComponents) -> AuthResponse? {
        guard let query = urlComponents.queryItems else { return nil }
        
        guard let code = (query.first { (item) -> Bool in item.name == "code" }?.value) else { return nil }
        let state = query.first { (item) -> Bool in item.name == "state" }?.value
        guard let callback = (query.first { (item) -> Bool in item.name == "callback" }?.value) else { return nil }
        
        return AuthResponse(
            code: code,
            state: state,
            callback: callback
        )
    }
    
    public static func getError(_ urlComponents: URLComponents) -> String? {
        guard let query = urlComponents.queryItems else { return nil }
        let error = query.first { (item) -> Bool in item.name == "error" }
        return error?.value
    }

    public static func isRejected(_ urlComponents: URLComponents) -> Bool {
        guard let query = urlComponents.queryItems else { return false }
        let rejected = query.first { (item) -> Bool in item.name == "rejected" }
        return rejected != nil
    }
}
