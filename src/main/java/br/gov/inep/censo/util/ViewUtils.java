package br.gov.inep.censo.util;

import javax.servlet.http.HttpSession;

/**
 * Funcoes utilitarias para renderizacao segura em JSP.
 */
public final class ViewUtils {

    private ViewUtils() {
    }

    public static String e(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (text.length() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static String csrf(HttpSession session) {
        return CsrfTokenUtil.getOrCreateToken(session);
    }
}
