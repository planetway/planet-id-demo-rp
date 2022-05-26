//
//  UIExtensions.swift
//  DemoRp
//
//  Created by Toomas Laigna on 11.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

extension UIViewController {
    
    func showAlert(_ title: String, _ text: String, _ handler: @escaping (UIAlertAction) -> Void = { _ in }) {
        let alert = UIAlertController(title: title, message: text, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: handler))
        
        self.present(alert, animated: true, completion: nil)
    }
    
    func showSpinner(_ onView: UIView) -> UIView {
        let spinnerView = UIView.init(frame: onView.bounds)
        spinnerView.backgroundColor = UIColor.init(red: 0.5, green: 0.5, blue: 0.5, alpha: 0.5)
        let ai = UIActivityIndicatorView.init(style: .whiteLarge)
        ai.startAnimating()
        ai.center = spinnerView.center
        
        DispatchQueue.main.async {
            spinnerView.addSubview(ai)
            onView.addSubview(spinnerView)
        }
        
        return spinnerView
    }
    
    func removeSpinner(_ spinner: UIView) {
        DispatchQueue.main.async {
            spinner.removeFromSuperview()
        }
    }
    
    // TODO: Localization.
    func displayError(_ error: Any, _ handler: @escaping (UIAlertAction) -> Void = { _ in }) {
        let errorMessage: String
        if error is PlanetIdActionInvocationError {
            errorMessage = "Error! We can not find your Planet ID. If you don't have one, then you can visit App Store or Google Play to download it. If you don't have an account, please sing up first."
        } else if let apiError = error as? ApiError {
            if apiError.errorCode == "PLANETID_NOT_LINKED" {
                errorMessage = "Your Planet ID is not linked to any account. Please log in with ID and Password and link your Planet ID in your account setting Page.If you don't have an account, please sing up first."
            } else {
                errorMessage = "Something went wrong: \(apiError.errorCode) \(apiError.message)"
            }
        } else if let networkError = error as? NetworkError {
            errorMessage = networkError.message
        } else {
            errorMessage = "Something went wrong: \(error)"
        }
        
        showAlert("Error", errorMessage, handler)
    }
}
