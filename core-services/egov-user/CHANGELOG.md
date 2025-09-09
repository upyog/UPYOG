# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-04-20
- Added 3 Api endpoints V2 for new address fields to create, update and search users
- Added new address fields in address model and table - address2, houseNumber,houseName, streetName, landmark, locality
- Added new Address api endpoints to create, update and search address
- Added new status field in address table to handle update Address functionality
- During update old address will be made inactive and new address will be created 
- Unique address constraint was modified to include created date
- Duplicate 'Other' addresses can be made except 'permanent' and 'correspondence' address

## 1.3.0 - 2023-02-06

- Transition from 1.3.0-beta version to 1.3.0 version

## 1.3.0-beta - 2022-06-22

- Enhanced user service for the implementation of privacy feature.
- Added logback-classic version 1.2.0 and updated tracer to remove a critical vulnerabilities

## 1.2.7 - 2022-02-02
- Added security fixes for user enumerration issue.
- Added size validation on user models
- Added email and sms notification feature whenever user changes email.

## 1.2.6 - 2022-01-13
- Updated to log4j2 version 2.17.1

## 1.2.5 - 2021-07-26
- Added OTHERS as one of the gender option values
- Allowed names with apostrophe symbol

## 1.2.4 - 2021-05-11
- added permanentCity in oAuth response
- added html validations on input fields
- replaced OTHER with TRANSGENDER in gender enum
- corrections to error handling
- updated LOCALSETUP.md



## 1.2.3 - 2021-02-26
- Updated domain name in application.properties
- Added size validations

## 1.2.2 - 2020-01-12
- Added field relationShip with guardian and refactoration of code.

## 1.2.1 - 2020-07-14

- Upgraded to kafka 1.3.11.RELEASE

## 1.2.0 - 2020-07-02

- Added support for encrypting user PII data

## 1.1.0 - 2020-06-19

- Added password policy for additional security

## 1.0.0

- Base version
