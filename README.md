# aemdesign-aem-core

## Git Describe

To test git versioning run

`git describe`

this will show you generated version message with latest tag and how many commits since tag.

If you get an error:

`fatal: No annotated tags can describe`

create or overwrite an existing tag with a message:

`git tag 1.0 1.0^{} -f -m "initial tag"`

## Version Convention
Version numbers should follow semver format:

 * MAJOR version when you make incompatible API changes,
 * MINOR version when you add functionality in a backwards-compatible manner, and
 * PATCH version when you make backwards-compatible bug fixes.

Please use MAJOR and MINOR version in Tags, PATCH version will be automatically added as a commit count since the last tag using the git describe.

## Version Meaning
Version `1.0.3-SNAPSHOT` means that current checkout has uncommitted changes
Version `1.0.3` means that current checkout does not have uncommitted changes and is 3 commits ahead of the tag `1.0`

## Minimal core artifacts required for providing overridable AEM components.
 
`aemdesign-aem-core-deploy` module creates an aem package for deployment which contains:
 * `aemdesign-aem-author`
 * `amedesign-aem-common`
 
`aemdesign-aem-common` module embeds bundles:
 * `aemdesign-aem-services` 
 * `aemdesign-aem-taglibs` 
 
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
    * `mvn versions:commit -Pdeploymentpackage`
 5. Commit the updated version numbers to the release branch.
    * `mvn scm:checkin -Dmessage="updating version numbers"`

#### Release new version
Once the testing cycle has been completed and all code fixes have been applied to the remote release branch, we create a git tag of our version and deploy the maven `aemdesign-aem-core` artifact to the remote maven repository and merge our release to master branch.
 1. Ensure we are on the release branch for [aemdesign-aem-core](https://gitlab.com/aem.design/aemdesign-aem-core).
 2. Raise a Gitlab Merge Request from the relase branch to master branch, adding the necessary reviewers.
 3. Create the git tag.
    * `mvn scm:tag -Dtag="<version>"`
 4. Deploy the maven release artifacts to the remote maven repository
    * `<ToDo>`
 5. Accept the [aemdesign-aem-core](https://gitlab.com/aem.design/aemdesign-aem-core) Merge Request and delete the release branch.
 6. Update the `Release history` section in this readme with details of the new release.
  
 
## Release history
| Version    | Release Date   | Features                                   |
| :--------- | :------------- | :----------------------------------------- |
| 1.0        | 12/09/2018     | Initial release after refactor             |
|            |                |                                            |
|            |                |                                            |
|            |                |                                            |
