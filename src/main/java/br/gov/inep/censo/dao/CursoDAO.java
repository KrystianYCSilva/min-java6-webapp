package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Curso (Registro 21), incluindo opcoes normalizadas e campos complementares.
 */
public class CursoDAO extends AbstractHibernateDao {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final Curso curso, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        return executeInTransaction(new SessionWork<Long>() {
            public Long execute(Session session) throws SQLException {
                Long cursoId = toLong(session.save(curso));
                opcaoDAO.salvarVinculosCurso(session, cursoId, opcaoIds);
                layoutCampoDAO.salvarValoresCurso(session, cursoId, camposComplementares);
                return cursoId;
            }
        });
    }

    public void atualizar(final Curso curso, final long[] opcaoIds, final Map<Long, String> camposComplementares)
            throws SQLException {
        if (curso == null || curso.getId() == null) {
            throw new IllegalArgumentException("Curso/ID nao informado para atualizacao.");
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) throws SQLException {
                session.merge(curso);
                opcaoDAO.substituirVinculosCurso(session, curso.getId(), opcaoIds);
                layoutCampoDAO.substituirValoresCurso(session, curso.getId(), camposComplementares);
                return null;
            }
        });
    }

    public Curso buscarPorId(final Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        return executeInSession(new SessionWork<Curso>() {
            public Curso execute(Session session) throws SQLException {
                Curso curso = (Curso) session.get(Curso.class, id);
                if (curso == null) {
                    return null;
                }
                hydrateResumo(session, curso);
                return curso;
            }
        });
    }

    public List<Curso> listar() throws SQLException {
        return executeInSession(new SessionWork<List<Curso>>() {
            public List<Curso> execute(Session session) throws SQLException {
                Query query = session.createQuery("from Curso c order by c.nome");
                List<Curso> cursos = query.list();
                for (int i = 0; i < cursos.size(); i++) {
                    hydrateResumo(session, cursos.get(i));
                }
                return cursos;
            }
        });
    }

    public List<Curso> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        final int page = normalizePage(pagina);
        final int size = normalizePageSize(tamanhoPagina);
        final int offset = (page - 1) * size;

        return executeInSession(new SessionWork<List<Curso>>() {
            public List<Curso> execute(Session session) throws SQLException {
                Query query = session.createQuery("from Curso c order by c.nome");
                query.setFirstResult(offset);
                query.setMaxResults(size);
                List<Curso> cursos = query.list();
                for (int i = 0; i < cursos.size(); i++) {
                    hydrateResumo(session, cursos.get(i));
                }
                return cursos;
            }
        });
    }

    public int contar() throws SQLException {
        return executeInSession(new SessionWork<Integer>() {
            public Integer execute(Session session) {
                Long total = (Long) session.createQuery("select count(c.id) from Curso c").uniqueResult();
                return Integer.valueOf(total == null ? 0 : total.intValue());
            }
        }).intValue();
    }

    public void excluir(final Long id) throws SQLException {
        if (id == null) {
            return;
        }
        executeInTransaction(new SessionWork<Void>() {
            public Void execute(Session session) throws SQLException {
                Curso curso = (Curso) session.get(Curso.class, id);
                if (curso == null) {
                    return null;
                }
                opcaoDAO.removerVinculosCurso(session, id);
                layoutCampoDAO.removerValoresCurso(session, id);
                session.delete(curso);
                return null;
            }
        });
    }

    public List<Long> listarOpcaoRecursoAssistivoIds(Long cursoId) throws SQLException {
        return opcaoDAO.listarIdsCurso(cursoId, CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
    }

    public List<String> listarOpcaoRecursoAssistivoCodigos(Long cursoId) throws SQLException {
        return opcaoDAO.listarCodigosCurso(cursoId, CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long cursoId) throws SQLException {
        return layoutCampoDAO.carregarValoresCursoPorCampoId(cursoId);
    }

    public Map<Integer, String> carregarCamposRegistro21PorNumero(Long cursoId) throws SQLException {
        return layoutCampoDAO.carregarValoresCursoPorNumero(cursoId, ModulosLayout.CURSO_21);
    }

    private void hydrateResumo(Session session, Curso curso) throws SQLException {
        curso.setRecursosTecnologiaAssistivaResumo(opcaoDAO.resumirCurso(
                session, curso.getId(), CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA));
    }

    private Long toLong(Serializable id) throws SQLException {
        if (id == null) {
            throw new SQLException("Falha ao gerar ID para curso.");
        }
        if (id instanceof Number) {
            return Long.valueOf(((Number) id).longValue());
        }
        return Long.valueOf(id.toString());
    }
}
