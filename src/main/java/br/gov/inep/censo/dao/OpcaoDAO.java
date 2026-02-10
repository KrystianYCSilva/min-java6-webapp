package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.OpcaoDominio;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO de catalogo de opcoes e vinculos 1..N por modulo.
 */
public class OpcaoDAO extends AbstractJpaDao {

    public List<OpcaoDominio> listarPorCategoria(final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<OpcaoDominio>>() {
            public List<OpcaoDominio> execute(EntityManager entityManager) {
                TypedQuery<OpcaoDominio> query = entityManager.createQuery(
                        "select o from OpcaoDominio o where o.categoria = :categoria order by o.nome",
                        OpcaoDominio.class);
                query.setParameter("categoria", categoria);
                return query.getResultList();
            }
        });
    }

    public void salvarVinculosAluno(EntityManager entityManager, Long alunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(entityManager, "aluno_opcao", "aluno_id", alunoId, opcaoIds);
    }

    public void salvarVinculosCurso(EntityManager entityManager, Long cursoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(entityManager, "curso_opcao", "curso_id", cursoId, opcaoIds);
    }

    public void salvarVinculosCursoAluno(EntityManager entityManager, Long cursoAlunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, opcaoIds);
    }

    public void substituirVinculosAluno(EntityManager entityManager, Long alunoId, long[] opcaoIds) throws SQLException {
        removerVinculos(entityManager, "aluno_opcao", "aluno_id", alunoId);
        salvarVinculosAluno(entityManager, alunoId, opcaoIds);
    }

    public void substituirVinculosCurso(EntityManager entityManager, Long cursoId, long[] opcaoIds) throws SQLException {
        removerVinculos(entityManager, "curso_opcao", "curso_id", cursoId);
        salvarVinculosCurso(entityManager, cursoId, opcaoIds);
    }

    public void substituirVinculosCursoAluno(EntityManager entityManager, Long cursoAlunoId, long[] opcaoIds)
            throws SQLException {
        removerVinculos(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
        salvarVinculosCursoAluno(entityManager, cursoAlunoId, opcaoIds);
    }

    public void removerVinculosAluno(EntityManager entityManager, Long alunoId) throws SQLException {
        removerVinculos(entityManager, "aluno_opcao", "aluno_id", alunoId);
    }

    public void removerVinculosCurso(EntityManager entityManager, Long cursoId) throws SQLException {
        removerVinculos(entityManager, "curso_opcao", "curso_id", cursoId);
    }

    public void removerVinculosCursoAluno(EntityManager entityManager, Long cursoAlunoId) throws SQLException {
        removerVinculos(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
    }

    public String resumirAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<String>() {
            public String execute(EntityManager entityManager) throws SQLException {
                return resumir(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public String resumirCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<String>() {
            public String execute(EntityManager entityManager) throws SQLException {
                return resumir(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public String resumirCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<String>() {
            public String execute(EntityManager entityManager) throws SQLException {
                return resumir(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public String resumirAluno(EntityManager entityManager, Long alunoId, String categoria) throws SQLException {
        return resumir(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public String resumirCurso(EntityManager entityManager, Long cursoId, String categoria) throws SQLException {
        return resumir(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public String resumirCursoAluno(EntityManager entityManager, Long cursoAlunoId, String categoria) throws SQLException {
        return resumir(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<Long> listarIdsAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Long>>() {
            public List<Long> execute(EntityManager entityManager) throws SQLException {
                return listarIds(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public List<Long> listarIdsCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Long>>() {
            public List<Long> execute(EntityManager entityManager) throws SQLException {
                return listarIds(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public List<Long> listarIdsCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<Long>>() {
            public List<Long> execute(EntityManager entityManager) throws SQLException {
                return listarIds(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public List<Long> listarIdsAluno(EntityManager entityManager, Long alunoId, String categoria) throws SQLException {
        return listarIds(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<Long> listarIdsCurso(EntityManager entityManager, Long cursoId, String categoria) throws SQLException {
        return listarIds(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<Long> listarIdsCursoAluno(EntityManager entityManager, Long cursoAlunoId, String categoria) throws SQLException {
        return listarIds(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<String> listarCodigosAluno(final Long alunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<String>>() {
            public List<String> execute(EntityManager entityManager) throws SQLException {
                return listarCodigos(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
            }
        });
    }

    public List<String> listarCodigosCurso(final Long cursoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<String>>() {
            public List<String> execute(EntityManager entityManager) throws SQLException {
                return listarCodigos(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
            }
        });
    }

    public List<String> listarCodigosCursoAluno(final Long cursoAlunoId, final String categoria) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<String>>() {
            public List<String> execute(EntityManager entityManager) throws SQLException {
                return listarCodigos(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
            }
        });
    }

    public List<String> listarCodigosAluno(EntityManager entityManager, Long alunoId, String categoria) throws SQLException {
        return listarCodigos(entityManager, "aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<String> listarCodigosCurso(EntityManager entityManager, Long cursoId, String categoria) throws SQLException {
        return listarCodigos(entityManager, "curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<String> listarCodigosCursoAluno(EntityManager entityManager, Long cursoAlunoId, String categoria)
            throws SQLException {
        return listarCodigos(entityManager, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    private void salvarVinculos(EntityManager entityManager, String tabela, String colunaFk, Long fkValue, long[] opcaoIds) {
        if (fkValue == null || opcaoIds == null || opcaoIds.length == 0) {
            return;
        }
        Set<Long> idsUnicos = new LinkedHashSet<Long>();
        for (int i = 0; i < opcaoIds.length; i++) {
            idsUnicos.add(Long.valueOf(opcaoIds[i]));
        }
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO " + tabela + " (" + colunaFk + ", opcao_id) VALUES (:fkValue, :opcaoId)");
        for (Long opcaoId : idsUnicos) {
            insertQuery.setParameter("fkValue", fkValue.longValue());
            insertQuery.setParameter("opcaoId", opcaoId.longValue());
            insertQuery.executeUpdate();
        }
    }

    private void removerVinculos(EntityManager entityManager, String tabela, String colunaFk, Long fkValue) {
        if (fkValue == null) {
            return;
        }
        Query deleteQuery = entityManager.createNativeQuery(
                "DELETE FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        deleteQuery.setParameter("fkValue", fkValue.longValue());
        deleteQuery.executeUpdate();
    }

    private String resumir(EntityManager entityManager, String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        if (fkValue == null) {
            return "";
        }
        Query query = entityManager.createNativeQuery(
                "SELECT o.nome FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria " +
                        "ORDER BY o.nome");
        query.setParameter("fkValue", fkValue.longValue());
        query.setParameter("categoria", categoria);
        List nomes = query.getResultList();
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

    private List<Long> listarIds(EntityManager entityManager, String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        if (fkValue == null) {
            return ids;
        }
        Query query = entityManager.createNativeQuery(
                "SELECT o.id FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
        query.setParameter("fkValue", fkValue.longValue());
        query.setParameter("categoria", categoria);
        List rows = query.getResultList();
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

    private List<String> listarCodigos(EntityManager entityManager,
                                       String tabela,
                                       String colunaFk,
                                       Long fkValue,
                                       String categoria)
            throws SQLException {
        List<String> codigos = new ArrayList<String>();
        if (fkValue == null) {
            return codigos;
        }
        Query query = entityManager.createNativeQuery(
                "SELECT o.codigo FROM " + tabela + " r " +
                        "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                        "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
        query.setParameter("fkValue", fkValue.longValue());
        query.setParameter("categoria", categoria);
        List rows = query.getResultList();
        for (int i = 0; i < rows.size(); i++) {
            Object value = rows.get(i);
            if (value != null) {
                codigos.add(value.toString());
            }
        }
        return codigos;
    }
}
