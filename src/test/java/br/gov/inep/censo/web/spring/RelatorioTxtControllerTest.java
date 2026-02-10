package br.gov.inep.censo.web.spring;

import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.service.DocenteService;
import br.gov.inep.censo.service.IesService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelatorioTxtControllerTest {

    private AlunoService alunoService;
    private CursoService cursoService;
    private DocenteService docenteService;
    private IesService iesService;
    private RelatorioTxtController controller;

    @Before
    public void setUp() {
        alunoService = mock(AlunoService.class);
        cursoService = mock(CursoService.class);
        docenteService = mock(DocenteService.class);
        iesService = mock(IesService.class);
        controller = new RelatorioTxtController(alunoService, cursoService, docenteService, iesService);
    }

    @Test
    public void deveExportarAlunosComAttachmentTxt() throws Exception {
        when(alunoService.exportarTodosTxtPipe()).thenReturn("41|123|Nome Aluno");

        ResponseEntity<String> response = controller.exportarAlunos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("41|123|Nome Aluno", response.getBody());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("alunos_registro41.txt"));
        assertTrue(response.getHeaders().getContentType().toString().contains("text/plain"));
    }

    @Test
    public void deveRetornar500QuandoFalharExportacaoDeCurso() throws Exception {
        when(cursoService.exportarPorIdTxtPipe(Long.valueOf(7L))).thenThrow(new SQLException("falha"));

        ResponseEntity<String> response = controller.exportarCursoPorId(Long.valueOf(7L));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Falha ao exportar relatorio de curso.", response.getBody());
    }
}
