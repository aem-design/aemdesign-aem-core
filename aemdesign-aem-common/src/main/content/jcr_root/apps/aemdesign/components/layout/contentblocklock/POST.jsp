<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@page session="false"
        import="com.day.cq.commons.servlets.HtmlStatusResponseHelper,
                  org.apache.jackrabbit.util.Text,
                  org.apache.sling.api.servlets.HtmlResponse,
                  org.apache.sling.servlets.post.SlingPostConstants,
                  javax.jcr.PropertyType,
                  java.util.Enumeration,
                  java.util.Locale"%>
<%

    HtmlResponse htmlResponse = null;
    log.error("Content Block Lock POST");

    boolean update = false;

    try {
        String[] groups = _properties.get("groups", new String[]{"administrators"});
        Boolean islocked = _properties.get("islocked", false);
        log.error("Content Block Lock POST: islocked {}", islocked);
        log.error("Content Block Lock POST: groups {}", groups);
        if (islocked) {
            final Authorizable authorizable = _resourceResolver.adaptTo(Authorizable.class);
            final List<String> groupsList = Arrays.asList(groups);

            if (isUserMemberOf(authorizable, groupsList)) {
                log.error("Content Block Lock POST: ADMIN");

                update = true;
                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true, _i18n.get("This content block is locked for updates but you have access to update"), _resource.getPath());
            } else {
                log.error("Content Block Lock POST: LOCKED");
                htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false, _i18n.get("This content block is locked for updates."), _resource.getPath());
            }
        } else {
            log.error("Content Block Lock POST: NOT LOCKED");

            update = true;

            htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true, _i18n.get("This content block is not locked."), _resource.getPath());
        }


        if (update) {
            processDeletes(_currentNode, request);
            writeContent(_currentNode, request);

            List<String> tagsParameters = getTagRequestParameters(request);
            for (String name : tagsParameters) {
                List<String> processedTags = getProcessedTags(_tagManager, name, request);
                if (!processedTags.isEmpty()) {
                    Node finalNode = getParentNode(_currentNode, name);
                    String propertyName = getPropertyName(name);
                    finalNode.setProperty(propertyName, processedTags.toArray(new String[0]));
                }
            }

            _currentNode.getSession().save();
        }


    } catch (Exception e) {
        log.error("Error occur creating a new page", e);

        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false, _i18n.get("Cannot update content ({0})", null, e.getMessage()));
    }

    htmlResponse.send(response, true);
%><%!
    private String getAdminUrl(Page page) {
        String url = page.getVanityUrl();

        if (url == null) {
            ValueMap vm = page.getProperties();
            if (vm.containsKey("sling:vanityPath")) {
                url = page.getProperties().get("sling:vanityPath", String.class);
            }
        }

        if (url == null) {
            url = Text.escapePath(page.getPath());
        }

        return url + ".html";
    }

    /**
     * Copied from Sling. Later on Sling POST Servlet will be refactored to provide a generic service for this.
     */
    private void processDeletes(final Node parentNode, final HttpServletRequest req) throws Exception {
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();

            if (!name.startsWith("./")) continue;
            if (!name.endsWith(SlingPostConstants.SUFFIX_DELETE)) continue;

            if (parentNode.hasProperty(name)) {
                parentNode.getProperty(name).remove();
            } else if (parentNode.hasNode(name)) {
                parentNode.getNode(name).remove();
            }
        }
    }

    /**
     * Copied from Sling. Later on Sling POST Servlet will be refactored to provide a generic service for this.
     */
    private void writeContent(final Node parentNode, final HttpServletRequest req) throws Exception {
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();

            if (!name.startsWith("./")) continue;
            // ignore all tags, they are handled separately
            if (name.indexOf("cq:tags") > 0) continue;
            if (name.startsWith("jcr:primaryType")) continue;
            if (name.startsWith("jcr:mixinTypes")) continue;
            if (name.endsWith(SlingPostConstants.TYPE_HINT_SUFFIX)) continue;

            String[] values = req.getParameterValues(name);

            String typeHint = req.getParameter(name + SlingPostConstants.TYPE_HINT_SUFFIX);
            boolean multiple = false;

            if (typeHint != null && typeHint.endsWith("[]")) {
                typeHint = typeHint.substring(0, typeHint.length() - "[]".length());
                multiple = true;
            }

            int type = PropertyType.STRING;
            if (typeHint != null) {
                type = PropertyType.valueFromName(typeHint);
            }

            List<String> jcrValues = new ArrayList<String>();

            for (String value : values) {
                if (value.length() > 0) {
                    jcrValues.add(value);
                }
            }

            multiple = multiple || jcrValues.size() > 1;

            String n = getPropertyName(name);
            Node finalNode = getParentNode(parentNode, name);

            if (multiple) {
                finalNode.setProperty(n, jcrValues.toArray(new String[0]), type);
            } else if (!jcrValues.isEmpty()) {
                finalNode.setProperty(n, jcrValues.get(0), type);
            }
        }
    }

    private List<String> getProcessedTags(TagManager tagManager, String name, final HttpServletRequest req)
            throws Exception {
        List<String> processedTags = new ArrayList<String>();

        if (tagManager != null) {
            String[] tags = req.getParameterValues(name);

            if (tags != null) {
                for (String tagId : tags) {
                    if (tagId.length() == 0) continue;

                    if (tagId.indexOf(":") < 0) {
                        Tag tag = tagManager.createTagByTitle(tagId, Locale.ENGLISH); // This is fixed to "en" in old siteadmin also
                        tagId = tag.getTagID();
                    }

                    processedTags.add(tagId);
                }
            }
        }
        return processedTags;
    }

    /**
     * Find all cq:tags parameters as they have to be handled separated
     * @param req
     * @return
     * @throws Exception
     */
    private List<String> getTagRequestParameters(final HttpServletRequest req) throws Exception {
        List<String> tagsParameters = new ArrayList<String>();
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();
            if (name.endsWith("cq:tags")) {
                tagsParameters.add(name);
            }
        }
        return tagsParameters;
    }

    /**
     * Get property name for repository
     * @param name
     * @return
     * @throws Exception
     */
    private String getPropertyName(String name) throws Exception {
        if (name.startsWith("./")) {
            name = name.substring("./".length());

            if (name.indexOf("/") > -1) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
        }
        return name;
    }

    /**
     * Get the parent node
     * If the parent doesn't exist, create it
     * @param parentNode
     * @param name
     * @return
     * @throws Exception
     */
    private Node getParentNode(Node parentNode, String name) throws Exception {
        if (name.startsWith("./")) {
            name = name.substring("./".length());

            if (name.indexOf("/") > -1) {
                String relPath = name.substring(0, name.lastIndexOf("/"));
                if (parentNode.hasNode(relPath)) {
                    parentNode = parentNode.getNode(relPath);
                } else {
                    parentNode = JcrUtil.createPath(parentNode.getPath() + "/" + relPath, "nt:unstructured",
                            parentNode.getSession());
                }
            }
        }

        return parentNode;
    }
%>

