//
//  FirstViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 07.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

class HomeViewController: UIViewController {
    
    @IBOutlet weak var applyButton: UIButton!
    
    override func viewWillAppear(_ animated: Bool) {
        applyButton.isEnabled = UserRepository.shared.userInfo?.planetId != nil
    }
    
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return true
    }
    
    @IBAction func unwindToViewController(segue: UIStoryboardSegue) {
    }
}
