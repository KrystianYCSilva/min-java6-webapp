package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.model.LayoutCampo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para metadados de leiaute e valores complementares de campos.
 */
public class LayoutCampoDAO extends AbstractJdbcDao {

    private static final String SQL_LISTAR_POR_MODULO =
            "SELECT id, modulo, numero_campo, nome_campo, obrigatoriedade " +
                    "FROM layout_campo WHERE modulo = ? ORDER BY numero_campo";

    public List<LayoutCampo> listarPorModulo(String modulo) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<LayoutCampo> campos = new ArrayList<LayoutCampo>();

        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTAR_POR_MODULO);
            statement.setString(1, modulo);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LayoutCampo campo = new LayoutCampo();
                campo.setId(Long.valueOf(resultSet.getLong("id")));
                campo.setModulo(resultSet.getString("modulo"));
                campo.setNumeroCampo(Integer.valueOf(resultSet.getInt("numero_campo")));
                campo.setNomeCampo(resultSet.getString("nome_campo"));
                campo.setObrigatoriedade(resultSet.getString("obrigatoriedade"));
                campos.add(campo);
            }
            return campos;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public void salvarValoresAluno(Connection connection, Long alunoId, Map<Long, String> valores) throws SQLException {
        salvarValores(connection, "aluno_layout_valor", "aluno_id", alunoId, valores);
    }

    public void salvarValoresCurso(Connection connection, Long cursoId, Map<Long, String> valores) throws SQLException {
        salvarValores(connection, "curso_layout_valor", "curso_id", cursoId, valores);
    }

    public void salvarValoresCursoAluno(Connection connection, Long cursoAlunoId, Map<Long, String> valores)
            throws SQLException {
        salvarValores(connection, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, valores);
    }

    public void salvarValoresDocente(Connection connection, Long docenteId, Map<Long, String> valores)
            throws SQLException {
        salvarValores(connection, "docente_layout_valor", "docente_id", docenteId, valores);
    }

    public void salvarValoresIes(Connection connection, Long iesId, Map<Long, String> valores)
            throws SQLException {
        salvarValores(connection, "ies_layout_valor", "ies_id", iesId, valores);
    }

    public void substituirValoresAluno(Connection connection, Long alunoId, Map<Long, String> valores) throws SQLException {
        removerValores(connection, "aluno_layout_valor", "aluno_id", alunoId);
        salvarValoresAluno(connection, alunoId, valores);
    }

    public void substituirValoresCurso(Connection connection, Long cursoId, Map<Long, String> valores) throws SQLException {
        removerValores(connection, "curso_layout_valor", "curso_id", cursoId);
        salvarValoresCurso(connection, cursoId, valores);
    }

    public void substituirValoresCursoAluno(Connection connection, Long cursoAlunoId, Map<Long, String> valores)
            throws SQLException {
        removerValores(connection, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
        salvarValoresCursoAluno(connection, cursoAlunoId, valores);
    }

    public void substituirValoresDocente(Connection connection, Long docenteId, Map<Long, String> valores)
            throws SQLException {
        removerValores(connection, "docente_layout_valor", "docente_id", docenteId);
        salvarValoresDocente(connection, docenteId, valores);
    }

    public void substituirValoresIes(Connection connection, Long iesId, Map<Long, String> valores)
            throws SQLException {
        removerValores(connection, "ies_layout_valor", "ies_id", iesId);
        salvarValoresIes(connection, iesId, valores);
    }

    public void removerValoresAluno(Connection connection, Long alunoId) throws SQLException {
        removerValores(connection, "aluno_layout_valor", "aluno_id", alunoId);
    }

    public void removerValoresCurso(Connection connection, Long cursoId) throws SQLException {
        removerValores(connection, "curso_layout_valor", "curso_id", cursoId);
    }

    public void removerValoresCursoAluno(Connection connection, Long cursoAlunoId) throws SQLException {
        removerValores(connection, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public void removerValoresDocente(Connection connection, Long docenteId) throws SQLException {
        removerValores(connection, "docente_layout_valor", "docente_id", docenteId);
    }

    public void removerValoresIes(Connection connection, Long iesId) throws SQLException {
        removerValores(connection, "ies_layout_valor", "ies_id", iesId);
    }

    private void salvarValores(Connection connection,
                               String tabela,
                               String colunaFk,
                               Long fkValue,
                               Map<Long, String> valores) throws SQLException {
        if (fkValue == null || valores == null || valores.isEmpty()) {
            return;
        }

        PreparedStatement statement = null;
        String sql = "MERGE INTO " + tabela + " (" + colunaFk + ", layout_campo_id, valor) " +
                "KEY(" + colunaFk + ", layout_campo_id) VALUES (?, ?, ?)";

        try {
            statement = connection.prepareStatement(sql);
            for (Map.Entry<Long, String> entry : valores.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                String value = entry.getValue();
                if (value == null || value.trim().length() == 0) {
                    continue;
                }

                statement.setLong(1, fkValue.longValue());
                statement.setLong(2, entry.getKey().longValue());
                statement.setString(3, value.trim());
                statement.addBatch();
            }
            statement.executeBatch();
        } finally {
            closeQuietly(statement);
        }
    }

    public Map<Long, String> carregarValores(Connection connection,
                                             String tabela,
                                             String colunaFk,
                                             Long fkValue) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        java.util.LinkedHashMap<Long, String> valores = new java.util.LinkedHashMap<Long, String>();
        String sql = "SELECT layout_campo_id, valor FROM " + tabela + " WHERE " + colunaFk + " = ?";

        try {
            statement = connection.prepareStatement(sql);
            if (fkValue == null) {
                statement.setNull(1, Types.BIGINT);
            } else {
                statement.setLong(1, fkValue.longValue());
            }
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                valores.put(Long.valueOf(resultSet.getLong("layout_campo_id")), resultSet.getString("valor"));
            }
            return valores;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
        }
    }

    public Map<Long, String> carregarValoresAlunoPorCampoId(Long alunoId) throws SQLException {
        return carregarValoresPorCampoId("aluno_layout_valor", "aluno_id", alunoId);
    }

    public Map<Long, String> carregarValoresCursoPorCampoId(Long cursoId) throws SQLException {
        return carregarValoresPorCampoId("curso_layout_valor", "curso_id", cursoId);
    }

    public Map<Long, String> carregarValoresCursoAlunoPorCampoId(Long cursoAlunoId) throws SQLException {
        return carregarValoresPorCampoId("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public Map<Long, String> carregarValoresDocentePorCampoId(Long docenteId) throws SQLException {
        return carregarValoresPorCampoId("docente_layout_valor", "docente_id", docenteId);
    }

    public Map<Long, String> carregarValoresIesPorCampoId(Long iesId) throws SQLException {
        return carregarValoresPorCampoId("ies_layout_valor", "ies_id", iesId);
    }

    public Map<Integer, String> carregarValoresAlunoPorNumero(Long alunoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("aluno_layout_valor", "aluno_id", alunoId, modulo);
    }

    public Map<Integer, String> carregarValoresCursoPorNumero(Long cursoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("curso_layout_valor", "curso_id", cursoId, modulo);
    }

    public Map<Integer, String> carregarValoresCursoAlunoPorNumero(Long cursoAlunoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, modulo);
    }

    public Map<Integer, String> carregarValoresDocentePorNumero(Long docenteId, String modulo) throws SQLException {
        return carregarValoresPorNumero("docente_layout_valor", "docente_id", docenteId, modulo);
    }

    public Map<Integer, String> carregarValoresIesPorNumero(Long iesId, String modulo) throws SQLException {
        return carregarValoresPorNumero("ies_layout_valor", "ies_id", iesId, modulo);
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

    private Map<Long, String> carregarValoresPorCampoId(String tabela, String colunaFk, Long fkValue) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Map<Long, String> valores = new LinkedHashMap<Long, String>();

        String sql = "SELECT layout_campo_id, valor FROM " + tabela + " WHERE " + colunaFk + " = ?";
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                valores.put(Long.valueOf(resultSet.getLong("layout_campo_id")), resultSet.getString("valor"));
            }
            return valores;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private Map<Integer, String> carregarValoresPorNumero(String tabela, String colunaFk, Long fkValue, String modulo)
            throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Map<Integer, String> valores = new LinkedHashMap<Integer, String>();

        String sql = "SELECT c.numero_campo, v.valor " +
                "FROM " + tabela + " v INNER JOIN layout_campo c ON c.id = v.layout_campo_id " +
                "WHERE v." + colunaFk + " = ? AND c.modulo = ?";
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            statement.setString(2, modulo);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                valores.put(Integer.valueOf(resultSet.getInt("numero_campo")), resultSet.getString("valor"));
            }
            return valores;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private void removerValores(Connection connection, String tabela, String colunaFk, Long fkValue) throws SQLException {
        if (fkValue == null) {
            return;
        }
        PreparedStatement statement = null;
        String sql = "DELETE FROM " + tabela + " WHERE " + colunaFk + " = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            statement.executeUpdate();
        } finally {
            closeQuietly(statement);
        }
    }
}
