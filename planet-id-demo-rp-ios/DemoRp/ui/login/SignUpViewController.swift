//
//  SignUpViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 10.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

class SignUpViewController: UIViewController {
    
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var verifyPasswordTextField: UITextField!
    @IBOutlet weak var validationErrorText: UILabel!
    @IBOutlet weak var createAccountButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        emailTextField.keyboardType = UIKeyboardType.emailAddress
        emailTextField.autocorrectionType = .no
        emailTextField.autocapitalizationType = .none
        emailTextField.addTarget(self, action: #selector(self.textFieldDidChange(_:)), for: UIControl.Event.editingChanged)
        emailTextField.addTarget(self, action: #selector(self.emailTextEditingDidEnd(_:)), for: UIControl.Event.editingDidEndOnExit)
        passwordTextField.addTarget(self, action: #selector(self.textFieldDidChange(_:)), for: UIControl.Event.editingChanged)
        passwordTextField.addTarget(self, action: #selector(self.passwordTextEditingDidEnd(_:)), for: UIControl.Event.editingDidEndOnExit)
        verifyPasswordTextField.addTarget(self, action: #selector(self.textFieldDidChange(_:)), for: UIControl.Event.editingChanged)
        verifyPasswordTextField.addTarget(self, action: #selector(self.verifyPasswordTextEditingDidEnd(_:)), for: UIControl.Event.editingDidEndOnExit)
        
        validationErrorText.text = ""
        createAccountButton.isEnabled = false
    }
    
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return false
    }
    
    @IBAction func createAccountButtonPressed(_ sender: UIButton) {
        createAccount()
    }
    
    @objc func textFieldDidChange(_ text: UITextField) {
        setError(emailTextField, nil)
        setError(passwordTextField, nil)
        setError(verifyPasswordTextField, nil)
        
        if !isEmailValid(emailTextField.text!) {
            setError(emailTextField, "Email invalid.")
        } else if !isPasswordValid(passwordTextField.text!) {
            setError(passwordTextField, "Password invalid.")
        } else if !isVerifyPasswordValid(passwordTextField.text!, verifyPasswordTextField.text!) {
            setError(verifyPasswordTextField, "Verify password invalid.")
        }
    }
    
    @objc func emailTextEditingDidEnd(_ text: UITextField) {
        passwordTextField.becomeFirstResponder()
    }
    
    @objc func passwordTextEditingDidEnd(_ text: UITextField) {
        verifyPasswordTextField.becomeFirstResponder()
    }
    
    @objc func verifyPasswordTextEditingDidEnd(_ text: UITextField) {
        if (createAccountButton.isEnabled) {
            createAccount()
        }
    }
    
    private func createAccount() {
        let spinner = showSpinner(view)
        
        ApiClient.shared.createAccount(email: emailTextField.text!, password: passwordTextField.text!, { result, error in
            self.removeSpinner(spinner)
            
            if (error != nil) {
                return self.displayError(error!, { _ in
                    self.emailTextField.text = ""
                    self.passwordTextField.text = ""
                    self.verifyPasswordTextField.text = ""
                    self.createAccountButton.isEnabled = false
                })
            }

            self.navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SignUpSuccess"), animated: true)
        })
    }
    
    private func isEmailValid(_ email: String) -> Bool {
        let predicate = NSPredicate(format: "SELF MATCHES %@", ".+@.+")
        return predicate.evaluate(with: email)
    }
    
    private func isPasswordValid(_ password: String) -> Bool {
        return password.count >= 5
    }
    
    private func isVerifyPasswordValid(_ password: String, _ verifyPassword: String) -> Bool {
        return password == verifyPassword
    }
    
    private func setError(_ textField: UITextField, _ text: String?) {
        if text == nil {
            validationErrorText.text = ""
            textField.layer.borderWidth = 0
            createAccountButton.isEnabled = true
        } else {
            validationErrorText.text = text
            textField.layer.borderWidth = 1.0
            textField.layer.borderColor = UIColor.red.cgColor
            createAccountButton.isEnabled = false
        }
    }
}
