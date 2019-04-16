<%!

    /**
     * Checks whether a jcr node is a content block we want to be processing
     *
     * @param childNode is the childnode
     * @return true if this is a content block component
     * @throws RepositoryException
     */
    private boolean isContentBlockComponent(Node childNode) throws RepositoryException {

        return
            (childNode.hasProperty("sling:resourceType") &&
                childNode.getProperty("sling:resourceType").getString().endsWith("contentblock") ) ||
            (childNode.hasProperty("sling:resourceType") &&
                childNode.getProperty("sling:resourceType").getString().endsWith("contentblocklock") );

    }

    /**
     * Return true if this content block node should be shown in the content menu. This
     * is indicated by the avialability of the hideInMenu property. If it's not there, it should
     * be visible, if it is set to true, it should be visible, otherwise hide it.
     *
     * @param childNode is the content block child node to inspect
     * @return true if the content block's title should be shown in the menu
     *
     * @throws RepositoryException when something weird happens retrieving the properties.
     */
    private boolean isVisibleInMenu(Node childNode) throws RepositoryException {
        return
            // not been set? it's visible
            !childNode.hasProperty(FIELD_HIDEINMENU) ||
            // set to true? it's visible.
            "true".equals(childNode.getProperty(FIELD_HIDEINMENU).getString());
    }

    /**
     * Get the content block menu for page <code>page</code>
     *
     * @param parSys is the Resource to
     * @return a sequenced map of the content block anchor names and their titles
     * @throws RepositoryException
     */
    private Map<String, String> getContentBlockMenu(Resource parSys) throws RepositoryException {
        Map<String, String> contentMenu = new LinkedHashMap<String, String>();

        if (parSys != null) {

            Node contentResourceNode = parSys.adaptTo(Node.class);
            NodeIterator nodeIterator = contentResourceNode.getNodes();

            // iterate over children
            if (nodeIterator != null) {

                while (nodeIterator.hasNext()) {

                    Node childNode = nodeIterator.nextNode();
                    if (childNode == null) {
                        continue;
                    }

                    if (isContentBlockComponent(childNode) && isVisibleInMenu(childNode)) {
                        String childTitle = childNode.getName();
                        String childName = childNode.getName();

                        if (childNode.hasProperty(FIELD_STYLE_COMPONENT_ID)) {
                            String componentId = childNode.getProperty(FIELD_STYLE_COMPONENT_ID).getString();
                            if (isNotEmpty(componentId)) {
                                childName = componentId;
                            }
                        }

                        if (childNode.hasProperty("title")) {
                            childTitle = childNode.getProperty("title").getString();
                            if (isEmpty(childTitle)) {
                                childTitle = childName;
                            }
                        }
                        contentMenu.put( childName, childTitle );

                    }
                }

                return contentMenu;
            }
        }

        return contentMenu;
    }

%>