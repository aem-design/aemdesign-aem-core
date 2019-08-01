# aemdesign-aem-core

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=design.aem%3Aaemdesign-aem-core&metric=alert_status)](https://sonarcloud.io/dashboard?id=design.aem%3Aaemdesign-aem-core)
[![build_status](https://travis-ci.org/aem-design/aemdesign-aem-core.svg?branch=master)](https://travis-ci.org/aem-design/aemdesign-aem-core)
[![codecov](https://codecov.io/gh/aem-design/aemdesign-aem-core/branch/master/graph/badge.svg?magic)](https://codecov.io/gh/aem-design/aemdesign-aem-core)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/design.aem/aemdesign-aem-common/badge.svg?magic)](https://maven-badges.herokuapp.com/maven-central/design.aem/aemdesign-aem-common)
[![github license](https://img.shields.io/github/license/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core) 
[![github issues](https://img.shields.io/github/issues/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core) 
[![github last commit](https://img.shields.io/github/last-commit/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core) 
[![github repo size](https://img.shields.io/github/repo-size/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core) 
[![github repo size](https://img.shields.io/github/languages/code-size/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core) 
[![github release](https://img.shields.io/github/release/aem-design/aemdesign-aem-core)](https://github.com/aem-design/aemdesign-aem-core)

A set of standardized components for AEM 6.4+ that can be used to speed up development of websites.

## Documentation

* [AEM.Design Blog](https://aem.design)
* [AEM.Design SonarCloud](https://sonarcloud.io/project/issues?id=design.aem%3Aaemdesign-aem-core)

## Sonar Quality Gate

After completing your commits and before PUSHING please run following command to push your updates to sonar.
Please cleanup your updates do not add items to Reliability, Security and Maintainability measures, please dont be a 💩 and cleanup your code 🙏😍

On Master branch run this

```bash
mvn sonar:sonar "-Dsonar.branch.name=master" "-Dsonar.host.url=https://sonarcloud.io" "-Dsonar.login=e565f767e9723a7e1b27e1c339cc24dc8ee87aaf" "-Dsonar.organization=aemdesign-github"
```

On other branches run this, change ``sonar.branch.name`` to your branch name

```bash
mvn sonar:sonar "-Dsonar.branch.name=develop" "-Dsonar.branch.target=master" "-Dsonar.host.url=https://sonarcloud.io" "-Dsonar.login=e565f767e9723a7e1b27e1c339cc24dc8ee87aaf" "-Dsonar.organization=aemdesign-github"
```

## Development
If you're curious about how the next generation of components looks like, a tech preview is made available in the
[`development`](https://github.com/aem-design/aemdesign-aem-core/tree/development) branch.

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Available Components

Detailed table of component can be found here [Component List](https://github.com/aem-design/aemdesign-aem-core/wiki/Component-List)

| Component Category                                              	| Link                                                                                                                                   	|
|-----------------------------------------------------------------	|----------------------------------------------------------------------------------------------------------------------------------------	|
| Cloud Services / Rest                                           	| [Rest](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/rest)                                                           	|
| Cloud Services / Salesforce API                                 	| [Salesforce API](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/salesforceapi)                                        	|
| Common / Redirection Notification                               	| [Redirection Notification](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/common/redirectnotification/v2/redirectnotification) 	|
| Common / Static Content Include                                 	| [Static Content Include](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/common/staticinclude/v2/staticinclude)                 	|
| Common / Timing Component                                       	| [Timing Component](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/common/timing/v2/timing)                                     	|
| Content / File Download Link                                    	| [File Download Link](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/download/v2/download)                              	|
| Content / Embed Source                                          	| [Embed Source](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/embedsource/v2/embedsource)                              	|
| Content / External                                              	| [External](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/external/v2/external)                                        	|
| Content / Link                                                  	| [Link](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/link/v2/link)                                                    	|
| Content / Content Reference                                     	| [Content Reference](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/reference/v2/reference)                             	|
| Content / Table                                                 	| [Table](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/table/v2/table)                                                 	|
| Content / Rich Text                                             	| [Rich Text](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/text/v2/text)                                               	|
| Content / Tool Tip                                              	| [Tool Tip](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/tooltip/v2/tooltip)                                          	|
| Content / Page Author                                           	| [Page Author](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/pageauthor/v2/pageauthor)                                 	|
| Content / Page Date                                             	| [Page Date](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/pagedate/v2/pagedate)                                       	|
| Content / Page Description                                      	| [Page Description](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/pagedescription/v2/pagedescription)                  	|
| Content / Page Tags                                             	| [Page Tags](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/pagetags/v2/pagetags)                                       	|
| Content / Page Title                                            	| [Page Title](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/pagetitle/v2/pagetitle)                                    	|
| Details / Contact Details                                       	| [Contact Details](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/details/contact-details/v2/contact-details)                   	|
| Details / Event Details                                         	| [Event Details](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/details/event-details/v2/event-details)                         	|
| Details / Location Details                                      	| [Location Details](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/details/location-details/v2/location-details)                	|
| Details / News Details                                          	| [News Details](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/details/news-details/v2/news-details)                            	|
| Details / Page Details                                          	| [Page Details](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/details/page-details/v2/page-details)                            	|
| Layout / Article                                                	| [Article](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/article/v2/article)                                            	|
| Layout / Aside                                                  	| [Aside](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/aside/v2/aside)                                                  	|
| Layout / Breadcrumb                                             	| [Breadcrumb](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/breadcrumb/v2/breadcrumb)                                   	|
| Layout / Columns                                                	| [Columns](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/colctrl/v2/colctrl)                                            	|
| Layout / Content Block                                          	| [Content Block](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/contentblock/v2/contentblock)                            	|
| Layout / Content Block Lock                                     	| [Content Block Lock](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/contentblocklock)                                   	|
| Layout / Content Block Menu                                     	| [Content Block Menu](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/contentblocklock/v2/contentblocklock)               	|
| Layout / Content Tabs                                           	| [Content Tabs](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/contenttabs/v2/contenttabs)                               	|
| Layout / Footer                                                 	| [Footer](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/footer/v2/footer)                                               	|
| Layout / Header                                                 	| [Header](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/header/v2/header)                                               	|
| Layout / Nav Bar                                                	| [Nav Bar](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/navbar/v2/navbar)                                              	|
| Layout / Container                                              	| [Container](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/layout/container)                                                   	|
| Lists / Asset List                                              	| [Asset List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/assetlist/v2/assetlist)                                      	|
| Lists / Contact List                                            	| [Contact List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/contactlist/v2/contactlist)                                	|
| Lists / Event List                                              	| [Event List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/eventlist/v2/eventlist)                                      	|
| Lists / Lang Nav                                                	| [Lang Nav](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/langnav/v2/langnav)                                            	|
| Lists / List                                                    	| [List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/list/v2/list)                                                      	|
| Lists / List Nav                                                	| [List Nav](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/listnav/v2/listnav)                                            	|
| Lists / Location List                                           	| [Location List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/locationlist/v2/locationlist)                             	|
| Lists / News List                                               	| [News List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/newslist/v2/newslist)                                         	|
| Lists / Page List                                               	| [Page List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/pagelist/v2/pagelist)                                         	|
| Lists / Search Results List                                     	| [Search Results List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/searchlist/v2/searchlist)                           	|
| Lists / Tag List                                                	| [Tag List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/taglist/v2/taglist)                                            	|
| Lists / Nav List                                                	| [Nav List](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/lists/navlist/v2/navlist)                                            	|
| Media / Audio                                                   	| [Audio](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/media/audio/v2/audio)                                                   	|
| Media / Image                                                   	| [Image](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/media/image/v2/image)                                                   	|
| Media / Video                                                   	| [Video](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/media/video/v2/video)                                                   	|
| Analytics / Analytics Data Layer                                	| [Analytics Data Layer](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/analytics/datalayer/v2/datalayer)                        	|
| Template / AEM.Design Base Page                                 	| [AEM.Design Base Page](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/base/v3/base)                                   	|
| Template / Cloud Config / AddThis                               	| [AddThis](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/cloudconfig/addthisconnect/config)                           	|
| Template / Cloud Config / Google Analytics                      	| [Google Analytics](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/cloudconfig/googleanalytics/config)                 	|
| Template / Cloud Config / Google Map                            	| [Google Map](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/cloudconfig/googlemap/config)                             	|
| Template / Column / AEM.Design One Column Page                  	| [AEM.Design One Column Page](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/column/one/v2/one)                        	|
| Template / Column / AEM.Design Three Column Page                	| [AEM.Design Three Column Page](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/column/three/v2/three)                  	|
| Template / Column / AEM.Design Two Column Page                  	| [AEM.Design Two Column Page](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/column/two/v2/two)                        	|
| Template / Common / Design Importer                             	| [Design Importer](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/common/design-importer)                              	|
| Template / Experience Fragment / AEM.Design Experience Fragment 	| [AEM.Design Experience Fragment](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/template/experience-fragments/base/v2/xfpage)  	|
| Widgets / AddThis / Addthis Button                              	| [Addthis Button](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/widgets/addthis/sharebutton/v2/sharebutton)                    	|
| Widgets / Online Media                                          	| [Online Media](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/widgets/onlinemedia/v2/onlinemedia)                              	|
| Widgets / Search Box                                            	| [Search Box](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/widgets/search/v2/search)                                          	|
| Workflow / Process Payload                                      	| [Process Payload](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/processpayload)                                      	|
| Workflow / Project Task Manager                                 	| [Project Task Manager](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/projecttaskmanager)                             	|
| Workflow / Project Update                                       	| [Project Update](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/projectupdate)                                        	|
| Workflow / Rest                                                 	| [Rest](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/rest)                                                           	|
| Workflow / Salesforce API                                       	| [Salesforce API](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/salesforceapi)                                        	|
| Workflow / Send Email                                           	| [Send Email](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/workflow/sendemail)                                                	|
| Coral / Common/Form / Tag Field                                 	| [Tag Field](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/coral/common/form/tagfield)                                         	|
| Coral / Datasources / Forms                                     	| [Forms](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/coral/datasources/forms)                                                	|
| Coral / Datasources / Tags                                      	| [Tags](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/coral/datasources/tags)                                                  	|
| Coral / Foundation / Accordion                                  	| [Accordion](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/coral/foundation/accordion)                                         	|
| Coral / Widgets/Form / Asset Options                            	| [Asset Options](tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/coral/widgets/form/assetoptions)                                	|

## Component Versioning

The components' versioning scheme is documented on the [AEM Core WCM Components' versioning policies](https://github.com/adobe/aem-core-wcm-components/wiki/Versioning-policies) wiki page.

## System Requirements

The latest version of the Core Components, require the below system requirements:

| AEM Version   | [2.0.0](https://github.com/aem-design/aemdesign-aem-core/tag/2.0.0) |
| -----         | ---   |
| 6.4           | yes   |
| 6.5           | yes   |

## Installation

To install everything, excluding examples, the [released aggregate package `aemdesign-aem-core-deploy`](https://github.com/aem-design/aemdesign-aem-core/releases) can be installed via the AEM Package Manager.

For more information about the Package Manager please have a look at [How to Work With Packages](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/package-manager.html) documentation page.

### Package Dependencies

Following packages are dependencies and should be installed on your AEM instance before installing AEM.Design:

- [ACS Twitter 1.0.0](https://github.com/Adobe-Consulting-Services/com.adobe.acs.bundles.twitter4j/releases/download/com.adobe.acs.bundles.twitter4j-1.0.0/com.adobe.acs.bundles.twitter4j-content-1.0.0.zip)
- [ACS Commons 4.0.0](https://github.com/Adobe-Consulting-Services/acs-aem-commons/releases/download/acs-aem-commons-4.0.0/acs-aem-commons-content-4.0.0.zip)
- [Core Components 2.3.2](https://github.com/adobe/aem-core-wcm-components/releases/download/core.wcm.components.reactor-2.3.2/core.wcm.components.all-2.3.2.zip)
- [Core Components Extension 1.0.12](https://github.com/adobe/aem-core-wcm-components/releases/download/core.wcm.components.reactor-2.3.2/core.wcm.components.extension-1.0.12.zip)
- [Netcentric Access Control Tool 2.3.2](http://repo1.maven.org/maven2/biz/netcentric/cq/tools/accesscontroltool/accesscontroltool-package/2.3.2/accesscontroltool-package-2.3.2.zip)
- [Adobe Vanity URL 1.0.2](https://www.adobeaemcloud.com/content/companies/public/adobe/packages/cq600/component/vanityurls-components/jcr%3acontent/package/file.res/vanityurls-components-1.0.2.zip)


## Build

The project has the following requirements:
* Java SE Development Kit 8 or Java SE Development Kit 11
* Apache Maven 3.3.1 or newer

For ease of build and installation the following profiles are provided:

 * ``installdeploymentpackage`` - installs the deploy package/bundle to an existing AEM author instance

You can use helper script for ease of local deployment

* ``deploy-local`` - deploy aemdesign-aem-core-deploy package to your local AEM instance running on port 4502
* ``deploy-local-publish`` - deploy aemdesign-aem-core-deploy package to your local AEM instance running on port 4503

### UberJar

This project relies on the AEM 6.4 cq-quickstart. This is publicly available on https://repo.adobe.com

For more details about the UberJar please head over to the
[How to Build AEM Projects using Apache Maven](https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/ht-projects-maven.html) documentation page.


## Include core components into your own project maven build

To add core components to your project, you will need to add it to your maven build.
The released version of the framework are available on the public maven repository at https://repo1.maven.org/maven2/design/aem/aemdesign-aem-core-deploy/ 

To include the deploy package into your own project's maven build using maven you can add the dependency to your pom.xml like this

 ```
 <dependency>
     <groupId>design.aem</groupId>
     <artifactId>aemdesign-aem-core-deploy</artifactId>
     <type>zip</type>
     <version>2.0.100</version>
 </dependency>
 ```

and then add this subpackage to your sub package section

```
 <subPackage>
     <groupId>design.aem</groupId>
     <artifactId>aemdesign-aem-core-deploy</artifactId>
     <filter>true</filter>
 </subPackage>
```

inside the configuration of the `content-package-maven-plugin`.

# Development Notes

Following section describes some of the development topics

## Release Versions

Release versions are automated and based on Tags and Commit count from Tag using the `git describe` command

To test git versioning run

`git describe`

this will show you generated version message with latest tag and how many commits since tag.

If you get an error:

`fatal: No annotated tags can describe`

create or overwrite an existing tag with a message:

`git tag 1.0 1.0^{} -f -m "initial tag"`

## Version Convention

Version numbers for Git Tags should follow semver format:

 * MAJOR version when you make incompatible API changes,
 * MINOR version when you add functionality in a backwards-compatible manner, and
 * PATCH version is automatically generated based on git commit count from last Tag

Please use MAJOR and MINOR version in Tags, PATCH version will be automatically added as a commit count since the last tag using the git describe.

## Minimal core artifacts required for providing overridable AEM components.
 
`aemdesign-aem-core-deploy` module creates an aem package for deployment which contains:
 * `aemdesign-aem-author`
 * `amedesign-aem-common`
 
`aemdesign-aem-common` module embeds bundles:
 * `aemdesign-aem-services` 
 
## To build
To ensure the project builds correctly locally run:

`mvn -Dvault.useProxy=false -DskipTests -e -U clean package`

## To deploy
To build and deploy the project to your local aem instance (default localhost:4502), in the project root run:

`./deploy-local`

## To create a release
Releases are managed via the maven plugins `versions-maven-plugin` and `maven-scm-plugin`

Version numbers should follow the [SemVer](https://semver.org/) convention.

### The release process
#### Prepare release branch
In preparation for a release, create a new git release branch from the current master snapshot branch:
 1. Create a new release branch.
    * `mvn scm:branch -Dbranch=release/<version> -Dmessage="creating release branch <version>"`
 2. Ensure you are on the new release branch.
    * `git checkout release/<version>`
 3. Update the maven `version` parameter.
    * `mvn versions:set -DnewVersion=<version>`
 4. Check the version number was correctly applied and confirm.
    * `mvn versions:commit`
 5. Commit the updated version numbers to the release branch.
    * `mvn scm:checkin -Dmessage="updating version numbers"`

#### Release new version
Once the testing cycle has been completed and all code fixes have been applied to the remote release branch, we create a git tag of our version and deploy the maven `aemdesign-aem-core` artifact to the remote maven repository and merge our release to master branch.
 1. Ensure we are on the release branch for [aemdesign-aem-core](https://github.com/aem-design/aemdesign-aem-core).
 2. Raise a Merge Request from the relase branch to master branch, adding the necessary reviewers.
 3. Create the git tag.
    * `mvn scm:tag -Dtag="<version>"`
 4. Deploy the maven release artifacts to the remote maven repository
    * `<ToDo>`
 5. Accept the [aemdesign-aem-core](https://github.com/aem-design/aemdesign-aem-core) Merge Request and delete the release branch.
 6. Update the `Release history` section in this readme with details of the new release.
  
 
## Release history

Please review [Versions](VERSIONS.md) for all version history

