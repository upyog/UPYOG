import UIKit
import Flutter
import Foundation
import CommonCrypto

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {

    let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
        let batteryChannel = FlutterMethodChannel(name: "flutter.dev/NDPSAESLibrary",
                                                  binaryMessenger: controller.binaryMessenger)

            batteryChannel.setMethodCallHandler({
              [weak self] (call: FlutterMethodCall, result: FlutterResult) -> Void in
  
                guard let args = call.arguments as? [String : Any] else {return}
                let AESMethod = args["AES_Method"] as! String
                let encDecKey = args["encKey"] as! String
                let encText = args["text"] as! String
                
                if(call.method == "NDPSAESInit") {
                    if(AESMethod == "encrypt") {
                        let encryptionResult = self?.getAtomEncryption(plainText: encText, key: encDecKey)
                        result(encryptionResult!)
                    }else{
                        let decryptionResult = self?.getAtomDecryption(cipherText: encText, key: encDecKey)
                        result(decryptionResult!)
                    }
                }else{
                    result(FlutterMethodNotImplemented)
                    return
                }
              })
      GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  private func getAtomEncryption(plainText: String!, key: String!) -> String? {
      let pswdIterations:UInt32 = 65536
      let keySize:UInt = 32
      let ivBytes: Array<UInt8> = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]

            let derivedKey = PBKDF.deriveKey(password: key,
                                             salt: key,
                                             prf: .sha512,
                                             rounds: pswdIterations,
                                             derivedKeyLength: keySize)
            let cryptor = Cryptor(operation: .encrypt,
                                  algorithm: .aes,
                                  options: [.PKCS7Padding],
                                  key: derivedKey,
                                  iv: ivBytes)
            let cipherText = cryptor.update(plainText)?.final()
            let hexStr = hexString(fromArray:cipherText.map{$0}!)
            return hexStr.uppercased()
  }

  public func hexString(fromArray : [UInt8], uppercase : Bool = false) -> String
  {
        return fromArray.map() { String(format:uppercase ? "%02X" : "%02x", $0) }.reduce("", +)
  }
    
  public func getAtomDecryption(cipherText: String!,
                           key: String!) -> String? {
        let pswdIterations:UInt32 = 65536
        let keySize:UInt = 32
        let ivBytes: Array<UInt8> = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
        
           let derivedKey = PBKDF.deriveKey(password: key,
                                            salt: key,
                                            prf: .sha512,
                                            rounds: pswdIterations,
                                            derivedKeyLength: keySize)
           let cryptor = Cryptor(operation: .decrypt,
                                 algorithm: .aes,
                                 options: [.PKCS7Padding],
                                 key: derivedKey,
                                 iv: ivBytes)
      if let data = hexadecimal(content: cipherText!),
              let decryptedPlainText = cryptor.update(data)?.final() {
               let decryptedString = String(bytes: decryptedPlainText,
                                            encoding: .utf8)
               return decryptedString
           }
           return nil
       }

   public func hexadecimal(content: String!) -> Data? {
            let regexPattern = "[0-9a-f]{1,2}";
            var data = Data(capacity: content.count / 2)
            let regex = try! NSRegularExpression(pattern: regexPattern,
                                                 options: .caseInsensitive)
            regex.enumerateMatches(in: content,
                                   options: [],
                                   range: NSMakeRange(0, content.count)) { match, flags, stop in
                let byteString = (content as NSString).substring(with: match!.range)
                var num = UInt8(byteString, radix: 16)!
                data.append(&num, count: 1)
            }
            guard data.count > 0 else {
                return nil
            }
            return data
  }
}
