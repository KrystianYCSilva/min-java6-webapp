package br.gov.inep.censo.web.spring;

import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.service.DocenteService;
import br.gov.inep.censo.service.IesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * Endpoints Spring MVC para exportacao de relatorios em TXT pipe.
 */
@Controller
@RequestMapping("/relatorios")
public class RelatorioTxtController {

    private static final String TEXT_PLAIN_UTF8 = "text/plain;charset=UTF-8";
    private static final MediaType MEDIA_TYPE_TEXT_UTF8 = new MediaType("text", "plain", Charset.forName("UTF-8"));

    private final AlunoService alunoService;
    private final CursoService cursoService;
    private final DocenteService docenteService;
    private final IesService iesService;

    @Autowired
    public RelatorioTxtController(AlunoService alunoService,
                                  CursoService cursoService,
                                  DocenteService docenteService,
                                  IesService iesService) {
        this.alunoService = alunoService;
        this.cursoService = cursoService;
        this.docenteService = docenteService;
        this.iesService = iesService;
    }

    @RequestMapping(value = "/alunos.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarAlunos() {
        try {
            return attachment("alunos_registro41.txt", alunoService.exportarTodosTxtPipe());
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de alunos.");
        }
    }

    @RequestMapping(value = "/alunos/{id:[0-9]+}.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarAlunoPorId(@PathVariable("id") Long id) {
        try {
            return attachment("aluno_" + id + "_registro41.txt", alunoService.exportarPorIdTxtPipe(id));
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de aluno.");
        }
    }

    @RequestMapping(value = "/cursos.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarCursos() {
        try {
            return attachment("cursos_registro21.txt", cursoService.exportarTodosTxtPipe());
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de cursos.");
        }
    }

    @RequestMapping(value = "/cursos/{id:[0-9]+}.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarCursoPorId(@PathVariable("id") Long id) {
        try {
            return attachment("curso_" + id + "_registro21.txt", cursoService.exportarPorIdTxtPipe(id));
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de curso.");
        }
    }

    @RequestMapping(value = "/docentes.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarDocentes() {
        try {
            return attachment("docentes_registro31.txt", docenteService.exportarTodosTxtPipe());
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de docentes.");
        }
    }

    @RequestMapping(value = "/docentes/{id:[0-9]+}.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarDocentePorId(@PathVariable("id") Long id) {
        try {
            return attachment("docente_" + id + "_registro31.txt", docenteService.exportarPorIdTxtPipe(id));
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de docente.");
        }
    }

    @RequestMapping(value = "/ies.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarIes() {
        try {
            return attachment("ies_registro11.txt", iesService.exportarTodosTxtPipe());
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de ies.");
        }
    }

    @RequestMapping(value = "/ies/{id:[0-9]+}.txt", method = RequestMethod.GET, produces = TEXT_PLAIN_UTF8)
    @ResponseBody
    public ResponseEntity<String> exportarIesPorId(@PathVariable("id") Long id) {
        try {
            return attachment("ies_" + id + "_registro11.txt", iesService.exportarPorIdTxtPipe(id));
        } catch (SQLException e) {
            return erro("Falha ao exportar relatorio de ies.");
        }
    }

    private ResponseEntity<String> attachment(String fileName, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_TYPE_TEXT_UTF8);
        headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        return new ResponseEntity<String>(content == null ? "" : content, headers, HttpStatus.OK);
    }

    private ResponseEntity<String> erro(String mensagem) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_TYPE_TEXT_UTF8);
        return new ResponseEntity<String>(mensagem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
