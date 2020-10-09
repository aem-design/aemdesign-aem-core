package design.aem.utils.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class InvocationProxyFactory {
    private InvocationProxyFactory() {
        // Does nothing, move on =)
    }

    public static Map<String, String> create(InvocationHandler invocationHandler) {
        // noinspection unchecked
        return (Map<String, String>) Proxy.newProxyInstance(
            InvocationProxyFactory.class.getClassLoader(),
            new Class[] { Map.class },
            invocationHandler);
    }
}
