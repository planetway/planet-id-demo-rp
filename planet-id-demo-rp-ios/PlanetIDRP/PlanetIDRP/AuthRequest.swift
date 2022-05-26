//
//  AuthRequest.swift
//  PlanetIDRP
//
//  Created by Toomas Laigna on 12.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

/**
 AuthenticationRequest in OpenID Connect 1.0 specification.
 PlanetID uses this to deliver the authentication request and also payloads for consent and signing requests.
 Snake case is deliberate.
 */
public struct AuthRequest : Codable {
    // Required
    public var action: Action
    // Required
    // client_id is the OAuth 2.0 client identifier.
    public var client_id: String
    public var language: String?
    public var level: Int?
    // nonce is the OpenID Connect 1.0 nonce.
    public var nonce: String?
    // payload is used to deliver the consent and sign document.
    public var payload: String?
    // planet_id is the PlanetID of the user. Set when action is consent or sign, and the RP knows the PlanetID of the user.
    public var planet_id: String?
    // Required
    public var provider_uri: String
    // Required
    // redirect_uri is the OAuth 2.0 redirect URI.
    public var redirect_uri: String
    // Required
    // response_type is the OAuth 2.0 response_type. Set to "code".
    public var response_type: String
    // return_app_uri is used to return to this app or URL when the action is finished in the PlanetID app. Set to the custom schema or Universal Link URL to open this app.
    public var return_app_uri: String?
    // Required
    // scope is the OAuth 2.0 scope. Set to "openid".
    public var scope: String
    // state is the OpenID Connect 1.0 state.
    public var state: String?
    
    public func url() -> URL {
        var urlComponents = URLComponents()
        urlComponents.scheme = "planetid" // planetid://
        urlComponents.host = "auth"
        var items :[URLQueryItem] = [
            URLQueryItem(name: "client_id", value: self.client_id),
            URLQueryItem(name: "signature_type", value: self.action.signatureType), // TODO do we need this ?
            URLQueryItem(name: "scope", value: self.scope),
            URLQueryItem(name: "action", value: self.action.rawValue),
            URLQueryItem(name: "redirect_uri", value: self.redirect_uri),
            URLQueryItem(name: "response_type", value: self.response_type),
            URLQueryItem(name: "nonce", value: self.nonce),
            URLQueryItem(name: "state", value: self.state),
            URLQueryItem(name: "language", value: self.language),
            URLQueryItem(name: "return_app_uri", value: self.return_app_uri),
        ]
        if self.payload != nil {
            items.append(URLQueryItem(name: "payload", value: self.payload))
        }
        urlComponents.queryItems = items
        return urlComponents.url!
    }
}

public enum Action :String, Codable {
    case authenticate
    case consent
    case sign
    
    var signatureType :String {
        get {
            switch self {
            case .authenticate:
                return "AUTHENTICATION"
            case .consent:
                return "CONSENT"
            case .sign:
                return "SIGNING"
            }
        }
    }
}
