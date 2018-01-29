AEM Design Common Components
============================

This a content package project generated using the multimodule-content-package-archetype.

Component Naming Convention
---------------------------

Some conventions for component creation

* all component should be located in their functional authoring category
* all component names should be lowercase
* all component names should only be alphanumeric
* all "Details" components should have a suffix of "-details", this is used for component search reference
* all "List" components should have a suffix "list", this is used only as a name convention

Component Attributes Naming Convention
--------------------------------------

Please follow following conventions for naming attriutes

* all attributes should be in camelCase

Building
--------

Use the deploy script to deploy into AEM.Design VM

```bash
./deploy
```

or

Use the deploy script to deploy into local AEM instance

```bash
./deploy-local
```


Maven Archetype Command
-----------------------

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

# Fav Icons

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