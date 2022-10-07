//
//  AccountSettingsViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 12.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit
import PlanetIDRP

class AccountSettingsViewController: UIViewController {
    
    var callbackUrl: URLComponents?
    
    @IBOutlet weak var linkWithPlanetIdText: UIButton!
    @IBOutlet weak var dataFromLraButton: UIButton!
    @IBOutlet weak var signedDocumentsButton: UIButton!
    
    override func viewDidLoad() {
        self.dataFromLraButton.setTitleColor(UIColor.lightGray, for: .disabled)
        
        if callbackUrl != nil {
            handleLinkWithPlanetIdCallback(callbackUrl!)
        }
    }
    
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return true
    }
    
    @IBAction func unwindToViewController(segue:UIStoryboardSegue) {
    }
    
    override func viewWillAppear(_ animated: Bool) {
        let planetIdIsLinked = UserRepository.shared.userInfo?.planetId != nil
        if planetIdIsLinked {
            self.linkWithPlanetIdText.setTitle("Unlink with Planet ID", for: .normal)
            self.dataFromLraButton.isEnabled = true
        } else {
            self.linkWithPlanetIdText.setTitle("Link with Planet ID", for: .normal)
            self.dataFromLraButton.isEnabled = false
        }
    }
    
    @IBAction func linkWithPlanetIdButtonPressed(_ sender: UIButton) {
        if UserRepository.shared.userInfo?.planetId == nil {
            UserRepository.shared.linkWithPlanetId({ userInfo, error in
                if error != nil {
                    return self.displayError(error!)
                }
                
                // Success handled by callback.
            })
        } else {
            unlink()
        }
    }
    
    @IBAction func deleteAccountButtonPressed(_ sender: UIButton) {
        let alert = UIAlertController(title: "Delete account", message: "Are you sure?", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in
            ApiClient.shared.deleteAccount({ result, error in
                if error != nil {
                    return self.displayError(error!)
                }
                
                RpApp.global.switchToLogin()
            })
        }))
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "Cancel"), style: .cancel, handler: { _ in }))
        
        self.present(alert, animated: true, completion: nil)
    }
    
    private func unlink() {
        let alert = UIAlertController(title: "Unlink account", message: "Are you sure you want to unlink your Planet ID?", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in
            let spinner = self.showSpinner(self.view)
            
            UserRepository.shared.unlinkWithPlanetId({ userInfo, error in
                self.removeSpinner(spinner)
                
                if error != nil {
                    return self.displayError(error!)
                }
                
                if userInfo?.planetId == nil {
                    self.linkWithPlanetIdText.setTitle("Link with Planet ID", for: .normal)
                    self.dataFromLraButton.isEnabled = false
                }
            })
        }))
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "Cancel"), style: .cancel, handler: { _ in }))
        
        self.present(alert, animated: true, completion: nil)
    }
    
    private func handleLinkWithPlanetIdCallback(_ urlComponents: URLComponents) {
        if PlanetID.isRejected(urlComponents) {
            return self.showAlert("Rejected", "Cancelled by user.")
        }
        
        guard let authResponse = PlanetID.getAuthResponse(urlComponents) else {
            return self.showAlert("Error", PlanetID.getError(urlComponents) ?? "")
        }
        
        let spinner = showSpinner(view)
        
        UserRepository.shared.linkWithPlanetIdCallback(authResponse) { (userInfo, error) in
            self.removeSpinner(spinner)
            
            if error != nil {
                self.navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LinkError"), animated: true)
                
                return
            }
            
            self.navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LinkSuccess"), animated: true)
        }
    }
}
