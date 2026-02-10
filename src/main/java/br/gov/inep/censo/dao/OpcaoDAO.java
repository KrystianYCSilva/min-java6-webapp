package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.model.OpcaoDominio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de catalogo de opcoes e vinculos 1..N por modulo.
 */
public class OpcaoDAO extends AbstractJdbcDao {

    private static final String SQL_LISTAR_POR_CATEGORIA =
            "SELECT id, categoria, codigo, nome FROM dominio_opcao " +
                    "WHERE categoria = ? AND ativo = TRUE ORDER BY nome";

    public List<OpcaoDominio> listarPorCategoria(String categoria) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<OpcaoDominio> itens = new ArrayList<OpcaoDominio>();

        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTAR_POR_CATEGORIA);
            statement.setString(1, categoria);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OpcaoDominio item = new OpcaoDominio();
                item.setId(Long.valueOf(resultSet.getLong("id")));
                item.setCategoria(resultSet.getString("categoria"));
                item.setCodigo(resultSet.getString("codigo"));
                item.setNome(resultSet.getString("nome"));
                itens.add(item);
            }
            return itens;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public void salvarVinculosAluno(Connection connection, Long alunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(connection, "aluno_opcao", "aluno_id", alunoId, opcaoIds);
    }

    public void salvarVinculosCurso(Connection connection, Long cursoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(connection, "curso_opcao", "curso_id", cursoId, opcaoIds);
    }

    public void salvarVinculosCursoAluno(Connection connection, Long cursoAlunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos(connection, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, opcaoIds);
    }

    public void substituirVinculosAluno(Connection connection, Long alunoId, long[] opcaoIds) throws SQLException {
        removerVinculos(connection, "aluno_opcao", "aluno_id", alunoId);
        salvarVinculosAluno(connection, alunoId, opcaoIds);
    }

    public void substituirVinculosCurso(Connection connection, Long cursoId, long[] opcaoIds) throws SQLException {
        removerVinculos(connection, "curso_opcao", "curso_id", cursoId);
        salvarVinculosCurso(connection, cursoId, opcaoIds);
    }

    public void substituirVinculosCursoAluno(Connection connection, Long cursoAlunoId, long[] opcaoIds)
            throws SQLException {
        removerVinculos(connection, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
        salvarVinculosCursoAluno(connection, cursoAlunoId, opcaoIds);
    }

    public void removerVinculosAluno(Connection connection, Long alunoId) throws SQLException {
        removerVinculos(connection, "aluno_opcao", "aluno_id", alunoId);
    }

    public void removerVinculosCurso(Connection connection, Long cursoId) throws SQLException {
        removerVinculos(connection, "curso_opcao", "curso_id", cursoId);
    }

    public void removerVinculosCursoAluno(Connection connection, Long cursoAlunoId) throws SQLException {
        removerVinculos(connection, "curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
    }

    public String resumirAluno(Long alunoId, String categoria) throws SQLException {
        return resumir("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public String resumirCurso(Long cursoId, String categoria) throws SQLException {
        return resumir("curso_opcao", "curso_id", cursoId, categoria);
    }

    public String resumirCursoAluno(Long cursoAlunoId, String categoria) throws SQLException {
        return resumir("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<Long> listarIdsAluno(Long alunoId, String categoria) throws SQLException {
        return listarIds("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<Long> listarIdsCurso(Long cursoId, String categoria) throws SQLException {
        return listarIds("curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<Long> listarIdsCursoAluno(Long cursoAlunoId, String categoria) throws SQLException {
        return listarIds("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<String> listarCodigosAluno(Long alunoId, String categoria) throws SQLException {
        return listarCodigos("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<String> listarCodigosCurso(Long cursoId, String categoria) throws SQLException {
        return listarCodigos("curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<String> listarCodigosCursoAluno(Long cursoAlunoId, String categoria) throws SQLException {
        return listarCodigos("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    private void salvarVinculos(Connection connection, String tabela, String colunaFk, Long fkValue, long[] opcaoIds)
            throws SQLException {
        if (fkValue == null || opcaoIds == null || opcaoIds.length == 0) {
            return;
        }

        PreparedStatement statement = null;
        String sql = "MERGE INTO " + tabela + " (" + colunaFk + ", opcao_id) KEY(" + colunaFk + ", opcao_id) " +
                "VALUES (?, ?)";

        try {
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < opcaoIds.length; i++) {
                statement.setLong(1, fkValue.longValue());
                statement.setLong(2, opcaoIds[i]);
                statement.addBatch();
            }
            statement.executeBatch();
        } finally {
            closeQuietly(statement);
        }
    }

    private void removerVinculos(Connection connection, String tabela, String colunaFk, Long fkValue)
            throws SQLException {
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

    private String resumir(String tabela, String colunaFk, Long fkValue, String categoria) throws SQLException {
        if (fkValue == null) {
            return "";
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT o.nome FROM " + tabela + " r " +
                "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                "WHERE r." + colunaFk + " = ? AND o.categoria = ? " +
                "ORDER BY o.nome";

        StringBuilder resumo = new StringBuilder();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            statement.setString(2, categoria);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resumo.length() > 0) {
                    resumo.append(", ");
                }
                resumo.append(resultSet.getString("nome"));
            }
            return resumo.toString();
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private List<Long> listarIds(String tabela, String colunaFk, Long fkValue, String categoria) throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        if (fkValue == null) {
            return ids;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "SELECT o.id FROM " + tabela + " r " +
                "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                "WHERE r." + colunaFk + " = ? AND o.categoria = ? ORDER BY o.nome";
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            statement.setString(2, categoria);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ids.add(Long.valueOf(resultSet.getLong("id")));
            }
            return ids;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    private List<String> listarCodigos(String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        List<String> codigos = new ArrayList<String>();
        if (fkValue == null) {
            return codigos;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "SELECT o.codigo FROM " + tabela + " r " +
                "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                "WHERE r." + colunaFk + " = ? AND o.categoria = ? ORDER BY o.nome";
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, fkValue.longValue());
            statement.setString(2, categoria);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                codigos.add(resultSet.getString("codigo"));
            }
            return codigos;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
