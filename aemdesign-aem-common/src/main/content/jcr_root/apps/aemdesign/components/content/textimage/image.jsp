<div class="img-wrapper">
    <c:choose>
        <c:when test="<%= StringUtils.isNotBlank(caption) %>">
            <div id="<%= divId %>" class="imageWithCaption">
                <div class="img-container">
                    <div><% image.draw(out); %></div>
                    <span class="caption"><%= escapeBody(caption) %></span>
                    <cq:text property="image/jcr:description" placeholder="" tagName="small" escapeXml="true"/>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div id="<%= divId %>" class="imageWithCaption">
                <% image.draw(out); %>
                <cq:text property="image/jcr:description" placeholder="" tagName="small" escapeXml="true"/>
            </div>
        </c:otherwise>
    </c:choose>
</div>

