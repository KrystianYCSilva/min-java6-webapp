package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste de integracao para CRUD de Docente com campos complementares.
 */
public class DocenteDAOTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void devePersistirAtualizarEPaginarDocente() throws Exception {
        DocenteDAO docenteDAO = new DocenteDAO();
        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();

        Map<Integer, Long> mapa = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.DOCENTE_31);
        Long campo29 = mapa.get(Integer.valueOf(29));
        Assert.assertNotNull(campo29);

        Docente docente = new Docente();
        docente.setIdDocenteIes("DOC-0001");
        docente.setNome("Docente Integracao");
        docente.setCpf("11122233344");
        docente.setDocumentoEstrangeiro("");
        docente.setDataNascimento(Date.valueOf("1985-05-01"));
        docente.setCorRaca(Integer.valueOf(1));
        docente.setNacionalidade(Integer.valueOf(1));
        docente.setPaisOrigem("BRA");
        docente.setUfNascimento(Integer.valueOf(35));
        docente.setMunicipioNascimento("3550308");
        docente.setDocenteDeficiencia(Integer.valueOf(0));

        Map<Long, String> complementares = new LinkedHashMap<Long, String>();
        complementares.put(campo29, "2");

        Long docenteId = docenteDAO.salvar(docente, complementares);
        Assert.assertNotNull(docenteId);
        Assert.assertNull(docenteDAO.buscarPorId(null));

        Docente salvo = docenteDAO.buscarPorId(docenteId);
        Assert.assertNotNull(salvo);
        Assert.assertEquals("Docente Integracao", salvo.getNome());
        Assert.assertEquals(1, docenteDAO.contar());
        Assert.assertEquals(1, docenteDAO.listarPaginado(1, 10).size());
        Assert.assertEquals("2", docenteDAO.carregarCamposComplementaresPorCampoId(docenteId).get(campo29));
        Assert.assertEquals("2", docenteDAO.carregarCamposRegistro31PorNumero(docenteId).get(Integer.valueOf(29)));

        salvo.setNome("Docente Alterado");
        Map<Long, String> atualizados = new LinkedHashMap<Long, String>();
        atualizados.put(campo29, "3");
        docenteDAO.atualizar(salvo, atualizados);

        Docente alterado = docenteDAO.buscarPorId(docenteId);
        Assert.assertEquals("Docente Alterado", alterado.getNome());
        Assert.assertEquals("3", docenteDAO.carregarCamposComplementaresPorCampoId(docenteId).get(campo29));

        List<Docente> listados = docenteDAO.listar();
        Assert.assertEquals(1, listados.size());

        docenteDAO.excluir(docenteId);
        Assert.assertEquals(0, docenteDAO.contar());
        Assert.assertNull(docenteDAO.buscarPorId(docenteId));
        docenteDAO.excluir(null);
    }
}
