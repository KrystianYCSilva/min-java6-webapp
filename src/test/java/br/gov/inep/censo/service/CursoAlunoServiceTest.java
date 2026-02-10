package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.CursoAlunoDAO;
import br.gov.inep.censo.model.CursoAluno;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Testes unitarios de validacao do servico de Registro 42.
 */
public class CursoAlunoServiceTest {

    @Test
    public void deveCadastrarQuandoDadosMinimosForemValidos() throws Exception {
        StubCursoAlunoDAO dao = new StubCursoAlunoDAO();
        CursoAlunoService service = new CursoAlunoService(dao);

        CursoAluno cursoAluno = novoCursoAlunoValido();
        Long id = service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());

        Assert.assertEquals(Long.valueOf(77L), id);
        Assert.assertTrue(dao.salvarChamado);
    }

    @Test
    public void deveFalharQuandoPeriodoReferenciaInvalido() throws Exception {
        CursoAlunoService service = new CursoAlunoService(new StubCursoAlunoDAO());
        CursoAluno cursoAluno = novoCursoAlunoValido();
        cursoAluno.setPeriodoReferencia("20A5");

        try {
            service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Periodo de referencia"));
        }
    }

    @Test
    public void deveFalharQuandoSemestreIngressoInvalido() throws Exception {
        CursoAlunoService service = new CursoAlunoService(new StubCursoAlunoDAO());
        CursoAluno cursoAluno = novoCursoAlunoValido();
        cursoAluno.setSemestreIngresso("032025");

        try {
            service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Semestre de ingresso"));
        }
    }

    @Test
    public void deveListarRegistrosQuandoSolicitado() throws Exception {
        StubCursoAlunoDAO dao = new StubCursoAlunoDAO();
        dao.lista.add(novoCursoAlunoValido());
        CursoAlunoService service = new CursoAlunoService(dao);

        List<CursoAluno> registros = service.listar();
        Assert.assertEquals(1, registros.size());
    }

    private CursoAluno novoCursoAlunoValido() {
        CursoAluno cursoAluno = new CursoAluno();
        cursoAluno.setAlunoId(Long.valueOf(1L));
        cursoAluno.setCursoId(Long.valueOf(2L));
        cursoAluno.setIdAlunoIes("ALUNO_IES");
        cursoAluno.setPeriodoReferencia("2025");
        cursoAluno.setSemestreIngresso("012025");
        return cursoAluno;
    }

    private static class StubCursoAlunoDAO extends CursoAlunoDAO {
        private boolean salvarChamado;
        private final List<CursoAluno> lista = new ArrayList<CursoAluno>();

        public Long salvar(CursoAluno cursoAluno, long[] opcaoIds, java.util.Map<Long, String> camposComplementares)
                throws SQLException {
            this.salvarChamado = true;
            return Long.valueOf(77L);
        }

        public List<CursoAluno> listar() throws SQLException {
            return lista;
        }
    }
}
