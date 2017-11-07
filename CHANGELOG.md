# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]
### Added
- Global exception handler for Http requests

### Changed
- Modified the error messages returned by the application to be more concise and not include internal application information such as class names.

### Added
- Added additional logging of JSON parsing errors

## [0.4]
### Changed
- Modified the Send Email endpoint to consume a JSON email descriptor rather than use URL query parameters
- Can now specify an email sender (falls back to sender configured in the environment variables)

## [0.3]
### Changed
- Fixed incorrect distribution management section of the POM file.

## 0.2
### Added
- No changes, burned release number

## 0.1
### Added
- Initial integration of Spring Email with DOI SMTP to allow sending emails from the application through the DOI Email Server.

- POST Send Email endpoint which can specify a reciever, message body, and subject.
    - Endpoint: /notification/email

- YAML Configuration for the email sender and a message body template.

- Docker secret support for including a full application.yml for a Spring Application.

[Unreleased]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.4...master
[0.4]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.3...mlrNotification-0.4
[0.3]: https://github.com/USGS-CIDA/MLR-Notification-Service/compare/mlrNotification-0.1...mlrNotification-0.3
 