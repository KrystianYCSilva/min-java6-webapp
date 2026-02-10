package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.AlunoDAO;
import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes de integracao do servico de Aluno cobrindo importacao e exportacao TXT pipe.
 */
public class AlunoServiceTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveImportarEExportarRegistro41() throws Exception {
        AlunoService service = new AlunoService();

        String[] campos = new String[23];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = "";
        }
        campos[0] = "41";
        campos[1] = "90001";
        campos[2] = "Maria Importada";
        campos[3] = "12345678901";
        campos[5] = "20010131";
        campos[6] = "1";
        campos[7] = "1";
        campos[8] = "PR";
        campos[9] = "Maringa";
        campos[10] = "BRA";
        campos[12] = "1";
        campos[13] = "1";
        campos[22] = "OBS_REG41";

        int total = service.importarTxtPipe(joinPipe(campos));
        Assert.assertEquals(1, total);

        AlunoDAO alunoDAO = new AlunoDAO();
        List<Aluno> alunos = alunoDAO.listar();
        Assert.assertEquals(1, alunos.size());
        Long alunoId = alunos.get(0).getId();
        Assert.assertNotNull(alunoId);

        String exportado = service.exportarPorIdTxtPipe(alunoId);
        Assert.assertTrue(exportado.startsWith("41|90001|Maria Importada|12345678901|"));
        Assert.assertTrue(exportado.contains("12345678901||20010131|1|1|PR|Maringa|BRA|"));

        Assert.assertTrue(service.exportarTodosTxtPipe().contains("Maria Importada"));
        Assert.assertEquals(2, service.listarOpcaoDeficienciaIds(alunoId).size());

        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();
        Long campo23 = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.ALUNO_41).get(Integer.valueOf(23));
        Map<Long, String> complementares = service.carregarCamposComplementaresPorCampoId(alunoId);
        Assert.assertEquals("OBS_REG41", complementares.get(campo23));
    }

    @Test
    public void deveAplicarValidacaoDeCpfNoCadastro() throws Exception {
        AlunoService service = new AlunoService();
        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Invalido");
        aluno.setCpf("123");
        aluno.setDataNascimento(Date.valueOf("2000-01-01"));
        aluno.setNacionalidade(Integer.valueOf(1));
        aluno.setPaisOrigem("BRA");

        try {
            service.cadastrar(aluno, new long[0], new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException por CPF invalido.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("CPF"));
        }
    }

    private String joinPipe(String[] campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(campos[i] != null ? campos[i] : "");
        }
        return sb.toString();
    }
}
