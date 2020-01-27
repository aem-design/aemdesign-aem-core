package design.aem.utils.components;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

import static design.aem.utils.components.ConstantsUtil.WCM_AUTHORING_MODE_COOKIE;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ParagraphUtilTest {

    @Mock
    JspWriter out;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    Cookie cookie;

    @Before
    public void before() {
        initMocks(this);
    }

    @Test
    public void testCloseRow_Success_WithClearFix() throws IOException {
        ParagraphUtil.closeRow(out, true);
        verify(out, times(2)).write("</div>");
        verify(out).write("<div style=\"clear:both\"></div>");
    }

    @Test
    public void testCloseRow_Success_WithoutClearFix() throws IOException {
        ParagraphUtil.closeRow(out, false);
        verify(out, times(2)).write("</div>");
        verify(out, never()).write("<div style=\"clear:both\"></div>");
    }

    @Test
    public void testUIMode_Success() {
        when(slingHttpServletRequest.getCookie(WCM_AUTHORING_MODE_COOKIE)).thenReturn(cookie);
        when(cookie.getValue()).thenReturn("TOUCH");
        String actualValue = ParagraphUtil.uiMode(slingHttpServletRequest);
        Assert.assertEquals("TOUCH", actualValue);
    }
}
