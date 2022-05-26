//
//  ApiClient.swift
//  DemoRp
//
//  Created by Toomas Laigna on 10.02.2020.
//  Copyright © 2020 Planetway. All rights reserved.
//

import Foundation
import PlanetIDRP

#if BUILD_POC
let DefaultAPI = "https://fudosan.poc2.planet-id.me"
#else
let DefaultAPI = "https://fudosan.test.planet-id.me"
#endif
// DefaultAPI = "http://localhost:8082"

class ApiClient {
    
    static let shared = ApiClient()
    
    func login(email: String, password: String, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        postJson("/api/authenticate", UserCredentials(user: email, password: password), onComplete)
    }
    
    func loginWithPlanetId(_ onComplete: @escaping (AuthRequest?, Any?) -> Void) {
        post("/api/authenticate-planet-id", onComplete)
    }
    
    func loginWithPlanetIdCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        postJson("/api/authenticate-planet-id/callback", authResponse, onComplete)
    }
    
    func logOut(_ onComplete: @escaping (Empty?, Any?) -> Void) {
        post("/api/logout", onComplete)
    }
    
    func createAccount(email: String, password: String, _ onComplete: @escaping (Empty?, Any?) -> Void) {
        postJson("/api/user", UserCredentials(user: email, password: password), onComplete)
    }
    
    func deleteAccount(_ onComplete: @escaping (Empty?, Any?) -> Void) {
        delete("/api/user", onComplete)
    }
    
    func linkWithPlanetId(onComplete: @escaping (AuthRequest?, Any?) -> Void) {
        post("/api/user/link", onComplete)
    }
    
    func linkWithPlanetIdCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        postJson("/api/callback/link", authResponse, onComplete)
    }
    
    func unlinkWithPlanetId(_ onComplete: @escaping (UserInfo?, Any?) -> Void) {
        post("/api/user/unlink", onComplete)
    }
    
    func dataBankPerson(dataBank: String, _ onComplete: @escaping (Dictionary<String, String>?, Any?) -> Void) {
        get("/api/data-bank/\(dataBank)/person", onComplete)
    }

    func dataBankConsent(dataBank: String, language: String, _ onComplete: @escaping (AuthRequest?, Any?) -> Void) {
        // TODO: query parameters.
//        post("/api/data-bank/\(dataBank)/consent?lang=\(language)", onComplete)
        post("/api/data-bank/\(dataBank)/consent", onComplete)
    }
    
    func consentCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (Empty?, Any?) -> Void) {
        postJson("/api/callback/consent", authResponse, onComplete)
    }
    
    func sign(document: String, _ onComplete: @escaping (AuthRequest?, Any?) -> Void) {
        postMultipart("/api/document/sign", document.data(using: .utf8)!, onComplete)
    }
    
    func signCallback(_ authResponse: AuthResponse, _ onComplete: @escaping (Empty?, Any?) -> Void) {
        postJson("/api/callback/sign", authResponse, onComplete)
    }
    
    func lraPerson(_ onComplete: @escaping (Person?, Any?) -> Void) {
        get("/api/lra/person", onComplete)
    }
    
    func lraConsent(_ onComplete: @escaping (AuthRequest?, Any?) -> Void) {
        post("/api/lra/consent", onComplete)
    }

    private func url(_ endpoint: String) -> URL {
        return URL(string: DefaultAPI)!.appendingPathComponent(endpoint)
    }
    
    private func get<T: Codable>(_ endpoint: String, _ onComplete: @escaping (T?, Any?) -> Void) {
        var request = URLRequest(url: url(endpoint))
        request.httpMethod = "GET"
        
        performRequest(request, onComplete)
    }
    
    private func post<T: Codable>(_ endpoint: String, _ onComplete: @escaping (T?, Any?) -> Void) {
        var request = URLRequest(url: url(endpoint))
        request.httpMethod = "POST"
        
        performRequest(request, onComplete)
    }
    
    private func postJson<T: Codable, R: Codable>(_ endpoint: String, _ body: T?, _ onComplete: @escaping (R?, Any?) -> Void) {
        var request = URLRequest(url: url(endpoint))
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try JSONEncoder().encode(body)
        } catch let error {
            print(error.localizedDescription)
        }
        
        performRequest(request, onComplete)
    }
    
    private func postMultipart<R: Codable>(_ endpoint: String, _ body: Data, _ onComplete: @escaping (R?, Any?) -> Void) {
        var request = URLRequest(url: url(endpoint))
        request.httpMethod = "POST"
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        var data = Data()
        data.append("\r\n--\(boundary)\r\n".data(using: .utf8)!)
        data.append("Content-Disposition: form-data; name=\"file\"; filename=\"foo.bar\"\r\n".data(using: .utf8)!)
        data.append("Content-Type: text/plain\r\n\r\n".data(using: .utf8)!)
        data.append(body)
        data.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)

        request.httpBody = data
        performRequest(request, onComplete)
    }
    
    private func delete<T: Codable>(_ endpoint: String, _ onComplete: @escaping (T?, Any?) -> Void) {
        var request = URLRequest(url: url(endpoint))
        request.httpMethod = "DELETE"

        performRequest(request, onComplete)
    }
    
    func performRequest<T: Codable>(_ request: URLRequest, _ onComplete: @escaping (T?, Any?) -> Void) {
        let urlSession = URLSession(configuration: .default)
        
        let task = urlSession.dataTask(with: request as URLRequest, completionHandler: { data, response, error in
            self.requestCompletionHandler(data, response as? HTTPURLResponse, error, onComplete)
        })

        task.resume()
    }
    
    private func requestCompletionHandler<T: Codable>(_ data: Data?, _ response: HTTPURLResponse?, _ error: Error?, _ onComplete: @escaping (T?, Any?) -> Void) {
        print("response=\(String(describing: response)), error=\(String(describing: error)), data=\(data != nil ? String(data: data!, encoding: .utf8) ?? "" : "")")
        
        guard error == nil else {
            DispatchQueue.main.async {
                onComplete(nil, NetworkError(message: error!.localizedDescription))
            }
            
            return
        }
        
        if (response!.statusCode == 401) {
            DispatchQueue.main.async {
                RpApp.global.switchToLogin()
            }
            
            return
        }
        
        let isSuccess = 200..<300 ~= response!.statusCode
        if (isSuccess) {
            let result: Any?
            if T.self == Empty.self {
                result = Empty()
            } else {
                result = try! JSONDecoder().decode(T.self, from: data!)
            }
            
            DispatchQueue.main.async {
                onComplete(result as? T, nil)
            }
        } else {
            do {
                let apiError = try JSONDecoder().decode(ApiError.self, from: data!)
            
                DispatchQueue.main.async {
                    onComplete(nil, apiError)
                }
            } catch _ {
                DispatchQueue.main.async {
                    onComplete(nil, response!.statusCode)
                }
            }
        }
    }
}
