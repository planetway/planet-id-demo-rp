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

# Getting consent status

This endpoint is for relying parties who participate in the context of the consent as a provider or as a consumer.

URL in production: `https://consent.prod.planet-id.me/v2/relying-parties/consent-status`

URL in PoC environment: `https://consent.poc.planet-id.me/v2/relying-parties/consent-status`

Verb: `GET`

Parameters:
 * `targetUserId` - the PlanetID of the user who gave the consent
 * `consumerSubsystemId` - the party who needs the data about the target user. Parameter value format `[Instance code]/[member class]/[member code]/[subsystem code]`. Example: `JP/COM/0170000000001/a-subsystem`
 * `providerServiceId` - the service that provides the data about the target user. Parameter value format `[Instance code]/[member class]/[member code]/[subsystem code]/[service code]`. Example: `JP/COM/0170000000001/b-subsystem/some-service`

Example: https://consent.prod.planet-id.me/v2/relying-parties/consent-status?targetUserId=728449601214&consumerSubsystemId=JP%2FCOM%0170000000001%2Fa-subsystem&providerServiceId=JP%2FCOM%0170000000002%2Fb-subsystem%2Fsome-service

Output in case the consent exists - HTTP response code `200`
```json
{"consentStatus": "exists"}
```

Output in case the consent does not exist or is revoked - HTTP response code `404`. The response body is one of

```json
{"consentStatus": "doesNotExist"}
{"consentStatus": "revoked"}
{"consentStatus": "expired"}
```

**Authentication:** The client needs to use Basic Authentication scheme. The credentials are the same, as described above (client_id and client_secret)

**Authorization:** Only the consumer and producer can query for the consent they participate in.