Relying Party integration guide
===============================

# Introduction

This guide is about the process to integrate PlanetID services (including consent) to Relying Party infrastructure. Usually to a web page or mobile application.

# Target Audience

The intended audience of this manual are developers responsible for integrating PlanetID services into their own application. Readers are expected to understand related internet standards, [RFC 6749 - The OAuth 2.0 Authorization Framework](https://tools.ietf.org/html/rfc6749) and [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html).


# Terminology

| Term                              | Description |
| --------------------------------- | ----------- |
| ASiC-E                            | https://en.wikipedia.org/wiki/Associated_Signature_Containers#ASiC_Extended_(ASiC-E)
| Authentication                    | Process used to achieve sufficient confidence in the binding between the Entity and the presented Identity |
| Cross-Site Request Forgery (CSRF) | It is an attack that forces an end user to execute unwanted actions on a web application in which they're currently authenticated. |
| OAuth 2.0                         | Open standard for access delegation |
| OpenID Connect Core 1.0           | OAuth 2.0 extension. Provides an identity layer on top of OAuth 2.0 framework |
| OpenID Provider (OP)              | OAuth 2.0 Authorization Server that is capable of Authenticating the End-User and providing Claims to a Relying Party about the Authentication event and the End-User. |
| PlanetID                          | Product of Planetway to offer identification. |
| Relying Party (RP)                | A company who has integrated PlanetID to their applications |
| Resource Server (RS)              | In OAuth context, this is the service the access is controlled by OP. Not applicable in PlanetID scenario |

# Overview

PlanetID provides authentication, authorization, signing and consent services as an extension of OpenID Connect specification.
PlanetID is an OpenID Connect Provider (OP) in OpenID Connect context.

# Setting up a Relying Party account

Any organization can become a Relying Party in PlanetID.

## Provided by Relying Party

Applicant provides following information to sign up.

| Name | Exaple | Required | Description |
| ---- | ------ | -------- | ----------- |
| TDB code | 368015672 | Y | PlanetID uses TDB code to internally identify organizations. |
| Relying party name in English | AAA Data Bank | Y | Human readable name for the relying party. This will be shown to end users on the PlanetID smartphone app. |
| Relying party name in Japanese | AAA情報銀行 | Y | Human readable name for the relying party. This will be shown to end users on the PlanetID smartphone app. |
| Icon image for English | | N | Icon for the relying party for English end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Icon image for Japanese | | N | Icon for the relying party for Japanese end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Redirect URIs | https://aaa.example.com/callback,<br />http://localhost:8000/callback | Y | List of all possible redirect URIs for OpenID Connect. Can be different for signing in, linking an account, signing a document, signing a concent. |
| Environment | PoC or Production | Y | PoC env connects to PlanetCross JP-TEST instance.<br />Production env connects to PlanetCross JP instance. |

Additionally, if relying party asks the user to sign a consent, applicant should provide following information.

| Name | Exaple | Required | Description |
| ---- | ------ | -------- | ----------- |
| Client name in English | BBB Insurance | Y | Human readable name for the client that sends a request over PlanetCross to fetch information from the data provider. This will be shown to end users on the PlanetID smartphone app. |
| Client name in Japanese | BBB 保険 | Y | Human readable name for the client that sends a request over   PlanetCross to fetch information from the data provider. This will be shown to end users on the PlanetID smartphone app. |
| Client icon image for English | | N | Icon for the client for English end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Client icon image for Japanese | | N | Icon for the client for Japanese end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Client PlanetCross subsystem code | JP-TEST/COM/0170123456789/client | Y | Full qualified subsystem code for the client subsystem on PlanetCross.|
| Purpose of the data access in English | BBB Portal uses your address and phone number to speed up creating the insurance contract. | Y | Human readable description how the client uses the data to provide benefit to the end user. This will be shown to end users on the PlanetID smartphone app. |
| Purpose of the data access in Japanese | BBB ポータルはあなたの電話番号と住所を使用して保険契約書の作成を速やかに行います。 | Y | Human readable description how the client uses the data to provide benefit to the end user. This will be shown to end users on the PlanetID smartphone app. |
| Data provider name in English | CCC Bank | Y | Human readable name for the data provider that responds to a request over PlanetCross from the client. This will be shown to end users on the PlanetID smartphone app. |
| Data provider name in Japanese | CCC 銀行 | Y | Human readable name for the data provider that responds to a request over PlanetCross from the client. This will be shown to end users on the PlanetID smartphone app. |
| Data provider icon image for English | | N | Icon for the data provider for English end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Data provider icon image for Japanese | | N | Icon for the data provider for Japanese end users. This will be shown to end users on the PlanetID smartphone app. Icon image should be 1024x1024 in PNG format. |
| Data provider PlanetCross service code | JP-TEST/COM/0170123456789/provider/service | Y | Full qualified service code for the data provider service on PlanetCross. |
| Description of data in English | Address, phone number | Y | Human readable description of all the data that is provided from data provider to client about the end user. This will be shown to end users on the PlanetID smartphone app. |
| Description of data in Japanese | 住所、電話番号 | Y | Human readable description of all the data that is provided from data provider to client about the end user. This will be shown to end users on the PlanetID smartphone app. |

<pre>Please make sure that data provider service has given access to the client subsystem before trying out consent, in the PlanetCross Security Server admin UI.</pre>


## Provided by Planetway

Following information is provided to applicant in return.
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| client_id             | JP.0170123456789 | client_id in terms of OpenID Connect |
| client_secret         | W487A6pJjNi+SayDd/KP | client_secret in terms of OpenID Connect |
| PlanetID API endpoint | https://api.prod.planet-id.me<br />https://api.poc.planet-id.me	| The base URL for the APIs. Following URL provides the OpenID Connect Provider Configuration Information endpoint according to OpenID Connect Discovery 1.0. API endpoint + ".well-known/openid-configuration" |
| Consent API endpoint  | https://consent.prod.planet-id.me<br />https://consent.poc.planet-id.me | For checking if a consent exists, is expired or is revoked. |

# Authenticating, linking, creating and revoking a consent

## Overview
The main function of a PlanetID application is to create a digital signature (cryptographic signature). In case of creating and revoking a consent, the data to be signed (payload) is given by the Relying Party. In case of authenticating and linking the data to be signed is generated by PlanetID service.

## Creating a digital signature

The process of creating a digital follow OpenID Connect Authorization Code Flow (https://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth). For the integrator this means implementing 3 steps.

### Step 1. Construct an URL and redirect the user to PlanetID service.
The base URL for production environment is `https://api.prod.planet-id.me/v2/openid/auth`

The base URL for PoC environment is `https://api.poc.planet-id.me/v2/openid/auth`

Parameters:
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| action | consent | The flow that is initilized by the user |
| scope | openid | The scope values are defined in the OAuth 2.0 specification. In this case it is always openid
| client_id | JP.0170368015672 | Unique identification for the Relying Party in PlanetID backend. This is issued by Planetway. |
| redirect_uri | https://aaa.example.com/callback-login | Endpoint managed by the Relying Party. The endpoint is responsible of exchanging access code for access token and id token. This has to exactly match one of the redirect URIs that was provided to Planetway when signing up. |
| response_type | code | The response type is defined in the OAuth 2.0 specification, In this case it is always code |
| nonce | 10da45890c48... | A random string generated and digested using sha256 by Relying Party backend to mitigate replay attack. |
| state | IQaLnKzVi | A random string generated  by Relying Party backend to mitigate CSRF attack. |
| level | 1 | Determines the verification level of the user. |
| planet_id | 123456789321 | the planet id of the user. In case of authentication and linking this parameter is excluded |
| payload | (see examples below) | URL-Encoded data to be signed. In case of authentication and linking this parameter is excluded |

Example request in case of authentication:
```
https://api.planet-id.me/v2/openid/auth?scope=openid&client_id=JP.0170368015672&redirect_uri=https%3A%2F%2Faaa.example.com%2Fcallback-login&response_type=code&nonce=10da45890c48127d5c6fc27d5894b2f5058c57fad5ff48c3083eef41722d1bd7&state=SWOHBgvFe&action=authenticate
````

Example payload for creating a consent:
```
<?xml version="1.0" encoding="UTF-8"?>
<signatureInput xmlns="https://www.planetway.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <signRequestType>consent_give</signRequestType>
   <requestUUID>a4265be3-2963-42f3-a011-8495772b9212</requestUUID>
   <requestURI>https://api.prod.planetway.com/signrequests/?uuid=25f71a40-ada0-4548-ac33-739056f17933</requestURI>
   <validTill>2021-12-16T22:00:00Z</validTill>
   <revokable>true</revokable>
   <data>
      <planetId>565932316113</planetId>
      <dataProvider>
         <relyingPartyCode>JP.0170000000001</relyingPartyCode>
         <planetXCode>JP-TEST/COM/0170000000001/gk</planetXCode>
      </dataProvider>
      <dataService>JP-TEST/COM/0170000000001/gk/pitatohouse</dataService>
      <dataConsumer>
         <relyingPartyCode>JP.0170368015672</relyingPartyCode>
         <planetXCode>JP-TEST/COM/0170368015672/consumer</planetXCode>
      </dataConsumer>
   </data>
</signatureInput>
```
Example payload URL-Encoded
```
%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22UTF-8%22%3F%3E%0A%3CsignatureInput%20xmlns%3D%22https%3A%2F%2Fwww.planetway.com%22%20xmlns%3Axsi%3D%22http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema-instance%22%3E%0A%20%20%20%3CsignRequestType%3Econsent_give%3C%2FsignRequestType%3E%0A%20%20%20%3CrequestUUID%3Ea4265be3-2963-42f3-a011-8495772b9212%3C%2FrequestUUID%3E%0A%20%20%20%3CrequestURI%3Ehttps%3A%2F%2Fapi.prod.planetway.com%2Fsignrequests%2F%3Fuuid%3D25f71a40-ada0-4548-ac33-739056f17933%3C%2FrequestURI%3E%0A%20%20%20%3CvalidTill%3E2021-12-16T22%3A00%3A00Z%3C%2FvalidTill%3E%0A%20%20%20%3Crevokable%3Etrue%3C%2Frevokable%3E%0A%20%20%20%3Cdata%3E%0A%20%20%20%20%20%20%3CplanetId%3E565932316113%3C%2FplanetId%3E%0A%20%20%20%20%20%20%3CdataProvider%3E%0A%20%20%20%20%20%20%20%20%20%3CrelyingPartyCode%3EJP.0170000000001%3C%2FrelyingPartyCode%3E%0A%20%20%20%20%20%20%20%20%20%3CplanetXCode%3EJP-TEST%2FCOM%2F0170000000001%2Fgk%3C%2FplanetXCode%3E%0A%20%20%20%20%20%20%3C%2FdataProvider%3E%0A%20%20%20%20%20%20%3CdataService%3EJP-TEST%2FCOM%2F0170000000001%2Fgk%2Fpitatohouse%3C%2FdataService%3E%0A%20%20%20%20%20%20%3CdataConsumer%3E%0A%20%20%20%20%20%20%20%20%20%3CrelyingPartyCode%3EJP.0170368015672%3C%2FrelyingPartyCode%3E%0A%20%20%20%20%20%20%20%20%20%3CplanetXCode%3EJP-TEST%2FCOM%2F0170368015672%2Fconsumer%3C%2FplanetXCode%3E%0A%20%20%20%20%20%20%3C%2FdataConsumer%3E%0A%20%20%20%3C%2Fdata%3E%0A%3C%2FsignatureInput%3E
```

Example payload for revoking a consent:
```
<?xml version="1.0" encoding="UTF-8"?>
<signatureInput xmlns="https://www.planetway.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <signRequestType>consent_revoke</signRequestType>
	 <requestUUID>fd6cf538-2649-4e01-be2e-cf7a9462cc72</requestUUID>
   <consentUUID>a4265be3-2963-42f3-a011-8495772b9212</consentUUID>
   <targetUserId>565932316113</targetUserId>
</signatureInput>
```
### Step 2. Receive callback request
After the digital signature is completed, user cancels the process or in case of an error PlanetID service will make an HTTP request to the provided callback URL.

Parameters in case of a success:
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| code | A jwt | Encoded string to convey the validity code |
| state | IQaLnKzVi | A random string generated by Relying Party backend (a session token for matching which user initialized the flow) |

Parameters in case of an error:
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| error | invalid_redirect_uri | Error code indicating what went wrong |
| state | IQaLnKzVi | A random string generated by Relying Party backend (a session token for matching which user initialized the flow) |

Error codes:
| Name                  | Description |
| --------------------- | ----------- |
| invalid_redirect_uri | If the parameter value for `redirect_uri` is unknown for PlanetID service |
| user_rejected | The flow was cancelled by the user |
| session_expired | The user was unable to finish the signing flow withing 5 minutes |
| access_code_not_generated | PlanetID service was unable to finalize the digital signature due to system error |
| system_error | Something bad happened in PlanetID service during the flow |
| unknown | Something bad happened that was not handled |

### Step 3. Request signature details from PlanetID service
This step is called Token Request (https://openid.net/specs/openid-connect-core-1_0.html#TokenRequest). The Relying Party will 'exchange' the authorization code received in Step 2 to data about the signature.

URL in production: `https://api.prod.planet-id.me/v2/openid/token`

URL in PoC environment: `https://api.poc.planet-id.me/v2/openid/token`

Parameters:
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| code | A jwt | The code that was received in the callback request |
| grant_type | authorization_code | The grand type is defined in the OAuth 2.0 specification, In this case it is always `authorization_code` |
| redirect_uri | https://aaa.example.com/callback-login | The same URI the callback was made to |

Successful response to this request is a JSON document containing following fields:
| Name                  | Example | Description |
| --------------------- | ------- | ----------- |
| expires_in | 600000 | Time in milliseconds the access_token is valid |
| access_token | A jwt | Specified by OAuth 2.0 |
| token_type | Bearer | Information about the access token. Always has the value of `Bearer` |
| refresh_token | null | not used |
| id_token | A jwt | Specified by OAuth 2.0 |
| payload | Base64 encoded zip file | ASiC-E document contaning the payload and the digital signature |
| payloadUuid | e1f76536-10be-403f-b9dc-041655c4c015 | ASiC-E document identifier in PlanetID service |
| signature | Base64 encoded bytes | The digital signature of the payload for convenience |
| certificate | -----BEGIN CERTIFICATE----- ... | The User's X.509 certificate in PEM format used to validate the digital signature |

### Remarks
It is the responsibility of an Relying Party to check if the ASiC-E container is valid. It is possible to use our ASiC-E container validator. For more info see [ASiC-E container validation](#ASiC-E-container-validation).

In case of authentication and linking, in most cases the only interesting bit of information is the `id_token`. This is a JWT document and contains the PlanetID of the user as value for `sub`. 

In case of creating and revoking a consent, in most cases the only interesting bit of information is the payload.

# Managing linked PlanetIDs
## Checking if a PlanetID is linked

Using this endpoint the Relying Party can query for a PlanetID.

URL in production: `https://api.prod.planet-id.me/v2/relying-parties/identities/<PlanetID>`

URL in PoC environment: `https://api.poc.planet-id.me/v2/relying-parties/identities/<PlanetID>`

Verb: `GET`

Parameters:
 * `PlanetID` - the PlanetID of a user.

Example: `https://api.prod.planet-id.me/v2/relying-parties/identities/12345678900`

Output in case the user is linked - HTTP response code `200`
```json
{
	"planetId": "12345678900",
	"createdAt": "2022-05-30T12:28:45.002+00:00"
}
```

Output in case the user is not linked - HTTP response code `404`.

```json
{ "error": "notFound" }
```

Output in case the Relying Party cannot be authenticated - HTTP response code `401`

```json
{ "error": "unauthorized" }
```

**Authentication:** The client needs to use Basic Authentication scheme. The credentials are the same, as described above (client_id and client_secret)

## Removing a link to a PlanetID

Using this endpoint the Relying Party can query for a PlanetID.

URL in production: `https://api.prod.planet-id.me/v2/relying-parties/identities/<PlanetID>`

URL in PoC environment: `https://api.poc.planet-id.me/v2/relying-parties/identities/<PlanetID>`

Verb: `DELETE`

Parameters:
 * `PlanetID` - the PlanetID of a user to unlink.

Example: `https://api.prod.planet-id.me/v2/relying-parties/identities/12345678900`

Output in case the user is linked - HTTP response code `204`. No response body (even if the link did not exist before).

Output in case the Relying Party cannot be authenticated - HTTP response code `401`

```json
{ "error": "unauthorized" }
```

**Authentication:** The client needs to use Basic Authentication scheme. The credentials are the same, as described above (client_id and client_secret)


# Getting consent status

This endpoint is for relying parties who participate in the context of the consent as a provider or as a consumer.

URL in production: `https://consent.prod.planet-id.me/v2/relying-parties/consent-status`

URL in PoC environment: `https://consent.poc.planet-id.me/v2/relying-parties/consent-status`

Verb: `GET`

Parameters:
 * `targetUserId` - the PlanetID of the user who gave the consent
 * `consumerSubsystemId` - the party who needs the data about the target user. Parameter value format `[Instance code]/[member class]/[member code]/[subsystem code]`. Example: `JP/COM/0170000000001/a-subsystem`
 * `providerServiceId` - the service that provides the data about the target user. Parameter value format `[Instance code]/[member class]/[member code]/[subsystem code]/[service code]`. Example: `JP/COM/0170000000001/b-subsystem/some-service`

Example: `https://consent.prod.planet-id.me/v2/relying-parties/consent-status?targetUserId=728449601214&consumerSubsystemId=JP%2FCOM%0170000000001%2Fa-subsystem&providerServiceId=JP%2FCOM%0170000000002%2Fb-subsystem%2Fsome-service`

Output in case the consent exists - HTTP response code `200`
```json
{ "consentStatus": "exists" }
```

Output in case the consent does not exist or is revoked - HTTP response code `404`. The response body is one of

```json
{ "consentStatus": "doesNotExist" }
{ "consentStatus": "revoked" }
{ "consentStatus": "expired" }
```

Output in case the Relying Party cannot be authenticated - HTTP response code `401`

```json
{ "error": "unauthorized" }
```

**Authentication:** The client needs to use Basic Authentication scheme. The credentials are the same, as described above (client_id and client_secret)

**Authorization:** Only the consumer and producer can query for the consent they participate in.

# ASiC-E container validation
The validator software `planetid-asice-validator.jar` can be downloaded from this repository.

This validator is based on Estonian digidoc4j validator with added support for RSASSA-PSS algorithm described 
in  https://tools.ietf.org/html/rfc6931#section-2.3.9

Support was added to apache xmlsec and sd-dss open source libraries. See patches directory for changes in these
libraries.

The software requires Java Runtime installed

Usage: `java -jar planetid-asice-validator.jar filename.asice`

In case of valid container the software should print out
```
INFO org.digidoc4j.impl.asic.asice.AsicEContainerValidator - Is container valid: true
```

In case of invalid container the software should print out
```
INFO org.digidoc4j.impl.asic.asice.AsicEContainerValidator - Is container valid: false
```