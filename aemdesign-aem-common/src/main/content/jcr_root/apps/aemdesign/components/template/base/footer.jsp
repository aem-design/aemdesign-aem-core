<%@include file="/apps/aemdesign/global/global.jsp" %>
<cq:include path="footer" resourceType="aemdesign/components/admin/footer"/>


<%--
This is the HIJACK to hide the footer
<script>
    $(document).ready(function () {
        if(window.location.href.indexOf("show=false") > -1) {
            $("a").attr("href", function(i, href) {
                return href + '?show=false';
            });

            $('div.header.sub.cars .colctrl-3c-c2').hide();

        }
    });
</script>
--%>