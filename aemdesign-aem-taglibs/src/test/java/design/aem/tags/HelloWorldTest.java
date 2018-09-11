package design.aem.tags;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.jsp.JspException;

public class HelloWorldTest {


    @Test
    public void testDoStartTag() throws Exception{
        try{
            MockServletContext mockServletContext = new MockServletContext();
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockPageContext mockPageContext= new MockPageContext(mockServletContext, request);

            HelloWorld tag = new HelloWorld();
            tag.setJspContext(mockPageContext);
            tag.doTag();


            assert("Hello World".equals(request.getAttribute("helloworld")));
        }catch(JspException je){
            assert(false);
        }
    }

}