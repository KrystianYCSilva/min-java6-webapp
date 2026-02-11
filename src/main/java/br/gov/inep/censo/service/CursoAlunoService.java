package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.CursoAlunoDAO;
import br.gov.inep.censo.dao.OpcaoDAO;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.repository.CursoAlunoRepository;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do Registro 42 (vinculo aluno-curso).
 */
public class CursoAlunoService {

    private final CursoAlunoDAO cursoAlunoDAO;
    private final CursoAlunoRepository cursoAlunoRepository;
    private final OpcaoDAO opcaoDAO;

    public CursoAlunoService() {
        this(new CursoAlunoDAO(), resolveRepository(), new OpcaoDAO());
    }

    public CursoAlunoService(CursoAlunoDAO cursoAlunoDAO) {
        this(cursoAlunoDAO, null, new OpcaoDAO());
    }

    public CursoAlunoService(CursoAlunoDAO cursoAlunoDAO,
                             CursoAlunoRepository cursoAlunoRepository,
                             OpcaoDAO opcaoDAO) {
        this.cursoAlunoDAO = cursoAlunoDAO;
        this.cursoAlunoRepository = cursoAlunoRepository;
        this.opcaoDAO = opcaoDAO;
    }

    public Long cadastrar(CursoAluno cursoAluno, long[] opcaoIds, Map<Long, String> camposComplementares)
            throws SQLException {
        validar(cursoAluno);
        return cursoAlunoDAO.salvar(cursoAluno, opcaoIds, camposComplementares);
    }

    public List<CursoAluno> listar() throws SQLException {
        if (cursoAlunoRepository != null) {
            try {
                List<CursoAluno> itens = cursoAlunoRepository.findAllWithAlunoAndCursoOrderByIdDesc();
                for (int i = 0; i < itens.size(); i++) {
                    hydrateResumo(itens.get(i));
                }
                return itens;
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar registros 42 via repository.", e);
            }
        }
        return cursoAlunoDAO.listar();
    }

    private void hydrateResumo(CursoAluno item) throws SQLException {
        if (item == null) {
            return;
        }
        if (item.getAluno() != null) {
            item.setAlunoId(item.getAluno().getId());
            item.setAlunoNome(item.getAluno().getNome());
        }
        if (item.getCurso() != null) {
            item.setCursoId(item.getCurso().getId());
            item.setCursoNome(item.getCurso().getNome());
            item.setCodigoCursoEmec(item.getCurso().getCodigoCursoEmec());
        }
        item.setFinanciamentosResumo(opcaoDAO.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
        item.setApoioSocialResumo(opcaoDAO.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
        item.setAtividadesResumo(opcaoDAO.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
        item.setReservasResumo(opcaoDAO.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));
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

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private static CursoAlunoRepository resolveRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(CursoAlunoRepository.class);
        } catch (Exception e) {
            return null;
        }
    }
}
