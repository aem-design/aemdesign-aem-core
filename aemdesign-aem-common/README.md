#AEM Design Common Components

This a content package project generated using the multimodule-content-package-archetype.

##Branches

Master      - has all components that
Dev         - has all dev changes ready to be merged into master
Research    - has all the R&D code from other projects prepared to merge into Dev branch

##Component Naming Convention

Some conventions for component creation

* all component should be located in their functional authoring category
* all component names should be lowercase
* all component names should only be alphanumeric
* all "Details" components should have a suffix of "-details", this is used for component search reference
* all "List" components should have a suffix "list", this is used only as a name convention

##Component Attributes Naming Convention

Please follow following conventions for naming attriutes

* all attributes should be in camelCase

##Building

Use the deploy script to deploy into AEM.Design VM

```bash
./deploy
```

or

Use the deploy script to deploy into local AEM instance

```bash
./deploy-local
```


##Maven Archetype Command

This command was used to generate this project:

mvn archetype:generate \
    -DarchetypeRepository=http://repo.adobe.com/nexus/content/groups/public/ \
    -DarchetypeGroupId=com.day.jcr.vault \
    -DarchetypeArtifactId=multimodule-content-package-archetype \
    -DarchetypeVersion=1.0.2 \
    -DgroupId=training-aem \
    -DartifactId=aemdesign-aem-common \
    -Dversion=1.0-SNAPSHOT \
    -Dpackage=aemdesign \
    -DappsFolderName=aemdesign-common \
    -DartifactName="AEM Design Commom Components" \
    -DcqVersion="6.3.0" \
    -DpackageGroup="AEM.Design"

Please NOTE: additions updates to all 3 POMs were

## Fav Icons

Using AEM Core Page implementation you can create following icon files in your /etc/design for them to be autmaticaly loaded into html head
```
favicon.ico
favicon_32.png
touch-icon_60.png
touch-icon_76.png
touch-icon_120.png
touch-icon_152.png
```

You can generate the icons un this site [https://realfavicongenerator.net/](https://realfavicongenerator.net/) select the "Use Old Package" option.


## Debug JSP

login into your vm ```./devops accesslocal``` and run

```bash
docker exec -it author bash

cd /aem/crx-quickstart/launchpad/felix/bundle473/data/classes/org/apache/jsp/apps/aemdesign/components/lists/list

awk 'FNR>=6130 && FNR<=6130' list_jsp.java
```

## Manually monitor logs

login into your vm ```./devops accesslocal``` and run

```bash
docker exec -it author tail -f crx-quickstart/logs/error.log
```

# Showcase Template

For consistency of testing and validation each page in showcase should be similar to others. Existing test template are tailored to match template structure described in this section.

What your showcase page should contain:

* a ```layout/article``` container for page contents
    * a ```details/page-details``` component with info about showcase page
    * a ```layout/contentblockmenu``` to show all component variants in showcase
    * a ```layout/contentblock``` with info about  variant with a sequential name to ensure news variants cant be added easily


Please use following as a template for Component showcase page.

WARNING: Please DO NOT commit auto generated content, only commit what you expect to be there for testing.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
          jcr:primaryType="cq:Page">
    <jcr:content
            cq:tags="[aemdesign:content-type/page]"
            cq:template="/conf/aemdesign/settings/wcm/templates/twocolumn"
            jcr:primaryType="cq:PageContent"
            jcr:title="Nav List"
            sling:resourceType="aemdesign/components/template/base">
        <article
                jcr:primaryType="nt:unstructured"
                sling:resourceType="/apps/aemdesign/components/layout/article">
            <par
                    jcr:primaryType="nt:unstructured"
                    sling:resourceType="aemdesign/components/layout/container">
                <page-details
                        jcr:primaryType="nt:unstructured"
                        sling:resourceType="aemdesign/components/details/page-details"/>
                <contentblockmenu1
                        jcr:primaryType="nt:unstructured"
                        sling:resourceType="aemdesign/components/layout/contentblockmenu"
                        componentId="contentblockmenu1"/>
                <contentblock1
                        jcr:primaryType="nt:unstructured"
                        sling:resourceType="aemdesign/components/layout/contentblock"
                        componentId="contentblock1"
                        hideTitle="false"
                        hideTitleSeparator="true"
                        title="Default Fixed List"
                        variant="advsection">
                    <par
                            jcr:primaryType="nt:unstructured"
                            sling:resourceType="aemdesign/components/layout/container">

                        <!-- YOUR COMPONENT GOES HERE -->

                    </par>
                </contentblock1>

            </par>
        </article>
    </jcr:content>
</jcr:root>

```
