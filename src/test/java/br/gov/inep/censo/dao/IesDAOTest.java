package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste de integracao para CRUD de IES (Registro 11) com campos complementares.
 */
public class IesDAOTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void devePersistirAtualizarEPaginarIes() throws Exception {
        IesDAO iesDAO = new IesDAO();
        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();

        Map<Integer, Long> mapa = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.IES_11);
        Long campo19 = mapa.get(Integer.valueOf(19));
        Assert.assertNotNull(campo19);

        Ies ies = new Ies();
        ies.setIdIesInep(Long.valueOf(1L));
        ies.setNomeLaboratorio("Laboratorio Integracao");
        ies.setRegistroLaboratorioIes("LAB-001");
        ies.setLaboratorioAtivoAno(Integer.valueOf(1));
        ies.setDescricaoAtividades("Descricao inicial");
        ies.setPalavrasChave("integracao");
        ies.setLaboratorioInformatica(Integer.valueOf(1));
        ies.setTipoLaboratorio(Integer.valueOf(59));
        ies.setCodigoUfLaboratorio(Integer.valueOf(35));
        ies.setCodigoMunicipioLaboratorio("3550308");

        Map<Long, String> complementares = new LinkedHashMap<Long, String>();
        complementares.put(campo19, "1");

        Long iesId = iesDAO.salvar(ies, complementares);
        Assert.assertNotNull(iesId);
        Assert.assertNull(iesDAO.buscarPorId(null));

        Ies salvo = iesDAO.buscarPorId(iesId);
        Assert.assertNotNull(salvo);
        Assert.assertEquals("Laboratorio Integracao", salvo.getNomeLaboratorio());
        Assert.assertEquals(1, iesDAO.contar());
        Assert.assertEquals(1, iesDAO.listarPaginado(1, 10).size());
        Assert.assertEquals("1", iesDAO.carregarCamposComplementaresPorCampoId(iesId).get(campo19));
        Assert.assertEquals("1", iesDAO.carregarCamposRegistro11PorNumero(iesId).get(Integer.valueOf(19)));

        salvo.setNomeLaboratorio("Laboratorio Alterado");
        Map<Long, String> atualizados = new LinkedHashMap<Long, String>();
        atualizados.put(campo19, "0");
        iesDAO.atualizar(salvo, atualizados);

        Ies alterado = iesDAO.buscarPorId(iesId);
        Assert.assertEquals("Laboratorio Alterado", alterado.getNomeLaboratorio());
        Assert.assertEquals("0", iesDAO.carregarCamposComplementaresPorCampoId(iesId).get(campo19));

        List<Ies> listados = iesDAO.listar();
        Assert.assertEquals(1, listados.size());

        iesDAO.excluir(iesId);
        Assert.assertEquals(0, iesDAO.contar());
        Assert.assertNull(iesDAO.buscarPorId(iesId));
        iesDAO.excluir(null);
    }
}
