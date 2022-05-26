//
//  SignViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 12.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit
import PlanetIDRP

class SignViewController: UIViewController {

    var callbackUrl: URLComponents?
    
    @IBOutlet weak var textName: UILabel!
    @IBOutlet weak var textDateOfBirth: UILabel!
    @IBOutlet weak var textAddress: UILabel!
    @IBOutlet weak var textPhone: UILabel!
    @IBOutlet weak var textBankAccount: UILabel!
    @IBOutlet weak var textEmployer: UILabel!
    
    override func viewDidLoad() {
        let person = UserRepository.shared.dataBankPerson!
        
        textName.text = person["name_en"]
        textDateOfBirth.text = person["dob_en"]
        textAddress.text = person["address_en"]
        textPhone.text = person["phone"]
        textBankAccount.text = person["bank_account"]
        textEmployer.text = person["employer_en"]
        
        if callbackUrl != nil {
            handleSignCallback(callbackUrl!)
        }
    }
    
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return false
    }
    
    @IBAction func signButtonPressed(_ sender: UIButton) {
        let spinner = showSpinner(view)
        
        ApiClient.shared.sign(document: "foo") { (authRequest, error) in
            self.removeSpinner(spinner)
            
            if error != nil {
                return self.displayError(error!)
            }
            
            self.invokeSignAction(authRequest: authRequest!)
        }
    }
    
    private func invokeSignAction(authRequest: AuthRequest) {
        var authRequest = authRequest
        authRequest.return_app_uri = "fudosandemorp://document-sign"
        let url = authRequest.url()
        UIApplication.shared.open(url) { success in
            if !success {
                return self.displayError(PlanetIdActionInvocationError())
            }
        }
    }
    
    private func handleSignCallback(_ urlComponents: URLComponents) {
        if PlanetID.isRejected(urlComponents) {
            return self.showAlert("Rejected", "Cancelled by user.")
        }
        
        guard let authResponse = PlanetID.getAuthResponse(urlComponents) else {
            return self.showAlert("Error", PlanetID.getError(urlComponents) ?? "")
        }
        
        let spinner = showSpinner(view)
        
        ApiClient.shared.signCallback(authResponse, { (_, error) in
            self.removeSpinner(spinner)
            
            if error != nil {
                return self.displayError(error!)
            }
            
            self.navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SignSuccess"), animated: true)
        })
    }
}
