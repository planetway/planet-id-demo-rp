//
//  UserRepository.swift
//  DemoRp
//
//  Created by Toomas Laigna on 12.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import Foundation
import UIKit
import PlanetIDRP

class UserRepository {
    
    static let shared = UserRepository()
    
    private (set) var userInfo: UserInfo?
    private (set) var dataBankPerson: Dictionary<String, String>?
    private (set) var lraPerson: Person?
    
    func login(_ email: String, _ password: String, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        ApiClient.shared.login(email: email, password: password, { userInfo, error in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.userInfo = userInfo
            onComplete(userInfo, nil)
        })
    }
    
    func loginWithPlanetId(_ onComplete: @escaping (Void?, Any?) -> Void) {
        ApiClient.shared.loginWithPlanetId({ authRequest, error in
            guard var authRequest = authRequest, error == nil else {
                return onComplete(nil, error)
            }
            
            authRequest.return_app_uri = "fudosandemorp://login-with-planet-id"
            let url = authRequest.url()
            UIApplication.shared.open(url) { success in
                if !success {
                    return onComplete(nil, PlanetIdActionInvocationError())
                }
            }
        })
    }
    
    func loginWithPlanetIdCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        ApiClient.shared.loginWithPlanetIdCallback(authResponse, { (userInfo, error) in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.userInfo = userInfo!
            onComplete(userInfo, nil)
        })
    }
    
    func linkWithPlanetId(_ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        ApiClient.shared.linkWithPlanetId(onComplete: { authRequest, error in
            guard var authRequest = authRequest, error == nil else {
                return onComplete(nil, error)
            }
            
            authRequest.return_app_uri = "fudosandemorp://link-planet-id"
            let url = authRequest.url()
            UIApplication.shared.open(url) { success in
                if !success {
                    return onComplete(nil, PlanetIdActionInvocationError())
                }
            }
        })
    }
    
    func linkWithPlanetIdCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        ApiClient.shared.linkWithPlanetIdCallback(authResponse, { (userInfo, error) in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.userInfo = userInfo!
            onComplete(userInfo, nil)
        })
    }
    
    func unlinkWithPlanetId(_ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        ApiClient.shared.unlinkWithPlanetId({ (userInfo, error) in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.userInfo = userInfo!
            onComplete(userInfo, nil)
        })
    }
    
    func dataBankPerson(dataBank: String, _ onComplete: @escaping (Dictionary<String, String>?, Any?) -> Void) {
        ApiClient.shared.dataBankPerson(dataBank: dataBank) { (person, error) in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.dataBankPerson = person!
            onComplete(person, nil)
        }
    }
    
    func personalInfoFromLra(_ onComplete: @escaping (Person?, Any?) -> Void) {
        ApiClient.shared.lraPerson() { lraPerson, error in
            guard error == nil else {
                return onComplete(nil, error)
            }
            
            self.lraPerson = lraPerson!
            onComplete(lraPerson, nil)
        }
    }
}
