<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.Property" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="javax.jcr.Value" %>
<%@ page import="org.apache.sling.api.scripting.SlingScriptHelper" %>
<%@ page import="java.util.*" %>
<%!

    final String TAG_VALUE = "value";
    final String TAG_ISDEFAULT = "isdefault";
    final String TAG_ISDEFAULT_VALUE = "false";

    //private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Create a formatted localized string that contains all tags in the tagProperty
     *
     * @param tagManager
     * @param tagPaths
     * @param locale
     * @return
     */
    private String getTags(TagManager tagManager, String[] tagPaths, Locale locale) {

        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        // convert from tag path into tag titles
        List<String> tags = new ArrayList<String>();
        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                if (locale == null){
                    tags.add(jcrTag.getTitle());
                }else{
                    tags.add(jcrTag.getLocalizedTitle(locale));
                }
            }
        }

        // concat them into a string
        int idx = 0;
        StringBuilder builder = new StringBuilder();
        for (String tag : tags) {
            builder.append(tag);
            ++idx;
            if (idx != tags.size()) {
                builder.append(", ");
            }
        }

        // return buffer
        return builder.toString();
    }

    /**
     * Create a formatted string that contains all tags in the tagProperty
     *
     * @param tagPaths is the property that contains all tags
     * @return a formatted string
     */
    private String getTags(TagManager tagManager, String[] tagPaths) {
        return getTags(tagManager, tagPaths, null);
    }

    /**
     * Get tag values from a JCR node
     * @param tagManager
     * @param thisNode
     * @param tagPropertyName
     * @return List<Tag>
     */
    private List<Tag> getTags(TagManager tagManager, Node thisNode, String tagPropertyName) throws RepositoryException {
        if (tagManager==null || thisNode==null) {
            return null;
        }

        Value[] pageTagValues = null;
        if (thisNode.hasProperty(tagPropertyName)) {
            if (thisNode.getProperty(tagPropertyName).isMultiple()) {
                pageTagValues = thisNode.getProperty(tagPropertyName).getValues();
            } else {
                pageTagValues = new Value[1];
                pageTagValues[0] = thisNode.getProperty(tagPropertyName).getValue();
            }
        }

        if (pageTagValues == null || pageTagValues.length == 0) {
            return null;
        }

        List<Tag> tags = new ArrayList<Tag>();
        for (Value tagValue : pageTagValues) {
            if (tagValue != null) {
                String path = tagValue.getString();
                if (path != null) {
                    Tag jcrTag = tagManager.resolve(path);
                    if (jcrTag != null) {
                        tags.add(jcrTag);
                    }
                }
            }
        }
        return tags;
    }


    /**
     * Get tag values from a JCR node
     * @param tagManager
     * @param valueMap
     * @param tagPropertyName
     * @return List<Tag>
     */
    private Map<String, Tag> getTagsMap(TagManager tagManager, InheritanceValueMap valueMap, String tagPropertyName, Boolean tryInherit) throws RepositoryException {
        Value[] pageTagValues = null;
        String[] tagStrings = null;
        Map<String, Tag> tags = new HashMap<String, Tag>();

        if (tryInherit) {
            tagStrings = valueMap.get(tagPropertyName, valueMap.getInherited(tagPropertyName, new String[0]));
        } else {
            tagStrings = valueMap.get(tagPropertyName, new String[0]);
        }


        if (tagStrings == null || tagStrings.length == 0) {
            return tags;
        }

        for (String tagValue : tagStrings) {
            if (isNotEmpty(tagValue)) {
                Tag jcrTag = tagManager.resolve(tagValue);
                if (jcrTag != null) {
                    tags.put(jcrTag.getTagID(), jcrTag);
                }
            }
        }
        return tags;
    }

    /**
     * Get tag values from a JCR node
     * @param tagManager
     * @param thisNode
     * @param tagPropertyName
     * @return List<Tag>
     */
    private List<Node> getTagsAsNodes(TagManager tagManager, Node thisNode, String tagPropertyName) throws RepositoryException {
        Value[] pageTagValues = null;
        if (thisNode.hasProperty(tagPropertyName)) {
            pageTagValues = thisNode.getProperty(tagPropertyName).getValues();
        }

        if (pageTagValues == null || pageTagValues.length == 0) {
            return null;
        }

        List<Node> tags = new ArrayList<Node>();
        for (Value tagValue : pageTagValues) {
            if (tagValue != null) {
                String path = tagValue.getString();
                if (path != null) {
                    Tag jcrTag = tagManager.resolve(path);
                    if (jcrTag != null) {
                        tags.add(jcrTag.adaptTo(Node.class));
                    }
                }
            }
        }
        return tags;
    }

    /**
     * Get tag values from a JCR node
     * @param tagManager
     * @param thisNode
     * @param tagPropertyName
     * @param tagPath
     * @return boolean
     */
    private boolean containsTag(TagManager tagManager, Node thisNode, String tagPropertyName, String tagPath) throws RepositoryException {
        boolean containsTagFlag = false;
        Tag jcrTag = tagManager.resolve(tagPath);
        try {
            List<Tag> tags = this.getTags(tagManager, thisNode, tagPropertyName);
            if (jcrTag != null) {
                containsTagFlag = tags.contains(jcrTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containsTagFlag;
    }


    /**
     * Get tag values from a Page - Tags in page property
     * @param tagManager
     * @param thisPage
     * @param tagPath
     * @return boolean
     */
    private boolean containsTag(TagManager tagManager, Page thisPage, String tagPath) throws RepositoryException {
        boolean containsTagFlag = false;
        Tag jcrTag = tagManager.resolve(tagPath);
        try {
            Tag[] pageTags = thisPage.getTags();
            List<Tag> tags = Arrays.asList(pageTags);
            if (jcrTag != null) {
                containsTagFlag = tags.contains(jcrTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containsTagFlag;
    }


    /**
     * Get tags from a Page as a string - Tags in page property
     * @param thisPage
     * @return String
     */
    private String getTagAsString(Page thisPage) throws RepositoryException {
        String tagString = "";
        Tag[] pageTags = thisPage.getTags();
        List<Tag> tags = Arrays.asList(pageTags);
        tagString = tagString + tags.size();
        for (Tag curTag : pageTags) {
            if (curTag != null) {
                tagString = tagString + "," + curTag.getTagID();
            }
        }
        return tagString;
    }

    /**
     * Create a formatted string that contains all tags attached to the provided details node.
     * @param tagManager The TagManager used to resolve tag names to objects.
     * @param node The details node to get the information from.
     * @param tagName The tags property name to get the information from.
     * @param defaultValue The value to return when something goes wrong.
     * @return The list of tags, formatted correctly for ICAL presentation.
     */
    private String getTagsTitles(TagManager tagManager, Node node, String tagName, String defaultValue) {
        try {
            if (!node.hasProperty(tagName)) {
                return defaultValue;
            }

            Property tagProperty = node.getProperty(tagName);

            // has any contents?
            Value[] values = tagProperty.getValues();
            if (values == null || values.length == 0) {
                return defaultValue;
            }

            // get the tag names
            List<String> tagPaths = new ArrayList<String>();
            for (Value tagValue : values) {
                if (tagValue != null) {
                    tagPaths.add(tagValue.getString());
                }
            }

            return getTags(tagManager, tagPaths.toArray(new String[tagPaths.size()]));
        } catch (RepositoryException e) {
            return defaultValue;
        }
    }

    /***
     * get value of tag path as admin
     * @param tagPath
     * @param sling
     * @return
     */
    public String getTagValueAsAdmin(String tagPath, SlingScriptHelper sling) {
        String tagValue="";

        if (isEmpty(tagPath) || sling == null) {
            return tagValue;
        }

        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {
            TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

            Tag jcrTag = _adminTagManager.resolve(tagPath);

            if (jcrTag != null) {
                tagValue = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey("value")) {
                        tagValue = tagVM.get("value", jcrTag.getName());
                    }
                }
            }

        } catch (Exception ex) {
            Logger LOG = LoggerFactory.getLogger(getClass());
            LOG.error("getTagValueAsAdmin: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }

        return tagValue;
    }


    /**
     * Get list of Tags from a list of tag paths
     * @param sling
     * @param tagPaths
     * @param locale
     * @return
     * @throws RepositoryException
     */
    private LinkedHashMap<String, Map> getTagsAsAdmin(SlingScriptHelper sling, String[] tagPaths, Locale locale) throws RepositoryException {
        LinkedHashMap<String, Map> tags = new LinkedHashMap<>();

        if (sling == null || tagPaths == null || tagPaths.length == 0) {
            return tags;
        }


        ResourceResolver adminResourceResolver  = openAdminResourceResolver(sling);
        try {
            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            for (String path : tagPaths) {
                Map<String, String> tagValues = new HashMap<>();

                Tag tag = tagManager.resolve(path);
                if (tag != null) {
                    tagValues.put("title",tag.getTitle());
                    tagValues.put("description",tag.getDescription());
                    tagValues.put("path",tag.getPath());

                    ValueMap tagVM = tag.adaptTo(Resource.class).getValueMap();
                    String tagValue = tag.getName();

                    if (tagVM.containsKey(TAG_VALUE)) {
                        tagValue = tagVM.get(TAG_VALUE, tag.getName());
                    }

                    if (tagVM.containsKey(TAG_ISDEFAULT)) {
                        tagValue = tagVM.get(TAG_ISDEFAULT, TAG_ISDEFAULT_VALUE);
                    }

                    if (locale != null) {
                        String titleLocal = JcrConstants.JCR_TITLE.concat(".").concat(org.apache.jackrabbit.util.Text.escapeIllegalJcrChars(locale.toString().toLowerCase()));
                        if (tagVM.containsKey(titleLocal)) {
                            tagValues.put("title", tagVM.get(titleLocal, tag.getName()));
                        }
                        tagValues.put("tagid",tag.getLocalTagID());

                        String valueLocal = TAG_VALUE.concat(".").concat(org.apache.jackrabbit.util.Text.escapeIllegalJcrChars(locale.toString().toLowerCase()));
                        if (tagVM.containsKey(valueLocal)) {
                            tagValue = tagVM.get(valueLocal, tag.getName());
                        }
                    }

                    tagValues.put("value",tagValue);

                }
                tags.put(tag.getTagID(),tagValues);
            }

        } catch (Exception ex) {
            Logger LOG = LoggerFactory.getLogger(getClass());
            LOG.error("getTagValueAsAdmin: " + ex.getMessage(), ex);
        } finally {
            closeAdminResourceResolver(adminResourceResolver);
        }
        return tags;
    }


    /**
     * Get Tag values
     * @param tagManager
     * @param tagPaths
     * @return
     */

    public String getTagsAsValues(TagManager tagManager, String separator, String tagPaths[]) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        int idx = 0;
        StringBuilder builder = new StringBuilder();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey("value")) {
                        value = tagVM.get("value", jcrTag.getName());
                    }
                }

                builder.append(value);
                builder.append(separator);
            }
        }
        if (builder.length() > 0){
            builder.setLength(builder.length()-1);
        }

        // return buffer
        return builder.toString();
    }

    /**
     * Get Tag values
     * @param tagManager
     * @param tagPaths
     * @return
     */

    public String[] getTagsValues(TagManager tagManager, String separator, String tagPaths[]) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        int idx = 0;
        ArrayList<String> tagValues = new ArrayList<>();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey("value")) {
                        value = tagVM.get("value", jcrTag.getName());
                    }
                }
                tagValues.add(value);
            }
        }
        // return buffer
        return tagValues.toArray(new String[tagValues.size()]);
    }

    /**
     * Get Tag values
     * @param tagManager
     * @param tagPaths
     * @return
     */

    public String getTagsAsKeywords(TagManager tagManager, String separator, String tagPaths[], Locale locale) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }
        //TODO: maybe exclude some namespaces out of results?
        int idx = 0;
        StringBuilder builder = new StringBuilder();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                String title = jcrTag.getTitle(locale);

                if (isNotEmpty(title)) {
                    value = title;
                }

                builder.append(value);
                builder.append(separator);
            }
        }
        if (builder.length() > 0){
            builder.setLength(builder.length()-1);
        }

        // return buffer
        return builder.toString();
    }


    /**
     * return Tag Names as Class String
     * @param tag list of tags
     * @return
     */

    public String getTagsAsClasses(String tag[]) {
        String cssTagClass = "";

        for (int i = 0; i < tag.length; i++) {
            if (i > 0) {
                cssTagClass = cssTagClass + " ";
            }
            cssTagClass = cssTagClass + tag[i].substring(tag[i].lastIndexOf(":") + 1);
        }

        return cssTagClass;
    }


    /**
     * Returns the value of the property on the specified node with the specified tag
     * @param node is the node to inspect
     * @param tagName is the name of the tag
     * @param propertyName is the name of the property
     * @param defaultValue is the default value if the property is not found
     * @return the value of the property as a String
     */
    private String getPropertyFromTag(TagManager tagManager, Node node, String tagName, String propertyName, String defaultValue) {

        try {
            List<Tag> tagList = getTags(tagManager, node, tagName);
            if (tagList != null) {
                for (Tag tag : tagList) {
                    Node tagNode = tag.adaptTo(Node.class);
                    if (tagNode.hasProperty(propertyName)) {
                        return tagNode.getProperty(propertyName).getValue().getString();
                    }
                }
            }
            return defaultValue;
        } catch (RepositoryException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the property on the specified node with the specified tag
     * @param node is the node to inspect
     * @param tag is the tag
     * @return the value of the property as a String Array of the tag titles
     */

    private List<Tag> getChildTags(TagManager tagManager, Node node, Tag tag) {

        List<Tag> children = new ArrayList<Tag>();

        for (Iterator<Tag> tagList = tag.listChildren(); tagList.hasNext(); ) {
            children.add(tagList.next());
        }

        return children;

    }

%>