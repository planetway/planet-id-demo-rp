//
//  SignedDocumentsViewController.swift
//  DemoRp
//
//  Created by Margus Räim on 16.09.2022.
//  Copyright © 2022 Toomas Laigna. All rights reserved.
//

import UIKit
import PlanetIDRP

class SignedDocumentCell: UITableViewCell {
    @IBOutlet weak var uuidLabel: UILabel!
    @IBOutlet weak var typeLabel: UILabel!
    @IBOutlet weak var revokeButton: UIButton!
    
    @IBAction func onRevoke(_ sender: Any) {
        ApiClient.shared.getConsentRevokeRequest(consentUuid: uuidLabel.text!) { (authRequest, error) in
            if (error == nil && authRequest != nil) {
                var authRequest = authRequest!
                authRequest.return_app_uri = "fudosandemorp://consent-revoke"
                let url = authRequest.url()
                UIApplication.shared.open(url) { success in
                    if !success {
                        print("Error opening PlanetId app")
                    }
                }
            } else {
                print("Error revoking consent: \(String(describing: error))")
            }
        }
    }
}

class SignedDocumentsViewController: UIViewController {
    
    @IBOutlet weak var documentsTable: UITableView!
    
    var signedDocuments: [SignedDocument] = []

    var callbackUrl: URLComponents?
    var spinner: UIView? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        documentsTable.dataSource = self
//        documentsTable.delegate = self
        self.spinner = showSpinner(view)
        if callbackUrl != nil {
            handleCallback(callbackUrl!)
            callbackUrl = nil
        } else {
            loadSignedDocuments()
        }
    }
    
    private func loadSignedDocuments() {
        UserRepository.shared.getSignedDocuments { result, error in
            if (error != nil) {
                // TODO: handle error
                if (self.spinner != nil) {
                    self.removeSpinner(self.spinner!)
                }
                return
            }
            DispatchQueue.main.async {
                self.signedDocuments = result!
                self.documentsTable.reloadData()
                self.removeSpinner(self.spinner!)
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
        
        ApiClient.shared.revokeConsentCallback(authResponse, { (_, error) in
            if error != nil {
                if (self.spinner != nil) {
                    self.removeSpinner(self.spinner!)
                }
                return self.displayError(error!)
            }
            self.loadSignedDocuments()
            
        })
    }

}

extension SignedDocumentsViewController : UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return signedDocuments.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DocumentCell", for: indexPath) as! SignedDocumentCell
        let doc = signedDocuments[indexPath.row]
        let isConsent = doc.signatureType == "CONSENT"
        cell.uuidLabel?.text = isConsent ? doc.consentUuid : doc.uuid
        
        if (isConsent) {
            if (doc.revokeDocumentUuid == nil ) {
                cell.revokeButton?.isHidden = false
                cell.typeLabel?.text = "Consent (valid)"
            } else {
                cell.typeLabel?.text = "Consent (revoked)"
            }
        }
        return cell
    }
}

//extension SignedDocumentsViewController: UITableViewDelegate {
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        tableView.deselectRow(at: indexPath, animated: true)
//        print(signedDocuments[indexPath.row])
//    }
//}
