package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste de integracao para CRUD de Curso com opcoes 1..N e campos complementares.
 */
public class CursoDAOTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void devePersistirAtualizarEPaginarCurso() throws Exception {
        CursoDAO cursoDAO = new CursoDAO();
        OpcaoDAO opcaoDAO = new OpcaoDAO();
        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();

        List<OpcaoDominio> opcoes = opcaoDAO.listarPorCategoria(CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
        Assert.assertTrue(opcoes.size() >= 2);

        long[] opcaoIds = new long[]{
                opcoes.get(0).getId().longValue(),
                opcoes.get(1).getId().longValue()
        };

        Map<Long, String> complementares = criarComplementares(layoutCampoDAO, "VALOR_CURSO_1");
        Curso curso = novoCurso("CURS1001", "Curso A");
        Long cursoId = cursoDAO.salvar(curso, opcaoIds, complementares);
        Assert.assertNotNull(cursoId);

        Curso salvo = cursoDAO.buscarPorId(cursoId);
        Assert.assertNotNull(salvo);
        Assert.assertEquals("CURS1001", salvo.getCodigoCursoEmec());
        Assert.assertEquals(1, cursoDAO.contar());
        Assert.assertEquals(2, cursoDAO.listarOpcaoRecursoAssistivoIds(cursoId).size());

        Map<Long, String> extrasPorCampo = cursoDAO.carregarCamposComplementaresPorCampoId(cursoId);
        Assert.assertFalse(extrasPorCampo.isEmpty());
        Assert.assertTrue(extrasPorCampo.containsValue("VALOR_CURSO_1"));

        Map<Integer, String> extrasPorNumero = cursoDAO.carregarCamposRegistro21PorNumero(cursoId);
        Assert.assertTrue(extrasPorNumero.containsValue("VALOR_CURSO_1"));

        salvo.setNome("Curso A Alterado");
        Map<Long, String> atualizados = criarComplementares(layoutCampoDAO, "VALOR_CURSO_2");
        cursoDAO.atualizar(salvo, new long[]{opcoes.get(1).getId().longValue()}, atualizados);

        Curso alterado = cursoDAO.buscarPorId(cursoId);
        Assert.assertEquals("Curso A Alterado", alterado.getNome());
        Assert.assertEquals(1, cursoDAO.listarOpcaoRecursoAssistivoIds(cursoId).size());
        Assert.assertTrue(cursoDAO.carregarCamposComplementaresPorCampoId(cursoId).containsValue("VALOR_CURSO_2"));

        Curso cursoB = novoCurso("CURS1002", "Curso B");
        cursoDAO.salvar(cursoB, new long[0], new LinkedHashMap<Long, String>());

        List<Curso> paginaUm = cursoDAO.listarPaginado(1, 1);
        List<Curso> paginaDois = cursoDAO.listarPaginado(2, 1);
        Assert.assertEquals(1, paginaUm.size());
        Assert.assertEquals(1, paginaDois.size());
        Assert.assertEquals(2, cursoDAO.contar());

        cursoDAO.excluir(cursoId);
        Assert.assertNull(cursoDAO.buscarPorId(cursoId));
        Assert.assertEquals(1, cursoDAO.contar());
    }

    private Curso novoCurso(String codigo, String nome) {
        Curso curso = new Curso();
        curso.setCodigoCursoEmec(codigo);
        curso.setNome(nome);
        curso.setNivelAcademico("GRADUACAO");
        curso.setFormatoOferta("PRESENCIAL");
        curso.setCursoTeveAlunoVinculado(Integer.valueOf(1));
        return curso;
    }

    private Map<Long, String> criarComplementares(LayoutCampoDAO layoutCampoDAO, String valor) throws Exception {
        Map<Integer, Long> mapa = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.CURSO_21);
        Map<Long, String> complementares = new LinkedHashMap<Long, String>();
        Long campo = mapa.get(Integer.valueOf(66));
        if (campo == null) {
            campo = mapa.get(Integer.valueOf(4));
        }
        Assert.assertNotNull(campo);
        complementares.put(campo, valor);
        return complementares;
    }
}
