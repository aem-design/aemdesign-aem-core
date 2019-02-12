Link
============

Link component

## Overview

Component for creating links (calls to action) on a page.
Call to Acton can be a Link or a Button.

### Information
* **Vendor**: [AEM.Design](http://aem.design)
* **Version**: v1
* **Compatibility**: AEM 6.4
* **Status**: production-ready
* **Showcase**: [/content/aemdesign-showcase/en/component/content/link](/content/aemdesign-showcase/en/component/content/link.html?wcmmode=disabled)
* **Local Code**: [/apps/aemdesign/components/content/link](/crx/de/#/apps/aemdesign/components/content/link)
* **Source**: [gitlab/aemdesign](https://gitlab.com/aem.design/aemdesign-aem-core/tree/master/aemdesign-aem-common/src/main/content/jcr_root/apps/aemdesign/components/content/link)
* **Readme**: [/apps/aemdesign/components/content/link](/mnt/overlay/wcm/core/content/sites/components/details.html/apps/aemdesign/components/content/link)

### Features
* Provides support for anchor and button elements

## Authoring

Following section covers authoring features

### Edit Dialog properties

These fields are available for input by the authors. These fields are used in templates

<table style="border-spacing: 1px;border-collapse: separate;width: 100.0%;text-align: left;background-color: black; text-indent: 4px;">
    <thead style="background-color: white;font-size: larger;">
        <tr>
            <th style="width: 8%;">Tab</th>
            <th style="width: 14%;">Field Title</th>
            <th style="width: 8%;">Field Name</th>
            <th style="width: 8%;">Default Value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody style="background-color: #b0e0e6;">
        <tr>
            <td>Content</td>
            <td>Label</td>
            <td>label</td>
            <td></td>
            <td>defines the text displayed</td>
        </tr>
        <tr>
            <td></td>
            <td>Target URL</td>
            <td>linkUrl</td>
            <td>#</td>
            <td>defines link to be used</td>
        </tr>
        <tr>
            <td></td>
            <td>Link Target</td>
            <td>hrefTarget</td>
            <td></td>
            <td>defines how the link click behaviour should work</td>
        </tr>
        <tr>
            <td></td>
            <td>Variant</td>
            <td>variant</td>
            <td>anchor</td>
            <td>defines which variant to use</td>
        </tr>
        <tr>
            <td>Style</td>
            <td>Include: Global Style Tab</td>
            <td></td>
            <td></td>
            <td>common style options</td>
        </tr>
        <tr>
            <td>Accessibility</td>
            <td>Include: Global Accessibility Tab</td>
            <td></td>
            <td></td>
            <td>common accessibility options</td>
        </tr>
        <tr>
            <td>Link Attributes</td>
            <td>Include: Global Link Attributes Tab</td>
            <td></td>
            <td></td>
            <td>common link attributes</td>
        </tr>
        <tr>
            <td>Analytics</td>
            <td>Include: Global Analytics Tab</td>
            <td></td>
            <td></td>
            <td>common analytics options</td>
        </tr>
    </tbody>
</table>


### Design Dialog Tabs

This enables creating of default values for components to be use in templates

<table style="border-spacing: 1px;border-collapse: separate;width: 100.0%;text-align: left;background-color: black; text-indent: 4px;">
    <thead style="background-color: white;font-size: larger;">
        <tr>
            <th style="width: 8%;">Tab</th>
            <th style="width: 14%;">Field Title</th>
            <th style="width: 8%;">Field Name</th>
            <th style="width: 8%;">Default Value</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody style="background-color: #b0e0e6;">
        <tr>
            <td>Content</td>
            <td>Includes: Content</td>
            <td></td>
            <td></td>
            <td>includes Content tab form Edit Dialog</td>
        </tr>
        <tr>
            <td>Style</td>
            <td>Include: Global Style Tab</td>
            <td></td>
            <td></td>
            <td>common style options</td>
        </tr>
        <tr>
            <td>Accessibility</td>
            <td>Include: Global Accessibility Tab</td>
            <td></td>
            <td></td>
            <td>common accessibility options</td>
        </tr>
        <tr>
            <td>Link Attributes</td>
            <td>Include: Global Link Attributes Tab</td>
            <td></td>
            <td></td>
            <td>common link attributes</td>
        </tr>
        <tr>
            <td>Analytics</td>
            <td>Include: Global Analytics Tab</td>
            <td></td>
            <td></td>
            <td>common analytics options</td>
        </tr>
    </tbody>
</table>

## Variants

This component has the following variants

<table style="border-spacing: 1px;border-collapse: separate;width: 100.0%;text-align: left;background-color: black; text-indent: 4px;">
    <thead style="background-color: white;font-size: larger;">
        <tr>
            <th style="width: 8%;">Name</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody style="background-color: #b0e0e6;">
        <tr>
            <td>Default</td>
            <td><em>a</em> tag with component attributes</td>
        </tr>
        <tr>
            <td>Button</td>
            <td><em>button</em> tag with component attributes</td>
        </tr>
    </tbody>
</table>




<p></p>