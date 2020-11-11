# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.1.0] - 2020-11-12

### Added
- AEM 6.5 stability improvements

### Changed
- Switched from the Adobe managed `AttrBuilder` class to a custom in-house managed version that uses a non-deprecated XSS handler
- The Vue model property handler has been rewritten to be more extensible

### Fixed
- Vue model no longer fails to render output when it comes across a JCR property it fails to understand

### Security
- Updated junit:junit to v4.13.1
- Updated com.google.guava:guava to v30.0-jre

## [2.0] - 10/05/2019

### Added
- Public release on GitHub

[unreleased]: https://github.com/aem-design/aemdesign-aem-core/compare/v2.1.0...HEAD
[2.0]: https://github.com/aem-design/aemdesign-aem-core/releases/tag/2.0
