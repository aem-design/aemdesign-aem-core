<%@include file="/apps/aemdesign/global/global.jsp"%><%
%><%@page import="
                  org.apache.jackrabbit.util.Text,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.ExpressionCustomizer,
                  com.adobe.granite.ui.components.Field,
                  com.day.cq.tagging.TagManager"%><%--###
TagField
========

.. granite:servercomponent:: /libs/cq/gui/components/coral/common/form/tagfield
   :supertype: /libs/granite/ui/components/coral/foundation/form/field

   A field that allows the user to enter tag.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.

   It has the following content structure:

   .. gnd:gnd::

      [cq:FormTagField] > granite:FormField

      /**
       * The name that identifies the field when submitting the form.
       */
      - name (StringEL)

      /**
       * A hint to the user of what can be entered in the field.
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
       * Indicates if the user must only select from the list of given options.
       * If it is not forced, the user can enter arbitrary value.
       */
      - forceSelection (Boolean)

      /**
       * When ``forceSelection = false``, ``true`` to create the user defined tag during form submission; ``false`` otherwise.
       */
      - autocreateTag (Boolean)

      /**
       * ``true`` to generate the `SlingPostServlet @Delete <http://sling.apache.org/documentation/bundles/manipulating-content-the-slingpostservlet-servlets-post.html#delete>`_ hidden input based on the field name.
       */
      - deleteHint (Boolean) = true

      /**
       * The root path of the tags. Tag root home is known to tagmanager API only and it will handle accordingly
       */
      - rootPath (StringEL) = '/'
###--%><%

    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());
    Field field = new Field(cfg);


    ExpressionCustomizer expressionCustomizer = ExpressionCustomizer.from(request);

    TagManager tm = _resourceResolver.adaptTo(TagManager.class);

    Tenant tenant = _resourceResolver.adaptTo(Tenant.class);

    String requestSuffix = _slingRequest.getRequestPathInfo().getSuffix();

    if (tenant == null) {
        tenant = _resource.adaptTo(Tenant.class);

        if (tenant != null) {
            expressionCustomizer.setVariable("tenantId", tenant.getId());
            expressionCustomizer.setVariable("tenant", tenant);
        } else {
            String finalTenantId;
            if (isNotEmpty(requestSuffix)) {
                finalTenantId = resolveTenantIdFromPath(requestSuffix);
            } else {
                finalTenantId = resolveTenantIdFromPath(_resource.getPath());
            }
            if (isNotEmpty(finalTenantId)) {
                expressionCustomizer.setVariable("tenantId", finalTenantId);
            }

        }
    }

    ExpressionHelper ex = cmp.getExpressionHelper();
    TagManager tagManager = _resourceResolver.adaptTo(TagManager.class);

    final String[] values = vm.get("value", new String[0]);

    final String name = vm.get("name", String.class);

    String tagRootPath;
    if(tagManager != null) {
        String rootPath = ex.getString(cfg.get("rootPath", "/"));
        com.day.cq.tagging.Tag tagRoot = tagManager.resolve(rootPath);
        tagRootPath = tagRoot != null ? tagRoot.getPath() : "";
    } else {
        tagRootPath = ex.getString(cfg.get("rootPath", "/etc/tags"));
    }

    final boolean multiple = cfg.get("multiple", false);
    final boolean disabled = cfg.get("disabled", false);

    final String selectionCount = multiple ? "multiple" : "single";

    final String pickerSrc = "/mnt/overlay/cq/gui/content/coral/common/form/tagfield/picker.html?root=" + Text.escape(tagRootPath) + "&selectionCount=" + Text.escape(selectionCount);
    final String suggestionSrc = "/mnt/overlay/cq/gui/content/coral/common/form/tagfield/suggestion{.offset,limit}.html?root=" + Text.escape(tagRootPath) + "{&query}";
    boolean isMixed = Field.isMixed(cfg, cmp.getValue());

    String placeholder;
    if (isMixed) {
        placeholder = _i18n.get("<Mixed Entries>");
    } else {
        placeholder = _i18n.getVar(cfg.get("emptyText", String.class));
    }

    com.adobe.granite.ui.components.Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.addClass("cq-ui-tagfield");

    attrs.add("name", name);
    attrs.add("placeholder", placeholder);
    attrs.addDisabled(disabled);
    attrs.addBoolean("multiple", multiple);
    attrs.addBoolean("required", cfg.get("required", false));
    attrs.addBoolean("forceselection", cfg.get("forceSelection", false));
    attrs.addHref("pickersrc", pickerSrc);

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

    if (multiple) {
        attrs.add("valuedisplaymode", "block");
    }

    if (isMixed) {
        attrs.addClass("foundation-field-mixed");
    }

    attrs.add("data-foundation-validation", StringUtils.join(cfg.get("validation", new String[0]), " "));

    if (cfg.get("autocreateTag", false)) {
        attrs.addHref("data-cq-ui-tagfield-create-action", Text.escapePath(_resource.getPath()));
    }

    com.adobe.granite.xss.XSSAPI oldXssAPI = _slingRequest.adaptTo(com.adobe.granite.xss.XSSAPI.class);
    AttrBuilder suggestionAttrs = new AttrBuilder(request, oldXssAPI);
    suggestionAttrs.add("foundation-autocomplete-suggestion", "");
    suggestionAttrs.addClass("foundation-picker-buttonlist");
    suggestionAttrs.add("data-foundation-picker-buttonlist-src", request.getContextPath() + suggestionSrc);

    AttrBuilder valueAttrs = new AttrBuilder(request, oldXssAPI);
    valueAttrs.add("foundation-autocomplete-value", "");
    valueAttrs.add("name", name);

%><foundation-autocomplete <%= attrs %>>
    <coral-overlay <%= suggestionAttrs %>></coral-overlay>
    <coral-taglist <%= valueAttrs %>><%
        for (String value : values) {
            com.day.cq.tagging.Tag cqTag = tagManager.resolve(value);

            String text;
            if (cqTag == null) {
                text = value;
            } else {
                text = cqTag.getTitlePath(request.getLocale());
            }

            %><coral-tag multiline value="<%= _xssAPI.encodeForHTMLAttr(value) %>"><%= _xssAPI.encodeForHTML(text) %></coral-tag><%
        }
    %></coral-taglist><%

    if (!StringUtils.isBlank(name)) {
        if (multiple) {
            AttrBuilder typeHintAttrs = new AttrBuilder(request, oldXssAPI);
            typeHintAttrs.addClass("foundation-field-related");
            typeHintAttrs.add("type", "hidden");
            typeHintAttrs.add("name", name + "@TypeHint");
            typeHintAttrs.add("value", "String[]");
            typeHintAttrs.addDisabled(disabled);

            %><input <%= typeHintAttrs %>><%
        }

        if (isMixed) {
            AttrBuilder patchAttrs = new AttrBuilder(request, oldXssAPI);
            patchAttrs.addClass("foundation-field-related foundation-field-mixed-patchcontrol");
            patchAttrs.add("type", "hidden");
            patchAttrs.add("name", name + "@Patch");

            %><input <%= patchAttrs %>><%
        } else if (cfg.get("deleteHint", true)) {
            AttrBuilder deleteAttrs = new AttrBuilder(request, oldXssAPI);
            deleteAttrs.addClass("foundation-field-related");
            deleteAttrs.add("type", "hidden");
            deleteAttrs.add("name", name + "@Delete");

            %><input <%= deleteAttrs %>><%
        }
    }
    %><ui:includeClientLib categories="cq.ui.coral.common.tagfield" />
</foundation-autocomplete>