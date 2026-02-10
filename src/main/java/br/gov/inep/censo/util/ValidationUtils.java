package br.gov.inep.censo.util;

/**
 * Utilitarios de validacao de campos de leiaute.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Verifica se uma string contem somente digitos.
     *
     * @param value valor analisado
     * @return true se todos os caracteres sao numericos
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica tamanho exato.
     *
     * @param value valor analisado
     * @param length tamanho esperado
     * @return true quando valor nao nulo e com tamanho exato
     */
    public static boolean hasExactLength(String value, int length) {
        return value != null && value.length() == length;
    }

    /**
     * Valida formato basico de CPF (11 digitos).
     *
     * @param cpf cpf sem pontuacao
     * @return true quando cpf possui 11 digitos numericos
     */
    public static boolean isCpfFormatoValido(String cpf) {
        return isNumeric(cpf) && hasExactLength(cpf, 11);
    }

    /**
     * Valida periodo de referencia AAAA.
     *
     * @param periodo ano de referencia
     * @return true quando entre 1900 e 2099
     */
    public static boolean isPeriodoReferenciaValido(String periodo) {
        if (!isNumeric(periodo) || !hasExactLength(periodo, 4)) {
            return false;
        }
        int ano = Integer.parseInt(periodo);
        return ano >= 1900 && ano <= 2099;
    }

    /**
     * Valida semestre no formato 01AAAA ou 02AAAA.
     *
     * @param semestre semestre textual
     * @return true quando formato e faixa de ano forem validos
     */
    public static boolean isSemestreValido(String semestre) {
        if (semestre == null || semestre.length() != 6) {
            return false;
        }
        String prefixo = semestre.substring(0, 2);
        String ano = semestre.substring(2);
        if (!("01".equals(prefixo) || "02".equals(prefixo))) {
            return false;
        }
        return isPeriodoReferenciaValido(ano);
    }
}
