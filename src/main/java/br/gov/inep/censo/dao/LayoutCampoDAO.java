package br.gov.inep.censo.dao;

import br.gov.inep.censo.model.LayoutCampo;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para metadados de leiaute e valores complementares de campos.
 */
public class LayoutCampoDAO extends AbstractHibernateDao {

    public List<LayoutCampo> listarPorModulo(final String modulo) throws SQLException {
        return executeInSession(new SessionWork<List<LayoutCampo>>() {
            public List<LayoutCampo> execute(Session session) {
                Query query = session.createQuery(
                        "from LayoutCampo l where l.modulo = :modulo order by l.numeroCampo");
                query.setString("modulo", modulo);
                return query.list();
            }
        });
    }

    public void salvarValoresAluno(Session session, Long alunoId, Map<Long, String> valores) {
        salvarValores(session, "aluno_layout_valor", "aluno_id", alunoId, valores);
    }

    public void salvarValoresCurso(Session session, Long cursoId, Map<Long, String> valores) {
        salvarValores(session, "curso_layout_valor", "curso_id", cursoId, valores);
    }

    public void salvarValoresCursoAluno(Session session, Long cursoAlunoId, Map<Long, String> valores) {
        salvarValores(session, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, valores);
    }

    public void salvarValoresDocente(Session session, Long docenteId, Map<Long, String> valores) {
        salvarValores(session, "docente_layout_valor", "docente_id", docenteId, valores);
    }

    public void salvarValoresIes(Session session, Long iesId, Map<Long, String> valores) {
        salvarValores(session, "ies_layout_valor", "ies_id", iesId, valores);
    }

    public void substituirValoresAluno(Session session, Long alunoId, Map<Long, String> valores) {
        removerValores(session, "aluno_layout_valor", "aluno_id", alunoId);
        salvarValoresAluno(session, alunoId, valores);
    }

    public void substituirValoresCurso(Session session, Long cursoId, Map<Long, String> valores) {
        removerValores(session, "curso_layout_valor", "curso_id", cursoId);
        salvarValoresCurso(session, cursoId, valores);
    }

    public void substituirValoresCursoAluno(Session session, Long cursoAlunoId, Map<Long, String> valores) {
        removerValores(session, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
        salvarValoresCursoAluno(session, cursoAlunoId, valores);
    }

    public void substituirValoresDocente(Session session, Long docenteId, Map<Long, String> valores) {
        removerValores(session, "docente_layout_valor", "docente_id", docenteId);
        salvarValoresDocente(session, docenteId, valores);
    }

    public void substituirValoresIes(Session session, Long iesId, Map<Long, String> valores) {
        removerValores(session, "ies_layout_valor", "ies_id", iesId);
        salvarValoresIes(session, iesId, valores);
    }

    public void removerValoresAluno(Session session, Long alunoId) {
        removerValores(session, "aluno_layout_valor", "aluno_id", alunoId);
    }

    public void removerValoresCurso(Session session, Long cursoId) {
        removerValores(session, "curso_layout_valor", "curso_id", cursoId);
    }

    public void removerValoresCursoAluno(Session session, Long cursoAlunoId) {
        removerValores(session, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public void removerValoresDocente(Session session, Long docenteId) {
        removerValores(session, "docente_layout_valor", "docente_id", docenteId);
    }

    public void removerValoresIes(Session session, Long iesId) {
        removerValores(session, "ies_layout_valor", "ies_id", iesId);
    }

    public Map<Long, String> carregarValoresAlunoPorCampoId(final Long alunoId) throws SQLException {
        return executeInSession(new SessionWork<Map<Long, String>>() {
            public Map<Long, String> execute(Session session) {
                return carregarValoresPorCampoId(session, "aluno_layout_valor", "aluno_id", alunoId);
            }
        });
    }

    public Map<Long, String> carregarValoresCursoPorCampoId(final Long cursoId) throws SQLException {
        return executeInSession(new SessionWork<Map<Long, String>>() {
            public Map<Long, String> execute(Session session) {
                return carregarValoresPorCampoId(session, "curso_layout_valor", "curso_id", cursoId);
            }
        });
    }

    public Map<Long, String> carregarValoresCursoAlunoPorCampoId(final Long cursoAlunoId) throws SQLException {
        return executeInSession(new SessionWork<Map<Long, String>>() {
            public Map<Long, String> execute(Session session) {
                return carregarValoresPorCampoId(session, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
            }
        });
    }

    public Map<Long, String> carregarValoresDocentePorCampoId(final Long docenteId) throws SQLException {
        return executeInSession(new SessionWork<Map<Long, String>>() {
            public Map<Long, String> execute(Session session) {
                return carregarValoresPorCampoId(session, "docente_layout_valor", "docente_id", docenteId);
            }
        });
    }

    public Map<Long, String> carregarValoresIesPorCampoId(final Long iesId) throws SQLException {
        return executeInSession(new SessionWork<Map<Long, String>>() {
            public Map<Long, String> execute(Session session) {
                return carregarValoresPorCampoId(session, "ies_layout_valor", "ies_id", iesId);
            }
        });
    }

    public Map<Integer, String> carregarValoresAlunoPorNumero(final Long alunoId, final String modulo)
            throws SQLException {
        return executeInSession(new SessionWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(Session session) {
                return carregarValoresPorNumero(session, "aluno_layout_valor", "aluno_id", alunoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresCursoPorNumero(final Long cursoId, final String modulo)
            throws SQLException {
        return executeInSession(new SessionWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(Session session) {
                return carregarValoresPorNumero(session, "curso_layout_valor", "curso_id", cursoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresCursoAlunoPorNumero(final Long cursoAlunoId, final String modulo)
            throws SQLException {
        return executeInSession(new SessionWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(Session session) {
                return carregarValoresPorNumero(
                        session, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresDocentePorNumero(final Long docenteId, final String modulo)
            throws SQLException {
        return executeInSession(new SessionWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(Session session) {
                return carregarValoresPorNumero(session, "docente_layout_valor", "docente_id", docenteId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresIesPorNumero(final Long iesId, final String modulo)
            throws SQLException {
        return executeInSession(new SessionWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(Session session) {
                return carregarValoresPorNumero(session, "ies_layout_valor", "ies_id", iesId, modulo);
            }
        });
    }

    public Map<Integer, Long> mapaCampoIdPorNumero(String modulo) throws SQLException {
        List<LayoutCampo> campos = listarPorModulo(modulo);
        Map<Integer, Long> mapa = new LinkedHashMap<Integer, Long>();
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            mapa.put(campo.getNumeroCampo(), campo.getId());
        }
        return mapa;
    }

    private void salvarValores(Session session,
                               String tabela,
                               String colunaFk,
                               Long fkValue,
                               Map<Long, String> valores) {
        if (fkValue == null || valores == null || valores.isEmpty()) {
            return;
        }
        Query query = session.createSQLQuery(
                "INSERT INTO " + tabela + " (" + colunaFk + ", layout_campo_id, valor) " +
                        "VALUES (:fkValue, :layoutCampoId, :valor)");
        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String valor = trimToNull(entry.getValue());
            if (valor == null) {
                continue;
            }
            query.setLong("fkValue", fkValue.longValue());
            query.setLong("layoutCampoId", entry.getKey().longValue());
            query.setString("valor", valor);
            query.executeUpdate();
        }
    }

    private Map<Long, String> carregarValoresPorCampoId(Session session,
                                                        String tabela,
                                                        String colunaFk,
                                                        Long fkValue) {
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (fkValue == null) {
            return valores;
        }
        Query query = session.createSQLQuery(
                "SELECT layout_campo_id, valor FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        query.setLong("fkValue", fkValue.longValue());
        List rows = query.list();
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (!(row instanceof Object[])) {
                continue;
            }
            Object[] values = (Object[]) row;
            if (values.length < 2 || values[0] == null) {
                continue;
            }
            valores.put(toLong(values[0]), values[1] == null ? null : values[1].toString());
        }
        return valores;
    }

    private Map<Integer, String> carregarValoresPorNumero(Session session,
                                                          String tabela,
                                                          String colunaFk,
                                                          Long fkValue,
                                                          String modulo) {
        Map<Integer, String> valores = new LinkedHashMap<Integer, String>();
        if (fkValue == null) {
            return valores;
        }
        Query query = session.createSQLQuery(
                "SELECT c.numero_campo, v.valor FROM " + tabela + " v " +
                        "INNER JOIN layout_campo c ON c.id = v.layout_campo_id " +
                        "WHERE v." + colunaFk + " = :fkValue AND c.modulo = :modulo");
        query.setLong("fkValue", fkValue.longValue());
        query.setString("modulo", modulo);
        List rows = query.list();
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (!(row instanceof Object[])) {
                continue;
            }
            Object[] values = (Object[]) row;
            if (values.length < 2 || values[0] == null) {
                continue;
            }
            valores.put(toInteger(values[0]), values[1] == null ? null : values[1].toString());
        }
        return valores;
    }

    private void removerValores(Session session, String tabela, String colunaFk, Long fkValue) {
        if (fkValue == null) {
            return;
        }
        Query query = session.createSQLQuery("DELETE FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        query.setLong("fkValue", fkValue.longValue());
        query.executeUpdate();
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        return Long.valueOf(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        return Integer.valueOf(value.toString());
    }
}
