package br.gov.inep.censo.web.filter;

import br.gov.inep.censo.util.CsrfTokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Testes de comportamento do filtro CSRF.
 */
public class CsrfFilterTest {

    private CsrfFilter csrfFilter;

    @Before
    public void setUp() {
        csrfFilter = new CsrfFilter();
    }

    @Test
    public void devePermitirGetSemToken() throws Exception {
        final boolean[] chainCalled = new boolean[1];
        HttpServletRequest request = newRequest("GET", null, null, null);
        HttpServletResponse response = newResponse(new int[1]);
        FilterChain chain = newChain(chainCalled);

        csrfFilter.doFilter(request, response, chain);

        Assert.assertTrue(chainCalled[0]);
    }

    @Test
    public void deveBloquearPostSemToken() throws Exception {
        final boolean[] chainCalled = new boolean[1];
        final int[] statusHolder = new int[1];

        HttpSession session = newSession(null);
        HttpServletRequest request = newRequest("POST", null, null, session);
        HttpServletResponse response = newResponse(statusHolder);
        FilterChain chain = newChain(chainCalled);

        csrfFilter.doFilter(request, response, chain);

        Assert.assertFalse(chainCalled[0]);
        Assert.assertEquals(HttpServletResponse.SC_FORBIDDEN, statusHolder[0]);
    }

    @Test
    public void devePermitirPostComTokenValido() throws Exception {
        final boolean[] chainCalled = new boolean[1];
        final int[] statusHolder = new int[1];
        String token = "token-valido";

        HttpSession session = newSession(token);
        HttpServletRequest request = newRequest("POST", CsrfTokenUtil.PARAM_NAME, token, session);
        HttpServletResponse response = newResponse(statusHolder);
        FilterChain chain = newChain(chainCalled);

        csrfFilter.doFilter(request, response, chain);

        Assert.assertTrue(chainCalled[0]);
        Assert.assertEquals(0, statusHolder[0]);
    }

    private HttpServletRequest newRequest(final String method,
                                          final String paramName,
                                          final String paramValue,
                                          final HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method m, Object[] args) {
                        String name = m.getName();
                        if ("getMethod".equals(name)) {
                            return method;
                        }
                        if ("getSession".equals(name)) {
                            if (args != null && args.length == 1 && Boolean.FALSE.equals(args[0])) {
                                return session;
                            }
                            return session;
                        }
                        if ("getParameter".equals(name) && args != null && args.length == 1) {
                            if (paramName != null && paramName.equals(String.valueOf(args[0]))) {
                                return paramValue;
                            }
                        }
                        return defaultValue(m.getReturnType());
                    }
                });
    }

    private HttpSession newSession(final String token) {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class[]{HttpSession.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method m, Object[] args) {
                        if ("getAttribute".equals(m.getName()) && args != null && args.length == 1) {
                            if (CsrfTokenUtil.SESSION_KEY.equals(String.valueOf(args[0]))) {
                                return token;
                            }
                        }
                        return defaultValue(m.getReturnType());
                    }
                });
    }

    private HttpServletResponse newResponse(final int[] statusHolder) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class[]{HttpServletResponse.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("sendError".equals(method.getName()) && args != null && args.length > 0) {
                            statusHolder[0] = ((Integer) args[0]).intValue();
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

