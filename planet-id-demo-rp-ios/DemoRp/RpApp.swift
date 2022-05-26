//
//  RpApp.swift
//  DemoRp
//
//  Created by Toomas Laigna on 11.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

class RpApp: UIApplication {

    static let global = UIApplication.shared as! RpApp
    
    func switchToMain() {
        self.delegate?.window??.rootViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "Main")
    }
    
    func switchToLogin() {
        self.delegate?.window??.rootViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LoginNavController")
    }
}
