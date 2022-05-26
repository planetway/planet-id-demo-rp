//
//  PlanetIDRPTests.swift
//  PlanetIDRPTests
//
//  Created by Masakazu Ohtsuka on 2020/08/26.
//  Copyright © 2020 Planetway. All rights reserved.
//

import XCTest
@testable import PlanetIDRP

class PlanetIDRPTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func test() {
        var authRequest = AuthRequest(action: .authenticate,
                      client_id: "JP.123",
                      language: "en",
                      level: nil,
                      nonce: "123asd!=#&",
                      payload: nil,
                      planet_id: nil,
                      provider_uri: "https://example.com",
                      redirect_uri: "https://rp.example.com/redirect",
                      response_type: "code",
                      scope: "openid",
                      state: "123asd!=#&")
        authRequest.return_app_uri = "exampleapp://returned"
        let url = authRequest.url()
        // print("url=\(url)")
        
        XCTAssertEqual("planetid://auth?client_id=JP.123&signature_type=AUTHENTICATION&scope=openid&action=authenticate&redirect_uri=https://rp.example.com/redirect&response_type=code&nonce=123asd!%3D%23%26&state=123asd!%3D%23%26&language=en&return_app_uri=exampleapp://returned", url.absoluteString)
    }
}
