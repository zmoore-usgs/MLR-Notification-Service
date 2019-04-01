# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]
## Added
- docker-related files to combine service and docker repos

## Changed
- dockerfile pulls version 0.0.4 wma-spring-boot-base docker image from dockerhub

## [0.7.2] - 2019-03-01
### Changed
- Standardized error message JSON format. 

## [0.7.1] - 2019-01-31
### Added
- Support for an email attachment.
 
### Changed
- Disabled TLS 1.0/1.1 by default. 
- change ERROR_MESSAGE_KEY to "error_message" so ui parsing can find it. 

## [0.7] - 2018-08-23
### Added
- service healthcheck
- Authorization to the notification endpoint

### Removed
- Dockerfile
- docker-entrypoint.sh
- keystorelocation and keystorepassword entries from application.yml

## [0.6] - 2017-11-20
### Added
- Dockerfile Healthcheck

### Changed
- application.yml to conform to other services' oauth config naming conventions

## [0.5] - 2017-11-03
### Added
- Global exception handler for Http requests 
- Authentication to the notification endpoint
- HTTPS Support

### Changed
- Modified the error messages returned by the application to be more concise and not include internal application information such as class names.

### Added
- Added additional logging of JSON parsing errors

## [0.4] - 2017-10-18
### Changed
- Modified the Send Email endpoint to consume a JSON email descriptor rather than use URL query parameters
- Can now specify an email sender (falls back to sender configured in the environment variables)

## [0.3] - 2017-10-02
### Changed
- Fixed incorrect distribution management section of the POM file.

## 0.2 - 2017-10-02
### Added
- No changes, burned release number

## 0.1 - 2017-10-02
### Added
- Initial integration of Spring Email with DOI SMTP to allow sending emails from the application through the DOI Email Server.

- POST Send Email endpoint which can specify a reciever, message body, and subject.
    - Endpoint: /notification/email

- YAML Configuration for the email sender and a message body template.

- Docker secret support for including a full application.yml for a Spring Application.

[Unreleased]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.7.2...master
[0.7.2]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.7.1...mlrNotification-0.7.2
[0.7.1]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.7...mlrNotification-0.7.1
[0.7]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.6...mlrNotification-0.7
[0.6]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.5...mlrNotification-0.6
[0.5]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.4...mlrNotification-0.5
[0.4]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.3...mlrNotification-0.4
[0.3]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.1...mlrNotification-0.3
 
