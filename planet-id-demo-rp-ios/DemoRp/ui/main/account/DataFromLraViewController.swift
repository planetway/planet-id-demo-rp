//
//  DataFromLRAViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 19.03.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit
import PlanetIDRP

class DataFromLraViewController: UIViewController {
    
    var callbackUrl: URLComponents?
    
    override func viewDidLoad() {
        if callbackUrl != nil {
            handleCallback(callbackUrl!)
        }
    }

    @IBAction func okButtonPressed(_ sender: UIButton) {
        let spinner = showSpinner(view)

        UserRepository.shared.personalInfoFromLra() { person, error in
            self.removeSpinner(spinner)

            if error != nil {
                if error as? Int == 403 {
                    self.handleNoConsent()
                } else {
                    return self.displayError(error!)
                }

                return
            }

            self.navigateToSuccessView()
        }
    }
    
    private func navigateToSuccessView() {
        navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "DataFromLraSuccess"), animated: true)
    }
    
    private func handleNoConsent() {
        let spinner = showSpinner(view)
        
        ApiClient.shared.lraConsent() { authRequest, error in
            self.removeSpinner(spinner)
            
            guard var authRequest = authRequest, error == nil else {
                return self.displayError(error!)
            }
            
            authRequest.return_app_uri = "fudosandemorp://lra-consent"
            let url = authRequest.url()
            UIApplication.shared.open(url) { success in
                if !success {
                    return self.displayError(PlanetIdActionInvocationError())
                }
            }
        }
    }
    
    private func handleCallback(_ urlComponents: URLComponents) {
        if PlanetID.isRejected(urlComponents) {
            return self.showAlert("Rejected", "Cancelled by user.")
        }
        
        guard let authResponse = PlanetID.getAuthResponse(urlComponents) else {
            return self.showAlert("Error", PlanetID.getError(urlComponents) ?? "")
        }
        
        let spinner = showSpinner(view)
        
        ApiClient.shared.consentCallback(authResponse) { (_, error) in
            guard error == nil else {
                self.removeSpinner(spinner)
                
                return self.displayError(error!)
            }
            
            UserRepository.shared.personalInfoFromLra() { person, error in
                self.removeSpinner(spinner)
                
                guard error == nil else {
                    return self.displayError(error!)
                }
                
                self.navigateToSuccessView()
            }
        }
    }
}
