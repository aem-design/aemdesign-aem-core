<%@ page import="com.day.cq.wcm.commons.ResourceIterator" %>
<%@ page import="com.day.cq.wcm.api.components.Component" %><%!


    /**
     * Get the text elements for the page, which represent the hover points
     *
     * @param page is the page to
     * @return a sequenced map of the content block anchor names and their titles
     * @throws RepositoryException
     */
    private Map<String, String> getContentText(Resource resource) throws RepositoryException {


        Resource parSys = resource.getChild("par");


        Map<String, String> contentMenu = new HashMap<String, String>();

        if (parSys != null) {

            Iterator<Resource> children = parSys.listChildren();

            if (children != null){

                while (children.hasNext()) {


                    Resource child  = children.next();
                    ValueMap childProperties = child.adaptTo(ValueMap.class);

                    String childTitle = childProperties.get("text","");

                    // make sure the title has something inside
                    if (isNotEmpty(childTitle))
                    {

                        String childPosX = childProperties.get("positionX","0");
                        String childPosY = childProperties.get("positionY","0");

                        contentMenu.put(
                                escapeBody(childTitle),
                                "left:"+childPosX+"px;top:"+childPosY+"px;"
                        );
                    }
                }


            }

        }

        return contentMenu;

    }

%>