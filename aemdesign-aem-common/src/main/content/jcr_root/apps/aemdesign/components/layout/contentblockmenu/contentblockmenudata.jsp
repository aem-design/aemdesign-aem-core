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
            childNode.getName().startsWith("contentblock") &&
            childNode.hasProperty("sling:resourceType") &&
            childNode.getProperty("sling:resourceType").getString().endsWith("contentblock");

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
                childNode.hasProperty("title") &&
                (
                    // not been set? it's visible
                    !childNode.hasProperty("hideInMenu") ||
                    // set to true? it's visible.
                    "true".equals(childNode.getProperty("hideInMenu").getString())
                );
    }

    /**
     * Get the content block menu for page <code>page</code>
     *
     * @param parSys is the Resource to
     * @return a sequenced map of the content block anchor names and their titles
     * @throws RepositoryException
     */
    private Map<String, String> getContentBlockMenu(Resource parSys) throws RepositoryException {



        if (parSys != null) {

            Node contentResourceNode = parSys.adaptTo(Node.class);
            NodeIterator nodeIterator = contentResourceNode.getNodes();

            // iterate over children
            if (nodeIterator != null) {

                // ordered map
                Map<String, String> contentMenu = new LinkedHashMap<String, String>();

                while (nodeIterator.hasNext()) {

                    Node childNode = nodeIterator.nextNode();
                    if (childNode == null) {
                        continue;
                    }

                    if (isContentBlockComponent(childNode) && isVisibleInMenu(childNode)) {
                        String childTitle = childNode.getProperty("title").getString();

                        // make sure the title has something inside
                        if (childTitle != null && !childTitle.trim().equals("")) {
                            contentMenu.put(
                                    childNode.getName(),
                                    escapeBody(childNode.getProperty("title").getString())
                            );
                        }

                    }
                }

                return contentMenu;
            }
        }

        return null;
    }

%>