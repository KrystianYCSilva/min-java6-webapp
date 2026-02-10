package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.Municipio;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Teste de integracao da tabela de apoio de municipios.
 */
public class MunicipioDAOTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveConsultarMunicipioPorCodigoEUf() throws Exception {
        MunicipioDAO municipioDAO = new MunicipioDAO();

        Assert.assertTrue(municipioDAO.existeCodigo("3550308"));
        Assert.assertFalse(municipioDAO.existeCodigo(""));
        Assert.assertFalse(municipioDAO.existeCodigo(null));
        Assert.assertFalse(municipioDAO.existeCodigo("0000000"));

        Assert.assertTrue(municipioDAO.existeCodigoNaUf("3550308", Integer.valueOf(35)));
        Assert.assertFalse(municipioDAO.existeCodigoNaUf("3550308", Integer.valueOf(41)));
        Assert.assertFalse(municipioDAO.existeCodigoNaUf(null, Integer.valueOf(35)));
        Assert.assertFalse(municipioDAO.existeCodigoNaUf("3550308", null));

        List<Municipio> municipiosSp = municipioDAO.listarPorUf(Integer.valueOf(35));
        Assert.assertFalse(municipiosSp.isEmpty());
        Assert.assertTrue(municipioDAO.listarPorUf(null).isEmpty());
    }
}
