//
//  LoginViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 10.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit
import PlanetIDRP

class LoginViewController : UIViewController {

    var callbackUrl: URLComponents?
    
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginWithPlanetIdButton: UIButton!
    @IBOutlet weak var loginButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
                
        loginButton.isEnabled = false
        
        emailTextField.keyboardType = UIKeyboardType.emailAddress
        emailTextField.autocorrectionType = .no
        emailTextField.autocapitalizationType = .none
        emailTextField.addTarget(self, action: #selector(self.textFieldDidChange(_:)), for: UIControl.Event.editingChanged)
        emailTextField.addTarget(self, action: #selector(self.emailTextEditingDidEnd(_:)), for: UIControl.Event.editingDidEndOnExit)
        passwordTextField.addTarget(self, action: #selector(self.textFieldDidChange(_:)), for: UIControl.Event.editingChanged)
        passwordTextField.addTarget(self, action: #selector(self.passwordTextEditingDidEnd(_:)), for: UIControl.Event.editingDidEndOnExit)
        
        if callbackUrl != nil {
            handleLoginWithPlanetIdCallback(callbackUrl!)
        }
    }
    
    // Unwind will stop here.
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return true
    }
    
    // Needed for unwind to work.
    @IBAction func unwindToViewController(segue:UIStoryboardSegue) {
    }
    
    @IBAction func loginWithPlanetIdButtonPressed(_ sender: UIButton) {
        UserRepository.shared.loginWithPlanetId({_, error in
            if error != nil {
                return self.displayError(error!, { _ in
                    self.emailTextField.text = ""
                    self.passwordTextField.text = ""
                    self.loginButton.isEnabled = false
                })
            }
            
            // Continuation by app delegate.
        })
    }
    
    @IBAction func loginButtonPressed(_ sender: UIButton) {
        let spinner = showSpinner(view)
        
        UserRepository.shared.login(emailTextField.text!, passwordTextField.text!, { result, error in
            self.removeSpinner(spinner)
            
            if (error != nil) {
                return self.displayError(error!, { _ in
                    self.emailTextField.text = ""
                    self.passwordTextField.text = ""
                    self.loginButton.isEnabled = false
                })
            }
            
            RpApp.global.switchToMain()
        })
    }
    
    @objc func textFieldDidChange(_ text: UITextField) {
        if emailTextField.text?.isEmpty == false && passwordTextField.text?.isEmpty == false {
            loginButton.isEnabled = true
        } else {
            loginButton.isEnabled = false
        }
    }
    
    @objc func emailTextEditingDidEnd(_ text: UITextField) {
        passwordTextField.becomeFirstResponder()
    }
    
    @objc func passwordTextEditingDidEnd(_ text: UITextField) {
        if loginButton.isEnabled {
            loginButtonPressed(loginButton)
        }
    }
    
    private func handleLoginWithPlanetIdCallback(_ urlComponents: URLComponents) {
        if PlanetID.isRejected(urlComponents) {
            return self.showAlert("Rejected", "Cancelled by user.")
        }
        
        guard let authResponse = PlanetID.getAuthResponse(urlComponents) else {
            return self.showAlert("Error", PlanetID.getError(urlComponents) ?? "")
        }
        
        let spinner = showSpinner(view)
        
        UserRepository.shared.loginWithPlanetIdCallback(authResponse, { (userInfo, error) in
            self.removeSpinner(spinner)
            
            if error != nil {
                return self.displayError(error!)
            }
            
            RpApp.global.switchToMain()
        })
    }
}
