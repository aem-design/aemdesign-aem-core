# Analytics Data Layer

Analytics Data Layer component

## Overview

Component that outputs analytics content into the page

### Information
* **Vendor**: [AEM.Design](http://aem.design)
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Showcase**: [/content/aemdesign-showcase/en/component/analytics/datalayer.html](/content/aemdesign-showcase/en/analytics/analytics/datalayer.html?wcmmode=disabled)
* **Local Code**: /crx/de/#/apps/aemdesign/components/content/link
* **Source**: [gitlab/aemdesign](https://gitlab.com/aem.design/aemdesign-aem-common/tree/master/src/main/content/jcr_root/apps/aemdesign/components/analytics/datalayer)

### Features
* Gathers analytics content from

## Authoring

Following section covers authoring features

## Fields

This component has the following variants

<table style="border-spacing: 1px;border-collapse: separate;width: 100.0%;text-align: left;background-color: black; text-indent: 4px;">
    <thead style="background-color: white;font-size: larger;">
        <tr>
            <th style="width: 8%;">Name</th>
            <th>Description</th>
            <th>Authorable</th>
        </tr>
    </thead>
    <tbody style="background-color: #b0e0e6;">
        <tr>
            <td>digitalData.page.pageInfo.pageName</td>
            <td>page name derived from path</td>
            <td>yes, override accepted form Page Details</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.pageType</td>
            <td>category of page</td>
            <td>yes, override accepted form Page Details</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.effectiveDate</td>
            <td>date page was published</td>
            <td>no</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.contentLanguage</td>
            <td>language of current page</td>
            <td>no, derived from translation</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.contentCountry</td>
            <td>country of current page</td>
            <td>no, derived from translation</td>
        </tr>
        <tr>
            <td>digitalData.page.attributes.detailsMissing</td>
            <td>does page have Page Details component</td>
            <td>no, checks if page has a Page Details component in primary container</td>
        </tr>
        <tr>
            <td>digitalData.page.attributes.platform</td>
            <td>platform to send to analytics</td>
            <td>yes, override accepted form Page Details</td>
        </tr>
        <tr>
            <td>digitalData.page.attributes.abort</td>
            <td>do not execute analytics on this page</td>
            <td>yes, override accepted form Page Details</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.referringURL</td>
            <td>referring url</td>
            <td>no</td>
        </tr>
        <tr>
            <td>digitalData.page.pageInfo.destinationUrl</td>
            <td>referring url</td>
            <td>no</td>
        </tr>
        <tr>
            <td>digitalData.page.attributes.breakPoint</td>
            <td>current breakpoint of the page</td>
            <td>no</td>
        </tr>
        <tr>
            <td>digitalData.event</td>
            <td>page events</td>
            <td>no</td>
        </tr>
        <tr>
            <td>digitalData.error</td>
            <td>page errors</td>
            <td>no</td>
        </tr>
    </tbody>
</table>
<p></p>