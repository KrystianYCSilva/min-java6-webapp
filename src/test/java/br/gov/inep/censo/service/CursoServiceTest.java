package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.CursoDAO;
import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes de integracao do servico de Curso cobrindo importacao e exportacao TXT pipe.
 */
public class CursoServiceTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveImportarEExportarRegistro21() throws Exception {
        CursoService service = new CursoService();

        String[] campos = new String[67];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = "";
        }
        campos[0] = "21";
        campos[1] = "550001";
        campos[2] = "1";
        campos[53] = "1";
        campos[54] = "1";
        campos[65] = "OBS_REG21";

        int total = service.importarTxtPipe(joinPipe(campos));
        Assert.assertEquals(1, total);

        CursoDAO cursoDAO = new CursoDAO();
        List<Curso> cursos = cursoDAO.listar();
        Assert.assertEquals(1, cursos.size());
        Long cursoId = cursos.get(0).getId();

        String exportado = service.exportarPorIdTxtPipe(cursoId);
        Assert.assertTrue(exportado.startsWith("21|550001|1|"));
        Assert.assertTrue(service.exportarTodosTxtPipe().contains("21|550001|1|"));
        Assert.assertTrue(service.listarOpcaoRecursoAssistivoIds(cursoId).size() >= 1);

        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();
        Long campo66 = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.CURSO_21).get(Integer.valueOf(66));
        Map<Long, String> complementares = service.carregarCamposComplementaresPorCampoId(cursoId);
        Assert.assertEquals("OBS_REG21", complementares.get(campo66));
    }

    @Test
    public void deveAplicarValidacaoNoCadastroDeCurso() throws Exception {
        CursoService service = new CursoService();
        Curso curso = new Curso();
        curso.setCodigoCursoEmec("");
        curso.setNome("Curso Invalido");
        curso.setNivelAcademico("GRADUACAO");
        curso.setFormatoOferta("PRESENCIAL");
        curso.setCursoTeveAlunoVinculado(Integer.valueOf(1));

        try {
            service.cadastrar(curso, new long[0], new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException por codigo obrigatorio.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Codigo do Curso"));
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
