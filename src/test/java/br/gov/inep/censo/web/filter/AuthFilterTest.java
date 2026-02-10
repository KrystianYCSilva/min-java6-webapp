package br.gov.inep.censo.web.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Testes de comportamento do filtro de autenticacao sem dependencias de mocking framework.
 */
public class AuthFilterTest {

    private AuthFilter authFilter;

    @Before
    public void setUp() throws Exception {
        authFilter = new AuthFilter();
        authFilter.init(newFilterConfig("/login"));
    }

    @Test
    public void deveBloquearAcessoSemSessao() throws Exception {
        final String[] redirectHolder = new String[1];
        final boolean[] chainCalled = new boolean[1];

        HttpServletRequest request = newRequest("/censo", null);
        HttpServletResponse response = newResponse(redirectHolder);
        FilterChain chain = newChain(chainCalled);

        authFilter.doFilter(request, response, chain);

        Assert.assertEquals("/censo/login", redirectHolder[0]);
        Assert.assertFalse(chainCalled[0]);
    }

    @Test
    public void devePermitirAcessoQuandoAutenticado() throws Exception {
        final String[] redirectHolder = new String[1];
        final boolean[] chainCalled = new boolean[1];

        HttpSession session = newSessionWithUser();
        HttpServletRequest request = newRequest("/censo", session);
        HttpServletResponse response = newResponse(redirectHolder);
        FilterChain chain = newChain(chainCalled);

        authFilter.doFilter(request, response, chain);

        Assert.assertTrue(chainCalled[0]);
        Assert.assertNull(redirectHolder[0]);
    }

    private FilterConfig newFilterConfig(final String loginPath) {
        return (FilterConfig) Proxy.newProxyInstance(
                FilterConfig.class.getClassLoader(),
                new Class[]{FilterConfig.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getInitParameter".equals(method.getName())) {
                            return loginPath;
                        }
                        return null;
                    }
                });
    }

    private HttpServletRequest newRequest(final String contextPath, final HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        String name = method.getName();
                        if ("getSession".equals(name)) {
                            if (args != null && args.length == 1 && Boolean.FALSE.equals(args[0])) {
                                return session;
                            }
                            return session;
                        }
                        if ("getContextPath".equals(name)) {
                            return contextPath;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private HttpServletResponse newResponse(final String[] redirectHolder) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class[]{HttpServletResponse.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("sendRedirect".equals(method.getName())) {
                            redirectHolder[0] = (String) args[0];
                            return null;
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private FilterChain newChain(final boolean[] called) {
        return (FilterChain) Proxy.newProxyInstance(
                FilterChain.class.getClassLoader(),
                new Class[]{FilterChain.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("doFilter".equals(method.getName())) {
                            called[0] = true;
                        }
                        return null;
                    }
                });
    }

    private HttpSession newSessionWithUser() {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class[]{HttpSession.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getAttribute".equals(method.getName()) && args != null && args.length == 1) {
                            if ("usuarioLogado".equals(String.valueOf(args[0]))) {
                                return new Object();
                            }
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private Object defaultValue(Class returnType) {
        if (returnType == null || returnType.equals(Void.TYPE)) {
            return null;
        }
        if (returnType.equals(Boolean.TYPE)) {
            return Boolean.FALSE;
        }
        if (returnType.equals(Integer.TYPE)) {
            return Integer.valueOf(0);
        }
        if (returnType.equals(Long.TYPE)) {
            return Long.valueOf(0L);
        }
        if (returnType.equals(Double.TYPE)) {
            return Double.valueOf(0D);
        }
        if (returnType.equals(Float.TYPE)) {
            return Float.valueOf(0F);
        }
        if (returnType.equals(Short.TYPE)) {
            return Short.valueOf((short) 0);
        }
        if (returnType.equals(Byte.TYPE)) {
            return Byte.valueOf((byte) 0);
        }
        if (returnType.equals(Character.TYPE)) {
            return Character.valueOf('\0');
        }
        return null;
    }
}
