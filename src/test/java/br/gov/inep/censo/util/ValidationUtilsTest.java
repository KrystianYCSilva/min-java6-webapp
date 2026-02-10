package br.gov.inep.censo.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testes unitarios para validacoes de campos do leiaute.
 */
public class ValidationUtilsTest {

    @Test
    public void deveValidarCpfComOnzeDigitos() {
        Assert.assertTrue(ValidationUtils.isCpfFormatoValido("12345678901"));
        Assert.assertFalse(ValidationUtils.isCpfFormatoValido("1234567890"));
        Assert.assertFalse(ValidationUtils.isCpfFormatoValido("1234567890A"));
        Assert.assertFalse(ValidationUtils.isCpfFormatoValido(null));
    }

    @Test
    public void deveValidarPeriodoReferenciaNoFormatoAno() {
        Assert.assertTrue(ValidationUtils.isPeriodoReferenciaValido("2025"));
        Assert.assertFalse(ValidationUtils.isPeriodoReferenciaValido("25"));
        Assert.assertFalse(ValidationUtils.isPeriodoReferenciaValido("2200"));
        Assert.assertFalse(ValidationUtils.isPeriodoReferenciaValido("20A5"));
    }

    @Test
    public void deveValidarSemestreNoFormatoOficial() {
        Assert.assertTrue(ValidationUtils.isSemestreValido("012025"));
        Assert.assertTrue(ValidationUtils.isSemestreValido("022030"));
        Assert.assertFalse(ValidationUtils.isSemestreValido("032025"));
        Assert.assertFalse(ValidationUtils.isSemestreValido("01220A"));
        Assert.assertFalse(ValidationUtils.isSemestreValido("202501"));
    }
}
