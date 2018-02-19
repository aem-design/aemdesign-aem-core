package com.isobar.adobe.aemdesign.common;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class HelloWorld extends SimpleTagSupport {

    public static final String ATTRIBUTE_HELLOWORLD = "helloworld";

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().setAttribute(ATTRIBUTE_HELLOWORLD, getHelloWorld(), PageContext.REQUEST_SCOPE);
    }

    public String getHelloWorld() {
        return "Hello World";
    }
}