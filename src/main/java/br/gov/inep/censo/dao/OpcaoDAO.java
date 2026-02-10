package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.OpcaoDominio;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO de catalogo de opcoes e vinculos 1..N por modulo.
 */
public class OpcaoDAO extends AbstractHibernateDao {

    public List<OpcaoDominio> listarPorCategoria(final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<OpcaoDominio>>() {
            public List<OpcaoDominio> execute(Session session) {
                Query query = session.createQuery(
                        "from OpcaoDominio o where o.categoria = :categoria order by o.nome");
                query.setString("categoria", categoria);
                return query.list();
            }
        });
    }

    public void salvarVinculosAluno(Session session, Long alunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(session, "aluno_opcao", "aluno_id", alunoId, opcaoIds);
    }

    public void salvarVinculosCurso(Session session, Long cursoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(session, "curso_opcao", "curso_id", cursoId, opcaoIds);
    }

    public void salvarVinculosCursoAluno(Session session, Long cursoAlunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, opcaoIds);
    }

    public void substituirVinculosAluno(Session session, Long alunoId, long[] opcaoIds) throws SQLException {
        removerVinculos(session, "aluno_opcao", "aluno_id", alunoId);
        salvarVinculosAluno(session, alunoId, opcaoIds);
    }

    public void substituirVinculosCurso(Session session, Long cursoId, long[] opcaoIds) throws SQLException {
        removerVinculos(session, "curso_opcao", "curso_id", cursoId);
        salvarVinculosCurso(session, cursoId, opcaoIds);
    }

    public void substituirVinculosCursoAluno(Session session, Long cursoAlunoId, long[] opcaoIds)
            throws SQLException {
        removerVinculos(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
        salvarVinculosCursoAluno(session, cursoAlunoId, opcaoIds);
    }

    public void removerVinculosAluno(Session session, Long alunoId) throws SQLException {
        removerVinculos(session, "aluno_opcao", "aluno_id", alunoId);
    }

    public void removerVinculosCurso(Session session, Long cursoId) throws SQLException {
        removerVinculos(session, "curso_opcao", "curso_id", cursoId);
    }

    public void removerVinculosCursoAluno(Session session, Long cursoAlunoId) throws SQLException {
        removerVinculos(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
    }

    public String resumirAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<String>() {
            public String execute(Session session) throws SQLException {
                return resumir(session, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public String resumirCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<String>() {
            public String execute(Session session) throws SQLException {
                return resumir(session, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public String resumirCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<String>() {
            public String execute(Session session) throws SQLException {
                return resumir(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public String resumirAluno(Session session, Long alunoId, String categoria) throws SQLException {
        return resumir(session, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public String resumirCurso(Session session, Long cursoId, String categoria) throws SQLException {
        return resumir(session, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public String resumirCursoAluno(Session session, Long cursoAlunoId, String categoria) throws SQLException {
        return resumir(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<Long> listarIdsAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<Long>>() {
            public List<Long> execute(Session session) throws SQLException {
                return listarIds(session, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public List<Long> listarIdsCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<Long>>() {
            public List<Long> execute(Session session) throws SQLException {
                return listarIds(session, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public List<Long> listarIdsCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<Long>>() {
            public List<Long> execute(Session session) throws SQLException {
                return listarIds(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public List<Long> listarIdsAluno(Session session, Long alunoId, String categoria) throws SQLException {
        return listarIds(session, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<Long> listarIdsCurso(Session session, Long cursoId, String categoria) throws SQLException {
        return listarIds(session, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<Long> listarIdsCursoAluno(Session session, Long cursoAlunoId, String categoria) throws SQLException {
        return listarIds(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<String> listarCodigosAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<String>>() {
            public List<String> execute(Session session) throws SQLException {
                return listarCodigos(session, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public List<String> listarCodigosCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<String>>() {
            public List<String> execute(Session session) throws SQLException {
                return listarCodigos(session, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public List<String> listarCodigosCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInSession(new SessionWork<List<String>>() {
            public List<String> execute(Session session) throws SQLException {
                return listarCodigos(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public List<String> listarCodigosAluno(Session session, Long alunoId, String categoria) throws SQLException {
        return listarCodigos(session, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<String> listarCodigosCurso(Session session, Long cursoId, String categoria) throws SQLException {
        return listarCodigos(session, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<String> listarCodigosCursoAluno(Session session, Long cursoAlunoId, String categoria)
            throws SQLException {
        return listarCodigos(session, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    private void salvarVinculos(Session session, String tabela, String colunaFk, Long fkValue, long[] opcaoIds) {
        if (fkValue == null || opcaoIds == null || opcaoIds.length == 0) {
            return;
        }
        Set<Long> idsUnicos = new LinkedHashSet<Long>();
        for (int i = 0; i < opcaoIds.length; i++) {
            idsUnicos.add(Long.valueOf(opcaoIds[i]));
        }
        Query insertQuery = session.createSQLQuery(
                "INSERT INTO " + tabela + " (" + colunaFk + ", opcao_id) VALUES (:fkValue, :opcaoId)");
        for (Long opcaoId : idsUnicos) {
            insertQuery.setLong("fkValue", fkValue.longValue());
            insertQuery.setLong("opcaoId", opcaoId.longValue());
            insertQuery.executeUpdate();
        }
    }

    private void removerVinculos(Session session, String tabela, String colunaFk, Long fkValue) {
        if (fkValue == null) {
            return;
        }
        Query deleteQuery = session.createSQLQuery(
                "DELETE FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        deleteQuery.setLong("fkValue", fkValue.longValue());
        deleteQuery.executeUpdate();
    }

    private String resumir(Session session, String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        if (fkValue == null) {
            return "";
        }
        Query query = session.createSQLQuery(
                "SELECT o.nome FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria " +
                        "ORDER BY o.nome");
        query.setLong("fkValue", fkValue.longValue());
        query.setString("categoria", categoria);
        List nomes = query.list();
        StringBuilder resumo = new StringBuilder();
        for (int i = 0; i < nomes.size(); i++) {
            Object nome = nomes.get(i);
            if (nome == null) {
                continue;
            }
            if (resumo.length() > 0) {
                resumo.append(", ");
            }
            resumo.append(nome.toString());
        }
        return resumo.toString();
    }

    private List<Long> listarIds(Session session, String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        if (fkValue == null) {
            return ids;
        }
        Query query = session.createSQLQuery(
                "SELECT o.id FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
        query.setLong("fkValue", fkValue.longValue());
        query.setString("categoria", categoria);
        List rows = query.list();
        for (int i = 0; i < rows.size(); i++) {
            Object value = rows.get(i);
            if (value == null) {
                continue;
            }
            if (value instanceof Number) {
                ids.add(Long.valueOf(((Number) value).longValue()));
            } else {
                ids.add(Long.valueOf(value.toString()));
            }
        }
        return ids;
    }

    private List<String> listarCodigos(Session session, String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        List<String> codigos = new ArrayList<String>();
        if (fkValue == null) {
            return codigos;
        }
        Query query = session.createSQLQuery(
                "SELECT o.codigo FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
        query.setLong("fkValue", fkValue.longValue());
        query.setString("categoria", categoria);
        List rows = query.list();
        for (int i = 0; i < rows.size(); i++) {
            Object value = rows.get(i);
            if (value != null) {
                codigos.add(value.toString());
            }
        }
        return codigos;
    }
}
