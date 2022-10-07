
//
//  AppDelegate.swift
//  DemoRp
//
//  Created by Toomas Laigna on 07.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

//@UIApplicationMain
class RpAppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.makeKeyAndVisible()
        
        RpApp.global.switchToLogin()
        
        return true
    }
    
    func application(_ application: UIApplication,
                     open url: URL,
                     options: [UIApplication.OpenURLOptionsKey : Any] = [:] ) -> Bool {
        print("Open url: \(url)")
        
        let urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: true)
        let action = urlComponents?.host
        if action == "login-with-planet-id" {
            handleOpenLoginWithPlanetId(urlComponents!)
        } else if action == "link-planet-id" {
            handleLinkPlanetId(urlComponents!)
        } else if action == "data-bank-consent" {
            handleDataBankConsent(urlComponents!)
        } else if action == "consent-revoke" {
            handleConsentRevoke(urlComponents!)
        } else if action == "document-sign" {
            handleDocumentSign(urlComponents!)
        } else if action == "lra-consent" {
            handleLraConsent(urlComponents!)
        }
        
        return true
    }
    
    private func handleOpenLoginWithPlanetId(_ urlComponents: URLComponents) {
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let navigationController: UINavigationController = storyBoard.instantiateViewController(withIdentifier: "LoginNavController") as! UINavigationController
        let loginViewController = navigationController.topViewController as! LoginViewController
        
        loginViewController.callbackUrl = urlComponents
        
        self.window?.rootViewController = navigationController
    }
    
    private func handleLinkPlanetId(_ urlComponents: URLComponents) {
        let accountTabIndex = 1
        
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let mainController: UITabBarController = storyBoard.instantiateViewController(withIdentifier: "Main") as! UITabBarController
        let navigationController = mainController.viewControllers![accountTabIndex] as! UINavigationController
        let accountSettingsViewController = storyBoard.instantiateViewController(withIdentifier: "AccountSettings") as! AccountSettingsViewController
        
        navigationController.pushViewController(accountSettingsViewController, animated: false)
        
        mainController.selectedIndex = accountTabIndex
        accountSettingsViewController.callbackUrl = urlComponents
        
        self.window?.rootViewController = mainController
    }
    
    private func handleDataBankConsent(_ urlComponents: URLComponents) {
        let accountTabIndex = 0
        
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let mainController: UITabBarController = storyBoard.instantiateViewController(withIdentifier: "Main") as! UITabBarController
        let navigationController = mainController.viewControllers![accountTabIndex] as! UINavigationController
        let applyViewController = storyBoard.instantiateViewController(withIdentifier: "Apply") as! ApplyViewController
        
        navigationController.pushViewController(applyViewController, animated: false)
        
        mainController.selectedIndex = accountTabIndex
        applyViewController.callbackUrl = urlComponents
        
        self.window?.rootViewController = mainController
    }
    
    private func handleConsentRevoke(_ urlComponents: URLComponents) {
        let accountTabIndex = 1
        
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let mainController: UITabBarController = storyBoard.instantiateViewController(withIdentifier: "Main") as! UITabBarController
        let navigationController = mainController.viewControllers![accountTabIndex] as! UINavigationController
        let signedDocumentsViewController = storyBoard.instantiateViewController(withIdentifier: "SignedDocuments") as! SignedDocumentsViewController
        signedDocumentsViewController.callbackUrl = urlComponents
        
        navigationController.pushViewController(signedDocumentsViewController, animated: false)
        
        mainController.selectedIndex = accountTabIndex
        
        self.window?.rootViewController = mainController
    }
    
    private func handleDocumentSign(_ urlComponents: URLComponents) {
        let accountTabIndex = 0
        
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let mainController: UITabBarController = storyBoard.instantiateViewController(withIdentifier: "Main") as! UITabBarController
        let navigationController = mainController.viewControllers![accountTabIndex] as! UINavigationController
        let applyViewController = storyBoard.instantiateViewController(withIdentifier: "Apply") as! ApplyViewController
        let signViewController = storyBoard.instantiateViewController(withIdentifier: "Sign") as! SignViewController

        navigationController.pushViewController(applyViewController, animated: false)
        navigationController.pushViewController(signViewController, animated: false)
        
        mainController.selectedIndex = accountTabIndex
        signViewController.callbackUrl = urlComponents
        
        self.window?.rootViewController = mainController
    }
    
    private func handleLraConsent(_ urlComponents: URLComponents) {
        let accountTabIndex = 1
        
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let mainController: UITabBarController = storyBoard.instantiateViewController(withIdentifier: "Main") as! UITabBarController
        let navigationController = mainController.viewControllers![accountTabIndex] as! UINavigationController
        let accountSettingsViewController = storyBoard.instantiateViewController(withIdentifier: "AccountSettings")
        let dataFromLraViewController = storyBoard.instantiateViewController(withIdentifier: "DataFromLra") as! DataFromLraViewController
        
        navigationController.pushViewController(accountSettingsViewController, animated: false)
        navigationController.pushViewController(dataFromLraViewController, animated: false)
        
        mainController.selectedIndex = accountTabIndex
        dataFromLraViewController.callbackUrl = urlComponents
        
        self.window?.rootViewController = mainController
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // TODO persist authentication.
    }
        
    // MARK: UISceneSession Lifecycle
}
