# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.1.0] - 2020-11-12

### Added
- `ComponentProperties` is now managed via `BaseComponent`
- Components that extend `BaseComponent` now have access to:
    - `setFields` - _Allows `setComponentFields` and `setAnalyticsFields` to be used and overridden without needing to override `ready`_
    - `setFieldDefaults` - _Allows defaults to be set via the `componentDefaults` property_
- Asset workflow for **webp** images
- AEM 6.5 stability improvements

### Changed
- Switched from the Adobe managed `AttrBuilder` class to a custom in-house managed version that uses the OGSi `XSSAPI`
- The Vue model property handler has been rewritten to be more extensible
- `ModelProxy` class has been renamed to `BaseComponent`

### Fixed
- Vue model no longer fails to render output when it comes across a JCR property it fails to understand
- Component decoration no longer overrides the original decoration defined

### Security
- Updated junit:junit to v4.13.1
- Updated com.google.guava:guava to v30.0-jre

## [2.0] - 10/05/2019

### Added
- Public release on GitHub

[unreleased]: https://github.com/aem-design/aemdesign-aem-core/compare/master...develop
[2.1.0]: https://github.com/aem-design/aemdesign-aem-core/releases/tag/v2.1.0
[2.0]: https://github.com/aem-design/aemdesign-aem-core/releases/tag/2.0
