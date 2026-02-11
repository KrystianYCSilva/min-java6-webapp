package br.gov.inep.censo.service;

import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

/**
 * Testes de integracao do servico de Registro 42.
 */
public class CursoAlunoServiceTest {

    private Long alunoId;
    private Long cursoId;

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();

        AlunoService alunoService = new AlunoService();
        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Registro42");
        aluno.setCpf("12312312312");
        aluno.setDataNascimento(Date.valueOf("2000-01-01"));
        aluno.setNacionalidade(Integer.valueOf(1));
        aluno.setPaisOrigem("BRA");
        this.alunoId = alunoService.cadastrar(aluno, new long[0], Collections.<Long, String>emptyMap());

        CursoService cursoService = new CursoService();
        Curso curso = new Curso();
        curso.setCodigoCursoEmec("CURSO-42");
        curso.setNome("Curso Registro42");
        curso.setNivelAcademico("GRADUACAO");
        curso.setFormatoOferta("PRESENCIAL");
        curso.setCursoTeveAlunoVinculado(Integer.valueOf(1));
        this.cursoId = cursoService.cadastrar(curso, new long[0], Collections.<Long, String>emptyMap());
    }

    @Test
    public void deveCadastrarQuandoDadosMinimosForemValidos() throws Exception {
        CursoAlunoService service = new CursoAlunoService();

        CursoAluno cursoAluno = novoCursoAlunoValido();
        Long id = service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());

        Assert.assertNotNull(id);
        Assert.assertEquals(1, service.listar().size());
    }

    @Test
    public void deveFalharQuandoPeriodoReferenciaInvalido() throws Exception {
        CursoAlunoService service = new CursoAlunoService();
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
        CursoAlunoService service = new CursoAlunoService();
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
        CursoAlunoService service = new CursoAlunoService();
        service.cadastrar(novoCursoAlunoValido(), new long[0], Collections.<Long, String>emptyMap());

        List<CursoAluno> registros = service.listar();
        Assert.assertEquals(1, registros.size());
        Assert.assertEquals(alunoId, registros.get(0).getAlunoId());
        Assert.assertEquals(cursoId, registros.get(0).getCursoId());
    }

    private CursoAluno novoCursoAlunoValido() {
        CursoAluno cursoAluno = new CursoAluno();
        cursoAluno.setAlunoId(alunoId);
        cursoAluno.setCursoId(cursoId);
        cursoAluno.setIdAlunoIes("ALUNO_IES");
        cursoAluno.setPeriodoReferencia("2025");
        cursoAluno.setSemestreIngresso("012025");
        return cursoAluno;
    }
}
