<%--
  ADOBE CONFIDENTIAL
  ___________________

  Copyright 2015 Adobe
  All Rights Reserved.

  NOTICE: All information contained herein is, and remains
  the property of Adobe and its suppliers, if any. The intellectual
  and technical concepts contained herein are proprietary to Adobe
  and its suppliers and are protected by all applicable intellectual
  property laws, including trade secret and copyright laws.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe.
--%><%
%><%@ include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false"
           import="java.text.Collator,
                  javax.servlet.http.HttpServletRequest,
                  javax.servlet.jsp.JspWriter,
                  org.apache.commons.collections4.IteratorUtils,
                  org.apache.commons.lang3.StringUtils,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %>
<%@ page import="java.util.*" %><%--###
Select
======

.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/form/select
   :supertype: /libs/granite/ui/components/coral/foundation/form/field

   Select is a component to represent a concept of selection of some options.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.

   It has the following content structure:

   .. gnd:gnd::

      [granite:FormSelect] > granite:FormField, granite:container

      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)

      /**
       * The initial text to display when nothing is selected.
       */
      - emptyText (String) i18n

      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)

      /**
       * Indicates if the field is mandatory to be filled.
       */
      - required (Boolean)

      /**
       * The name of the validator to be applied. E.g. ``foundation.jcr.name``.
       * See :doc:`validation </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/validation/index>` in Granite UI.
       */
      - validation (String) multiple

      /**
       * Indicates if the user is able to select multiple selections.
       */
      - multiple (Boolean)

      /**
       * ``true`` to translate the options, ``false`` otherwise.
       */
      - translateOptions (Boolean) = true

      /**
       * ``true`` to sort the options based on the text, ``false`` otherwise.
       *
       * It is assumed that the options don't contain option group.
       */
      - ordered (Boolean) = false

      /**
       * ``true`` to also add an empty option; ``false`` otherwise.
       *
       * Empty option is an option having both value and text equal to empty string.
       */
      - emptyOption (Boolean) = false

      /**
       * The variant of the select.
       */
      - variant (String) = 'default' < 'default', 'quiet'

      /**
       * ``true`` to generate the `SlingPostServlet @Delete <http://sling.apache.org/documentation/bundles/manipulating-content-the-slingpostservlet-servlets-post.html#delete>`_ hidden input based on the field name.
       */
      - deleteHint (Boolean) = true

      /**
       * ``true`` to force to be ``ignore-freshness`` specifically just for this field.
       *
       * This property is useful when you have a newly introduced field in the form, and there is a need to specifically set the default selected item.
       * To set the default selected item, set the ``selected`` property of the item as usual.
       *
       * See ``nameNotFoundMode`` property of :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form>`.
       */
      - forceIgnoreFreshness (Boolean)

   Each option has the following structure:

   .. gnd:gnd::

      [granite:FormSelectItem] > granite:commonAttrs, granite:renderCondition

      /**
       * The value of the option.
       */
      - value (StringEL) mandatory

      /**
       * Indicates if the option is in disabled state.
       */
      - disabled (Boolean)

      /**
       * ``true`` to pre-select this option, ``false`` otherwise.
       */
      - selected (Boolean)

      /**
       * The text of the option.
       */
      - text (String) i18n

      /**
       * The icon of the option.
       */
      - icon (String)

      /**
       * The icon describing the status of the option.
       */
      - statusIcon (String)

      /**
       * The text describing the status.
       * It is RECOMMENDED that it is specified when ``statusIcon`` is also specified for a11y purpose.
       */
      - statusText (String) i18n

      /**
       * The style of the status icon.
       */
      - statusVariant (String) < 'error', 'warning', 'success', 'help', 'info'

###--%><%

  Config cfg = cmp.getConfig();
  ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

  String name = cfg.get("name", String.class);
  final boolean disabled = cfg.get("disabled", false);
  final boolean forceIgnoreFreshness = cfg.get("forceIgnoreFreshness", false);

  Iterator<Resource> itemIterator = cmp.getItemDataSource().iterator();

  if (cfg.get("ordered", false)) {
    List<Resource> items = IteratorUtils.toList(itemIterator);
    final Collator langCollator = Collator.getInstance(request.getLocale());

    Collections.sort(items, new Comparator<Resource>() {
      public int compare(Resource o1, Resource o2) {
        return langCollator.compare(getOptionText(o1, cmp), getOptionText(o2, cmp));
      }
    });

    itemIterator = items.iterator();
  }

  Tag tag = cmp.consumeTag();
  AttrBuilder attrs = tag.getAttrs();
  cmp.populateCommonAttrs(attrs);

  attrs.add("name", name);
  attrs.addMultiple(cfg.get("multiple", false));
  attrs.addDisabled(disabled);
  attrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
  attrs.addBoolean("required", cfg.get("required", false));
  attrs.add("variant", cfg.get("variant", String.class));

  String fieldLabel = cfg.get("fieldLabel", String.class);
  String fieldDesc = cfg.get("fieldDescription", String.class);
  String labelledBy = null;

  if (fieldLabel != null && fieldDesc != null) {
    labelledBy = vm.get("labelId", String.class) + " " + vm.get("descriptionId", String.class);
  } else if (fieldLabel != null) {
    labelledBy = vm.get("labelId", String.class);
  } else if (fieldDesc != null) {
    labelledBy = vm.get("descriptionId", String.class);
  }

  if (StringUtils.isNotBlank(labelledBy)) {
    attrs.add("labelledby", labelledBy);
  }

  String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
  attrs.add("data-foundation-validation", validation);
  attrs.add("data-validation", validation); // Compatibility

%><coral-select <%= attrs.build() %>><%
  if (cfg.get("emptyOption", false)) {
    String value = "";

    AttrBuilder opAttrs = new AttrBuilder(request, xssAPI);
    opAttrs.add("value", value);
    opAttrs.addSelected(cmp.getValue().isSelected(value, false, forceIgnoreFreshness));

    out.println("<coral-select-item " + opAttrs.build() + "></coral-select-item>");
  }

  for (Iterator<Resource> items = itemIterator; items.hasNext();) {
    printOption(request, out, items.next(), cmp, forceIgnoreFreshness);
  }

  if (!StringUtils.isBlank(name) && cfg.get("deleteHint", true)) {
    AttrBuilder deleteAttrs = new AttrBuilder(request, xssAPI);
    deleteAttrs.addClass("foundation-field-related");
    deleteAttrs.add("type", "hidden");
    deleteAttrs.add("name", name + "@Delete");
    deleteAttrs.addDisabled(disabled);

%><input <%= deleteAttrs %>><%
  }
%></coral-select><%!

  private void printOption(HttpServletRequest request, JspWriter out, Resource option, ComponentHelper cmp, boolean forceIgnoreFreshness) throws Exception {
    if (!cmp.getRenderCondition(option, false).check()) {
      return;
    }
    I18n i18n = cmp.getI18n();
    XSSAPI xss = cmp.getXss();

    Config optionCfg = new Config(option);
    String value = cmp.getExpressionHelper().getString(optionCfg.get("value", String.class));
    String valuealt = cmp.getExpressionHelper().getString(optionCfg.get("valueAlt", String.class));

    AttrBuilder opAttrs = new AttrBuilder(request, cmp.getXss());
    cmp.populateCommonAttrs(opAttrs, option);

    opAttrs.add("value", value);
    if (!Objects.equals(valuealt, "")) {
      opAttrs.add("data-valuealt", valuealt);
    }
    opAttrs.addDisabled(optionCfg.get("disabled", false));

    // if the item is an optgroup, render the <optgroup> and all its containing items
    if (optionCfg.get("group", false)) {
      opAttrs.add("label", i18n.getVar(optionCfg.get("text", String.class)));

      out.println("<coral-select-group> " + opAttrs.build() + ">");
      for (Iterator<Resource> options = option.listChildren(); options.hasNext();) {
        printOption(request, out, options.next(), cmp, forceIgnoreFreshness);
      }
      out.println("</coral-select-group>");
    } else {
      // otherwise, render the <option>
      opAttrs.addSelected(cmp.getValue().isSelected(value, optionCfg.get("selected", false), forceIgnoreFreshness));

      out.print("<coral-select-item " + opAttrs.build() + ">");

      printIcon(request, out, optionCfg.get("icon", String.class), cmp);
      // we print it first due to float right
      printStatusIcon(request, out, optionCfg, cmp);
      out.print(xss.encodeForHTML(getOptionText(option, cmp)));

      out.println("</coral-select-item>");
    }
  }

  private void printIcon(HttpServletRequest request, JspWriter out, String icon, ComponentHelper cmp) throws Exception {
    if (icon == null || "".equals(icon)) return;

    AttrBuilder attrs = new AttrBuilder(request, cmp.getXss());
    attrs.add("icon", icon);

    out.print("<coral-icon " + attrs + "></coral-icon>");
  }

  private void printStatusIcon(HttpServletRequest request, JspWriter out, Config cfg, ComponentHelper cmp) throws Exception {
    String icon = cfg.get("statusIcon", "");

    if ("".equals(icon)) return;

    AttrBuilder attrs = new AttrBuilder(request, cmp.getXss());
    attrs.add("icon", icon);
    attrs.add("aria-label", cfg.get("statusText", String.class));

    attrs.addClass("granite-Select-statusIcon");

    String statusVariant = cfg.get("statusVariant", "");
    if (!"".equals(statusVariant)) {
      attrs.addClass("granite-Select-statusIcon--" + statusVariant);
    }

    out.print("<coral-icon " + attrs + "></coral-icon>");
  }

  private String getOptionText(Resource option, ComponentHelper cmp) {
    Config optionCfg = new Config(option);
    String text = optionCfg.get("text", "");

    if (cmp.getConfig().get("translateOptions", true)) {
      text = cmp.getI18n().getVar(text);
    }

    return text;
  }
%>
