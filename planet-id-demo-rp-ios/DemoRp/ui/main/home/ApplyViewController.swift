//
//  ApplyViewModel.swift
//  DemoRp
//
//  Created by Toomas Laigna on 12.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit
import PlanetIDRP

class ApplyViewController: UIViewController {
    
    var callbackUrl: URLComponents?
    
    private var selectedDataBank: String? = nil
    @IBOutlet weak var dataBanksStackView: UIStackView!
    @IBOutlet weak var retrieveButton: UIButton!
    
    override func viewDidLoad() {
        retrieveButton.isEnabled = false
        
        if callbackUrl != nil {
            handleDataBankConsentCallback(callbackUrl!)
        }
    }
    
    override func canPerformUnwindSegueAction(_ action: Selector, from fromViewController: UIViewController, sender: Any?) -> Bool {
        return false
    }
    
    @IBAction func dataBankButtonPressed(_ sender: UIButton) {
        selectDataBank(dataBank: sender.accessibilityIdentifier!)
    }
    
    private func selectDataBank(dataBank: String) {
        selectedDataBank = dataBank
        
        for uiView in dataBanksStackView.arrangedSubviews {
            let itemStackView = uiView as! UIStackView
            let button = itemStackView.arrangedSubviews[1] as! UIButton
            
            if button.accessibilityIdentifier == dataBank {
                for innerUiView in itemStackView.arrangedSubviews {
                    innerUiView.layer.backgroundColor = UIColor.lightGray.cgColor
                }
            } else {
                for innerUiView in itemStackView.arrangedSubviews {
                    innerUiView.layer.backgroundColor = nil
                }
            }
        }
        
        retrieveButton.isEnabled = true
    }
    
    @IBAction func retrieveButtonPressed(_ sender: UIButton) {
        let spinner = showSpinner(view)
        
        UserRepository.shared.dataBankPerson(dataBank: selectedDataBank!) { (person, error) in
            // 403 Forbidden means no consent in this case.
            if error as? Int == 403 {
                ApiClient.shared.dataBankConsent(dataBank: self.selectedDataBank!, language: "en") { (authRequest, error) in
                    self.removeSpinner(spinner)
                    
                    if error != nil {
                        return self.displayError(error!)
                    }
                    
                    self.invokeConsentAction(authRequest!)
                }
                
                return
            }
            
            self.removeSpinner(spinner)
            
            self.handleDataBankPersonSuccess(person!)
        }
    }
    
    private func handleDataBankPersonSuccess(_ person: Dictionary<String, String>) {
        print("getDataBankPerson -> \(person)")
        
        self.navigationController?.pushViewController(UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "Sign"), animated: true)
    }
    
    private func invokeConsentAction(_ authRequest: AuthRequest) {
        var authRequest = authRequest
        authRequest.return_app_uri = "fudosandemorp://data-bank-consent?data-bank=\(selectedDataBank!)"
        let url = authRequest.url()
        UIApplication.shared.open(url) { success in
            if !success {
                return self.displayError(PlanetIdActionInvocationError())
            }
        }
    }
    
    private func handleDataBankConsentCallback(_ urlComponents: URLComponents) {
        if PlanetID.isRejected(urlComponents) {
            return self.showAlert("Rejected", "Cancelled by user.")
        }
        
        guard let authResponse = PlanetID.getAuthResponse(urlComponents) else {
            return self.showAlert("Error", PlanetID.getError(urlComponents) ?? "")
        }
        
        guard let dataBank = urlComponents.queryItems?.first(
            where: { (item) -> Bool in item.name == "data-bank" })?.value else {
            return self.showAlert("Error", "Missing data bank selection.")
        }
        
        selectDataBank(dataBank: dataBank)
        
        let spinner = showSpinner(view)
        
        ApiClient.shared.consentCallback(authResponse) { (_, error) in
            if error != nil {
                self.removeSpinner(spinner)
                
                return self.displayError(error!)
            }
            
            UserRepository.shared.dataBankPerson(dataBank: dataBank) { (person, error) in
                self.removeSpinner(spinner)
                
                if error != nil {
                    return self.displayError(error!)
                }
                
                self.handleDataBankPersonSuccess(person!)
            }
        }
    }
}
