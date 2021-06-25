News Details
============

News Details component

# Overview

Component for adding content metadata to a News and Badge config info which is used for rendering in Page Lists.

## Information
* **Vendor**: [AEM.Design](http://aem.design)
* **Version**: v1
* **Compatibility**: AEM 6.4+
* **Status**: production-ready
* **Showcase**: [/content/aemdesign-showcase/en/component/details/news-details](/content/aemdesign-showcase/en/component/details/news-details.html?wcmmode=disabled)
* **Local Code**: [/apps/aemdesign/components/details/news-details/v2/news-details](/crx/de/#/apps/aemdesign/components/details/news-details/v2/news-details)
* **Source**: [github/aem-design](https://github.com/aem-design/aemdesign-aem-common/tree/master/src/main/content/jcr_root/apps/aemdesign/components/details/news-details/v2/news-details)
* **Readme**: [/apps/aemdesign/components/details/news-details/v2/news-details](/mnt/overlay/wcm/core/content/sites/components/details.html/apps/aemdesign/components/details/news-details/v2/news-details)

## Features
* Provides mechanism to add page details and badge config
* Provides consistent Page Banner experience

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
            <td><em>a</em> tag with component attributes</td>
        </tr>
    </tbody>
</table>

# ISO 8601 duration format

Time Required field uses ISO 8601 duration format and allows representing schema for an article or a page.

Source: [ISO 8601 duration format](https://www.digi.com/resources/documentation/digidocs/90001437-13/reference/r_iso_8601_duration_format.htm)

ISO 8601 Durations are expressed using the following format, where (n) is replaced by the value for each of the date and time elements that follow the (n):

  `P(n)Y(n)M(n)DT(n)H(n)M(n)S`

Where:

- `P` is the duration designator (referred to as "period"), and is always placed at the beginning of the duration.
- `Y` is the year designator that follows the value for the number of years.
- `M` is the month designator that follows the value for the number of months.
- `W` is the week designator that follows the value for the number of weeks.
- `D` is the day designator that follows the value for the number of days.
- `T` is the time designator that precedes the time components.
- `H` is the hour designator that follows the value for the number of hours.
- `M` is the minute designator that follows the value for the number of minutes.
- `S` is the second designator that follows the value for the number of seconds.

For example:

- `P3Y6M4DT12H30M5S` - Represents a duration of three years, six months, four days, twelve hours, thirty minutes, and five seconds.
- `PT1M` - Represents a duration of time required to complete something which is one minute
- `PT1H1M` - Represents a duration of time required to complete something which is one hour and one minute

<p></p>
