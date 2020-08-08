<%@include file="/libs/granite/ui/global.jsp" %>
<%@page session="false"
        import="java.util.Iterator,
                com.adobe.granite.ui.components.AttrBuilder,
                com.adobe.granite.ui.components.Config,
                com.adobe.granite.ui.components.Tag" %><%--###
Accordion
=========

.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/accordion

   The accordion.

   It has the following content structure:

   .. gnd:gnd::

      [granite:Accordion] > granite:commonAttrs, granite:renderCondition, granite:container

      /**
       * Whether multiple items can be opened at the same time.
       */
      - multiple (Boolean)

      /**
       * The variant of the accordion.
       *
       * default
       *    The default look and feel.
       * quiet
       *    Quiet variant with minimal borders.
       * large
       *    Large variant, typically used inside a navigation rail since it does not have borders on the sides.
       */
      - variant (String) = 'default' < 'default', 'quiet', 'large'

      /**
       * Put vertical margin to the root element.
       */
      - margin (Boolean)

   Each item at least has the following content structure:

   .. gnd:gnd::

      [granite:AccordionItem]

      /**
       * The item title.
       */
      - jcr:title (String) mandatory i18n

      /**
       * The item specific config for the parent.
       */
      + parentConfig (granite:AccordionItemParentconfig)


      [granite:AccordionItemParentconfig]

      /**
       * ``true`` to open the item initially; ``false`` otherwise.
       */
      - active (Boolean)

      /**
       * ``true`` to disable the item; ``false`` otherwise.
       */
      - disabled (Boolean)

   Example::

      + myaccordion
        - sling:resourceType = "granite/ui/components/coral/foundation/accordion"
        + items
          + item1
            - jcr:title = "Item 1"
          + item2
            - jcr:title = "Item 2"
            + parentConfig
              - active = true
###--%>
<%

  if (!cmp.getRenderCondition(resource, false).check()) {
    return;
  }

  Config cfg = cmp.getConfig();

  Tag tag = cmp.consumeTag();

  AttrBuilder attrs = tag.getAttrs();
  cmp.populateCommonAttrs(attrs);
  attrs.addMultiple(cfg.get("multiple", false));
  attrs.add("variant", cfg.get("variant", String.class));

  if (cfg.get("margin", false)) {
    attrs.addClass("foundation-layout-util-vmargin");
  }

%>
<coral-accordion <%= attrs.build() %>><%
  for (Iterator<Resource> items = cmp.getItemDataSource().iterator(); items.hasNext(); ) {
    Resource item = items.next();
    Config itemCfg = new Config(item);


    AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);

    Resource parentConfig = item.getChild("parentConfig");

    if (parentConfig != null) {
      Config parentConfigCfg = new Config(parentConfig);

      itemAttrs.addSelected(parentConfigCfg.get("active", false));
      itemAttrs.addBoolean("disabled", parentConfigCfg.get("disabled", false));
    }

    //add config
    itemAttrs.addClass(itemCfg.get("class", ""));
    itemAttrs.add("data-showhidetargetvalue", itemCfg.get("showhidetargetvalue", ""));

%>
  <coral-accordion-item <%= itemAttrs %>>
    <coral-accordion-item-label><%= outVar(xssAPI, i18n, itemCfg.get("jcr:title", String.class)) %>
    </coral-accordion-item-label>
    <coral-accordion-item-content>
      <sling:include resource="<%= item %>"/>
    </coral-accordion-item-content>
  </coral-accordion-item>
  <%
    }
  %></coral-accordion>
