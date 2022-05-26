//
//  DataFromLraSuccessViewController.swift
//  DemoRp
//
//  Created by Toomas Laigna on 19.03.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import UIKit

class DataFromLraSuccessViewController: UIViewController {
    
    @IBOutlet weak var textNameRomaji: UILabel!
    @IBOutlet weak var textNameKatakana: UILabel!
    @IBOutlet weak var textNameKanji: UILabel!
    @IBOutlet weak var textDateOfBirth: UILabel!
    @IBOutlet weak var textAddress: UILabel!
    
    override func viewDidLoad() {
        let person = UserRepository.shared.lraPerson!
        
        textNameRomaji.text = "\(person.firstNameRomaji ?? "") \(person.lastNameRomaji ?? "")"
        textNameKatakana.text = "\(person.lastNameKatakana ?? "") \(person.firstNameKatakana ?? "")"
        textNameKanji.text = "\(person.lastNameKanji ?? "") \(person.firstNameKanji ?? "")"
        textDateOfBirth.text = person.dateOfBirth ?? "-"
        textAddress.text = person.address.first?.toString() ?? "-"
    }
}
