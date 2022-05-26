//
//  SecondViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 07.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

class AccountViewController: UIViewController {

    @IBAction func logOutButtonPressed(_ sender: UIButton) {
        ApiClient.shared.logOut({ result, error in
            RpApp.global.switchToLogin()
        })
    }
}
