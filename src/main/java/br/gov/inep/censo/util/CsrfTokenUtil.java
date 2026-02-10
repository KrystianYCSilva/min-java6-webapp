package br.gov.inep.censo.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;

/**
 * Utilitario para tokens CSRF no padrao Synchronizer Token.
 */
public final class CsrfTokenUtil {

    public static final String SESSION_KEY = "_csrf_token";
    public static final String PARAM_NAME = "_csrf";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfTokenUtil() {
    }

    public static String getOrCreateToken(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Sessao HTTP obrigatoria para token CSRF.");
        }
        Object value = session.getAttribute(SESSION_KEY);
        if (value instanceof String) {
            String token = (String) value;
            if (token.length() > 0) {
                return token;
            }
        }
        String token = generateToken();
        session.setAttribute(SESSION_KEY, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object stored = session.getAttribute(SESSION_KEY);
        if (!(stored instanceof String)) {
            return false;
        }
        String expected = (String) stored;
        if (expected.length() == 0) {
            return false;
        }
        String actual = request.getParameter(PARAM_NAME);
        if (actual == null || actual.length() == 0) {
            return false;
        }
        return expected.equals(actual);
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            if (value < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(value));
        }
        return sb.toString();
    }
}

