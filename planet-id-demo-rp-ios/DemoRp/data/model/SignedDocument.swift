//
//  SignedDocument.swift
//  DemoRp
//
//  Created by Margus Räim on 17.09.2022.
//  Copyright © 2022 Toomas Laigna. All rights reserved.
//

struct SignedDocument : Codable {
    var uuid: String
    var signatureType: String
    var consentUuid: String?
    var revokeDocumentUuid: String?
}
