package br.gov.inep.censo.util;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Testes unitarios do mapeamento de campos dinamicos de request.
 */
public class RequestFieldMapperTest {

    @Test
    public void deveMapearApenasCamposComplementaresValidos() {
        Map<String, String> parametros = new LinkedHashMap<String, String>();
        parametros.put("extra_10", "  valor 10  ");
        parametros.put("extra_11", " ");
        parametros.put("extra_abc", "invalido");
        parametros.put("nome", "ignorar");

        HttpServletRequest request = newRequest(parametros);
        Map<Long, String> resultado = RequestFieldMapper.mapCamposComplementares(request);

        Assert.assertEquals(1, resultado.size());
        Assert.assertEquals("valor 10", resultado.get(Long.valueOf(10L)));
    }

    @Test
    public void deveMapearIdsSelecionadosSomenteQuandoNumericos() {
        long[] ids = RequestFieldMapper.mapSelectedIds(new String[]{"1", "2", "x", "", "3"});
        Assert.assertArrayEquals(new long[]{1L, 2L, 3L}, ids);
    }

    private HttpServletRequest newRequest(final Map<String, String> parametros) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        String nomeMetodo = method.getName();
                        if ("getParameterNames".equals(nomeMetodo)) {
                            Vector<String> keys = new Vector<String>(parametros.keySet());
                            Enumeration<String> enumeration = keys.elements();
                            return enumeration;
                        }
                        if ("getParameter".equals(nomeMetodo) && args != null && args.length == 1) {
                            return parametros.get(String.valueOf(args[0]));
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
        return null;
    }
}
