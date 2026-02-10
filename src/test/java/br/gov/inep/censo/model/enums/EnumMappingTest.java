package br.gov.inep.censo.model.enums;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testes unitarios para mapeamento das enumeracoes de dominio.
 */
public class EnumMappingTest {

    @Test
    public void deveMapearCorRacaPorCodigo() {
        Assert.assertEquals(CorRacaEnum.PARDA, CorRacaEnum.fromCodigo(Integer.valueOf(3)));
        Assert.assertNull(CorRacaEnum.fromCodigo(Integer.valueOf(99)));
    }

    @Test
    public void deveMapearNacionalidadePorCodigo() {
        Assert.assertEquals(NacionalidadeEnum.BRASILEIRA_NATURALIZACAO, NacionalidadeEnum.fromCodigo(Integer.valueOf(2)));
        Assert.assertNull(NacionalidadeEnum.fromCodigo(null));
    }

    @Test
    public void deveMapearNivelAcademicoEFormatoPorCodigo() {
        Assert.assertEquals(NivelAcademicoEnum.GRADUACAO, NivelAcademicoEnum.fromCodigo("graduacao"));
        Assert.assertEquals(FormatoOfertaEnum.EAD, FormatoOfertaEnum.fromCodigo("EAD"));
        Assert.assertNull(NivelAcademicoEnum.fromCodigo("X"));
        Assert.assertNull(FormatoOfertaEnum.fromCodigo("X"));
    }

    @Test
    public void deveMapearPaisEstadoETipoLaboratorio() {
        Assert.assertEquals(PaisEnum.BRA, PaisEnum.fromCodigo("bra"));
        Assert.assertEquals(EstadoEnum.SP, EstadoEnum.fromCodigo(Integer.valueOf(35)));
        Assert.assertEquals(TipoLaboratorioEnum.TIPO_59, TipoLaboratorioEnum.fromCodigo(Integer.valueOf(59)));
        Assert.assertNull(PaisEnum.fromCodigo("ZZZ"));
        Assert.assertNull(EstadoEnum.fromCodigo(Integer.valueOf(99)));
        Assert.assertNull(TipoLaboratorioEnum.fromCodigo(Integer.valueOf(9999)));
    }
}
