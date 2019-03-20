Contact Details
===============

Contact Details component

# Overview

Component for adding Contact metadata to a page and Badge config info which is used for rendering in Contact Lists.

## Information
* **Vendor**: [AEM.Design](http://aem.design)
* **Version**: v1
* **Compatibility**: AEM 6.4
* **Status**: production-ready
* **Showcase**: [/content/aemdesign-showcase/en/component/details/contact-details](/content/aemdesign-showcase/en/component/details/contact-details.html?wcmmode=disabled)
* **Local Code**: [/apps/aemdesign/components/details/contact-details/v2/contact-details](/crx/de/#/apps/aemdesign/components/details/contact-details/v2/contact-details)
* **Source**: [gitlab/aemdesign](https://gitlab.com/aem.design/aemdesign-aem-common/tree/master/src/main/content/jcr_root/apps/aemdesign/components/details/contact-details/v2/contact-details)
* **Readme**: [/apps/aemdesign/components/details/contact-details/v2/contact-details](/mnt/overlay/wcm/core/content/sites/components/details.html/apps/aemdesign/components/details/contact-details/v2/contact-details)


## Features
* Provides mechanism to add contact details and badge config
* Provides consistent Contact Page Banner experience

# Authoring

Following section covers authoring features

## Edit Dialog properties

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
            <td>Details / Title</td>
            <td>Honorific Prefix</td>
            <td>honorificPrefix</td>
            <td></td>
            <td>An honorific prefix preceding a Person's name such as Dr/Mrs/Mr.</td>
        </tr>
        <tr>
            <td>Details / Title</td>
            <td>First Name</td>
            <td>givenName</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Title</td>
            <td>Last Name</td>
            <td>familyName</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Title</td>
            <td>Title Format</td>
            <td>titleFormat</td>
            <td>${honorificPrefix} ${givenName} ${familyName}</td>
            <td>Format of Title to be used for display.<br>Configure templates in <a href="/libs/cq/tagging/gui/content/tags.html/content/cq:tags/aemdesign/component-style-theme/details/contact-details/format/title">component-style-theme/details/contact-details/format/title</a></td>
        </tr>
        <tr>
            <td>Details / Description</td>
            <td>Job Title</td>
            <td>jobTitle</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Description</td>
            <td>Email</td>
            <td>email</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Description</td>
            <td>Employee</td>
            <td>employee</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Description</td>
            <td>Description Format</td>
            <td>descriptionFormat</td>
            <td></td>
            <td>Format of Description to be used for display.<br>Configure templates in <a href="/libs/cq/tagging/gui/content/tags.html/content/cq:tags/aemdesign/component-style-theme/details/contact-details/format/description">component-style-theme/details/contact-details/format/description</a></td>
        </tr>
        <tr>
            <td>Details / Description</td>
            <td>Hide Description</td>
            <td>hideDescription</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Details / Other</td>
            <td>Category</td>
            <td>cq:tags</td>
            <td></td>
            <td>Tags</td>
        </tr>
        <tr>
            <td>Layout / Variant</td>
            <td>Variant</td>
            <td>variant</td>
            <td></td>
            <td>defines which variant to use</td>
        </tr>
        <tr>
            <td>Layout / Display Options</td>
            <td>Show Breadcrumb</td>
            <td>showBreadcrumb</td>
            <td></td>
            <td>show breadcrumb above title</td>
        </tr>
        <tr>
            <td>Layout / Display Options</td>
            <td>Show Toolbar</td>
            <td>showToolbar</td>
            <td></td>
            <td>show toolbar above title</td>
        </tr>
        <tr>
            <td>Layout / Display Options</td>
            <td>Show Parsys</td>
            <td>showParsys</td>
            <td></td>
            <td>show parsys after title</td>
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


## Design Dialog Tabs

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
            <td>Analytics</td>
            <td>Include: Global Analytics Tab</td>
            <td></td>
            <td></td>
            <td>common analytics options</td>
        </tr>
    </tbody>
</table>

# Variants

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
            <td>show basic Contact details with Image, Title and Description config for Breadcrumb, Toolbar and parsys</td>
        </tr>
        <tr>
            <td>Hidden</td>
            <td>hide variant on a page</td>
        </tr>
    </tbody>
</table>


# RoadMap

Following is example schema that should be used as reference for future design, source <a href="http://schema.org/Person">http://schema.org/Person</a>.

    <div itemscope itemtype="http://schema.org/Person">
        <span itemprop="name">Jane Doe</span>
        <img src="janedoe.jpg" itemprop="image" alt="Photo of Jane Joe"/>
        <span itemprop="jobTitle">Professor</span>
        <div itemprop="address" itemscope itemtype="http://schema.org/PostalAddress">
        <span itemprop="streetAddress">
          20341 Whitworth Institute
          405 N. Whitworth
        </span>
            <span itemprop="addressLocality">Seattle</span>,
            <span itemprop="addressRegion">WA</span>
            <span itemprop="postalCode">98052</span>
        </div>
        <span itemprop="telephone">(425) 123-4567</span>
        <a href="mailto:jane-doe@xyz.edu" itemprop="email">
            jane-doe@xyz.edu</a>
        Jane's home page:
        <a href="http://www.janedoe.com" itemprop="url">janedoe.com</a>
        Graduate students:
        <a href="http://www.xyz.edu/students/alicejones.html" itemprop="colleague">
            Alice Jones</a>
        <a href="http://www.xyz.edu/students/bobsmith.html" itemprop="colleague">
            Bob Smith</a>
    </div>

<p></p>