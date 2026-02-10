package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.ConnectionFactory;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Teste de integracao de persistencia JDBC para Aluno, Curso e Registro 42.
 */
public class AlunoDAOTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void devePersistirFluxoAlunoCursoEVinculo() throws Exception {
        AlunoDAO alunoDAO = new AlunoDAO();
        CursoDAO cursoDAO = new CursoDAO();
        CursoAlunoDAO cursoAlunoDAO = new CursoAlunoDAO();

        Aluno aluno = new Aluno();
        aluno.setIdAlunoInep(Long.valueOf(99001L));
        aluno.setNome("Aluno Integracao");
        aluno.setCpf("12345678901");
        aluno.setDataNascimento(Date.valueOf("2000-01-01"));
        aluno.setNacionalidade(Integer.valueOf(1));
        aluno.setUfNascimento("PR");
        aluno.setMunicipioNascimento("Maringa");
        aluno.setPaisOrigem("BRA");

        Long alunoId = alunoDAO.salvar(aluno, new long[0], Collections.<Long, String>emptyMap());
        Assert.assertNotNull(alunoId);

        Curso curso = new Curso();
        curso.setCodigoCursoEmec("CURS001");
        curso.setNome("Curso Integracao");
        curso.setNivelAcademico("GRADUACAO");
        curso.setFormatoOferta("PRESENCIAL");
        curso.setCursoTeveAlunoVinculado(Integer.valueOf(1));

        Long cursoId = cursoDAO.salvar(curso, new long[0], Collections.<Long, String>emptyMap());
        Assert.assertNotNull(cursoId);

        CursoAluno vinculo = new CursoAluno();
        vinculo.setAlunoId(alunoId);
        vinculo.setCursoId(cursoId);
        vinculo.setIdAlunoIes("ALUNO_IES_01");
        vinculo.setPeriodoReferencia("2025");
        vinculo.setTurnoAluno(Integer.valueOf(1));
        vinculo.setSituacaoVinculo(Integer.valueOf(1));
        vinculo.setSemestreIngresso("012025");
        vinculo.setFormaIngressoVestibular(Integer.valueOf(1));
        vinculo.setFormaIngressoEnem(Integer.valueOf(0));
        vinculo.setFormaIngressoAvaliacaoSeriada(Integer.valueOf(0));
        vinculo.setFormaIngressoSelecaoSimplificada(Integer.valueOf(0));
        vinculo.setFormaIngressoEgressoBiLi(Integer.valueOf(0));
        vinculo.setFormaIngressoPecG(Integer.valueOf(0));
        vinculo.setFormaIngressoTransferenciaExofficio(Integer.valueOf(0));
        vinculo.setFormaIngressoDecisaoJudicial(Integer.valueOf(0));
        vinculo.setFormaIngressoVagasRemanescentes(Integer.valueOf(0));
        vinculo.setFormaIngressoProgramasEspeciais(Integer.valueOf(0));
        vinculo.setAlunoParfor(Integer.valueOf(0));
        vinculo.setSegundaLicenciaturaFormacao(Integer.valueOf(0));

        Long vinculoId = cursoAlunoDAO.salvar(vinculo, new long[0], Collections.<Long, String>emptyMap());
        Assert.assertNotNull(vinculoId);

        List<CursoAluno> registros = cursoAlunoDAO.listar();
        Assert.assertEquals(1, registros.size());
        Assert.assertEquals(alunoId, registros.get(0).getAlunoId());
        Assert.assertEquals(cursoId, registros.get(0).getCursoId());

        Connection connection = null;
        DatabaseConnection dbUnitConnection = null;
        try {
            connection = ConnectionFactory.getConnection();
            dbUnitConnection = new DatabaseConnection(connection);
            IDataSet dataSet = dbUnitConnection.createDataSet();
            ITable alunoTable = dataSet.getTable("ALUNO");
            ITable cursoTable = dataSet.getTable("CURSO");
            ITable cursoAlunoTable = dataSet.getTable("CURSO_ALUNO");

            Assert.assertEquals(1, alunoTable.getRowCount());
            Assert.assertEquals(1, cursoTable.getRowCount());
            Assert.assertEquals(1, cursoAlunoTable.getRowCount());
        } finally {
            if (dbUnitConnection != null) {
                dbUnitConnection.close();
            } else if (connection != null) {
                connection.close();
            }
        }

        alunoDAO.excluir(alunoId);
        Assert.assertNull(alunoDAO.buscarPorId(alunoId));
        Assert.assertTrue(cursoAlunoDAO.listar().isEmpty());
        Assert.assertEquals(1, cursoDAO.contar());
    }

    @Test
    public void deveAtualizarECarregarCamposNormalizadosDoAluno() throws Exception {
        AlunoDAO alunoDAO = new AlunoDAO();
        OpcaoDAO opcaoDAO = new OpcaoDAO();
        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();

        List<OpcaoDominio> opcoesDeficiencia = opcaoDAO.listarPorCategoria(CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        Assert.assertTrue(opcoesDeficiencia.size() >= 2);
        long[] opcoesIniciais = new long[]{
                opcoesDeficiencia.get(0).getId().longValue(),
                opcoesDeficiencia.get(1).getId().longValue()
        };

        Map<Long, String> complementares = new LinkedHashMap<Long, String>();
        Long campo23 = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.ALUNO_41).get(Integer.valueOf(23));
        Assert.assertNotNull(campo23);
        complementares.put(campo23, "OBS_TESTE_1");

        Aluno aluno = new Aluno();
        aluno.setIdAlunoInep(Long.valueOf(77123L));
        aluno.setNome("Aluno Atualizacao");
        aluno.setCpf("98765432100");
        aluno.setDataNascimento(Date.valueOf("1999-12-31"));
        aluno.setCorRaca(Integer.valueOf(3));
        aluno.setNacionalidade(Integer.valueOf(1));
        aluno.setUfNascimento("SP");
        aluno.setMunicipioNascimento("Sao Paulo");
        aluno.setPaisOrigem("BRA");

        Long alunoId = alunoDAO.salvar(aluno, opcoesIniciais, complementares);
        Assert.assertNotNull(alunoId);
        Assert.assertNull(alunoDAO.buscarPorId(null));

        Aluno salvo = alunoDAO.buscarPorId(alunoId);
        Assert.assertNotNull(salvo);
        Assert.assertEquals(Integer.valueOf(3), salvo.getCorRaca());
        Assert.assertEquals(2, alunoDAO.listarOpcaoDeficienciaIds(alunoId).size());
        Assert.assertEquals("OBS_TESTE_1", alunoDAO.carregarCamposComplementaresPorCampoId(alunoId).get(campo23));
        Assert.assertEquals("OBS_TESTE_1", alunoDAO.carregarCamposRegistro41PorNumero(alunoId).get(Integer.valueOf(23)));
        Assert.assertEquals(1, alunoDAO.listarPaginado(1, 10).size());
        Assert.assertEquals(1, alunoDAO.contar());

        salvo.setNome("Aluno Atualizado");
        Map<Long, String> complementaresAtualizados = new LinkedHashMap<Long, String>();
        complementaresAtualizados.put(campo23, "OBS_TESTE_2");
        alunoDAO.atualizar(salvo, new long[]{opcoesDeficiencia.get(1).getId().longValue()}, complementaresAtualizados);

        Aluno atualizado = alunoDAO.buscarPorId(alunoId);
        Assert.assertEquals("Aluno Atualizado", atualizado.getNome());
        Assert.assertEquals(1, alunoDAO.listarOpcaoDeficienciaIds(alunoId).size());
        Assert.assertEquals("OBS_TESTE_2", alunoDAO.carregarCamposComplementaresPorCampoId(alunoId).get(campo23));

        alunoDAO.excluir(alunoId);
        Assert.assertEquals(0, alunoDAO.contar());
        Assert.assertNull(alunoDAO.buscarPorId(alunoId));

        // deve sair sem erro
        alunoDAO.excluir(null);
    }
}
