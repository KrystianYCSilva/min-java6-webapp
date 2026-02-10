package br.gov.inep.censo.util;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilitario para mapear campos dinamicos de formulario.
 */
public final class RequestFieldMapper {

    private RequestFieldMapper() {
    }

    /**
     * Converte parametros com prefixo {@code extra_} para mapa de ID do campo no leiaute.
     *
     * @param request request atual
     * @return mapa de valores complementares
     */
    public static Map<Long, String> mapCamposComplementares(HttpServletRequest request) {
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        java.util.Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = String.valueOf(names.nextElement());
            if (!name.startsWith("extra_")) {
                continue;
            }
            String idPart = name.substring("extra_".length());
            if (!ValidationUtils.isNumeric(idPart)) {
                continue;
            }
            String value = request.getParameter(name);
            if (value == null || value.trim().length() == 0) {
                continue;
            }
            valores.put(Long.valueOf(Long.parseLong(idPart)), value.trim());
        }
        return valores;
    }

    /**
     * Converte lista de IDs selecionados de checkboxes.
     *
     * @param values lista de valores do request
     * @return array de ids validos
     */
    public static long[] mapSelectedIds(String[] values) {
        if (values == null || values.length == 0) {
            return new long[0];
        }
        java.util.ArrayList ids = new java.util.ArrayList();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (ValidationUtils.isNumeric(value)) {
                ids.add(Long.valueOf(Long.parseLong(value)));
            }
        }
        long[] result = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ((Long) ids.get(i)).longValue();
        }
        return result;
    }
}
