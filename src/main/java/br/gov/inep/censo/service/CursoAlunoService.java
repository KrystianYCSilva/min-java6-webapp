package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.CursoAlunoDAO;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do Registro 42 (vinculo aluno-curso).
 */
public class CursoAlunoService {

    private final CursoAlunoDAO cursoAlunoDAO;

    public CursoAlunoService() {
        this(new CursoAlunoDAO());
    }

    public CursoAlunoService(CursoAlunoDAO cursoAlunoDAO) {
        this.cursoAlunoDAO = cursoAlunoDAO;
    }

    public Long cadastrar(CursoAluno cursoAluno, long[] opcaoIds, Map<Long, String> camposComplementares)
            throws SQLException {
        validar(cursoAluno);
        return cursoAlunoDAO.salvar(cursoAluno, opcaoIds, camposComplementares);
    }

    public List<CursoAluno> listar() throws SQLException {
        return cursoAlunoDAO.listar();
    }

    private void validar(CursoAluno cursoAluno) {
        if (cursoAluno == null) {
            throw new IllegalArgumentException("Registro 42 nao informado.");
        }
        if (cursoAluno.getAlunoId() == null || cursoAluno.getCursoId() == null) {
            throw new IllegalArgumentException("Aluno e Curso sao obrigatorios.");
        }
        if (cursoAluno.getIdAlunoIes() == null || cursoAluno.getIdAlunoIes().trim().length() == 0) {
            throw new IllegalArgumentException("ID na IES e obrigatorio.");
        }
        if (!ValidationUtils.isPeriodoReferenciaValido(cursoAluno.getPeriodoReferencia())) {
            throw new IllegalArgumentException("Periodo de referencia deve estar no formato AAAA.");
        }
        if (cursoAluno.getSemestreIngresso() != null && cursoAluno.getSemestreIngresso().trim().length() > 0
                && !ValidationUtils.isSemestreValido(cursoAluno.getSemestreIngresso())) {
            throw new IllegalArgumentException("Semestre de ingresso invalido. Use 01AAAA ou 02AAAA.");
        }
    }
}
