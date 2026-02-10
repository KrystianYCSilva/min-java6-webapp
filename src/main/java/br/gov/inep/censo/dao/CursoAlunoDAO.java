package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do vinculo aluno-curso (Registro 42).
 */
public class CursoAlunoDAO extends AbstractHibernateDao {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoAlunoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoAlunoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final CursoAluno cursoAluno,
                       final long[] opcaoIds,
                       final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new SessionWork<Long>() {
            public Long execute(Session session) throws SQLException {
                if (cursoAluno.getAlunoId() == null || cursoAluno.getCursoId() == null) {
                    throw new IllegalArgumentException("Aluno e Curso sao obrigatorios para o registro 42.");
                }
                Aluno alunoRef = (Aluno) session.load(Aluno.class, cursoAluno.getAlunoId());
                Curso cursoRef = (Curso) session.load(Curso.class, cursoAluno.getCursoId());
                cursoAluno.setAluno(alunoRef);
                cursoAluno.setCurso(cursoRef);

                Long cursoAlunoId = toLong(session.save(cursoAluno));
                opcaoDAO.salvarVinculosCursoAluno(session, cursoAlunoId, opcaoIds);
                layoutCampoDAO.salvarValoresCursoAluno(session, cursoAlunoId, camposComplementares);
                return cursoAlunoId;
            }
        });
    }

    public List<CursoAluno> listar() throws SQLException {
        return executeInSession(new SessionWork<List<CursoAluno>>() {
            public List<CursoAluno> execute(Session session) throws SQLException {
                Query query = session.createQuery(
                        "select distinct ca from CursoAluno ca " +
                                "join fetch ca.aluno " +
                                "join fetch ca.curso " +
                                "order by ca.id desc");
                List<CursoAluno> itens = query.list();
                for (int i = 0; i < itens.size(); i++) {
                    CursoAluno item = itens.get(i);
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
                            session, item.getId(), CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
                    item.setApoioSocialResumo(opcaoDAO.resumirCursoAluno(
                            session, item.getId(), CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
                    item.setAtividadesResumo(opcaoDAO.resumirCursoAluno(
                            session, item.getId(), CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
                    item.setReservasResumo(opcaoDAO.resumirCursoAluno(
                            session, item.getId(), CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));
                }
                return itens;
            }
        });
    }

    private Long toLong(Serializable id) throws SQLException {
        if (id == null) {
            throw new SQLException("Falha ao gerar ID para curso_aluno.");
        }
        if (id instanceof Number) {
            return Long.valueOf(((Number) id).longValue());
        }
        return Long.valueOf(id.toString());
    }
}
