package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Docente (Registro 31), incluindo campos complementares de leiaute.
 */
public class DocenteDAO extends AbstractJdbcDao {

    private static final String SQL_INSERT =
            "INSERT INTO docente (" +
                    "id_docente_ies, nome, cpf, documento_estrangeiro, data_nascimento, " +
                    "cor_raca, nacionalidade, pais_origem, uf_nascimento, municipio_nascimento, docente_deficiencia" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE docente SET id_docente_ies = ?, nome = ?, cpf = ?, documento_estrangeiro = ?, " +
                    "data_nascimento = ?, cor_raca = ?, nacionalidade = ?, pais_origem = ?, " +
                    "uf_nascimento = ?, municipio_nascimento = ?, docente_deficiencia = ? WHERE id = ?";

    private static final String SQL_LISTA =
            "SELECT id, id_docente_ies, nome, cpf, documento_estrangeiro, data_nascimento, " +
                    "cor_raca, nacionalidade, pais_origem, uf_nascimento, municipio_nascimento, docente_deficiencia " +
                    "FROM docente ORDER BY nome";

    private static final String SQL_PAGINADO =
            "SELECT id, id_docente_ies, nome, cpf, documento_estrangeiro, data_nascimento, " +
                    "cor_raca, nacionalidade, pais_origem, uf_nascimento, municipio_nascimento, docente_deficiencia " +
                    "FROM docente ORDER BY nome LIMIT ? OFFSET ?";

    private static final String SQL_COUNT = "SELECT COUNT(1) AS total FROM docente";
    private static final String SQL_BY_ID =
            "SELECT id, id_docente_ies, nome, cpf, documento_estrangeiro, data_nascimento, " +
                    "cor_raca, nacionalidade, pais_origem, uf_nascimento, municipio_nascimento, docente_deficiencia " +
                    "FROM docente WHERE id = ?";

    private final LayoutCampoDAO layoutCampoDAO;

    public DocenteDAO() {
        this(new LayoutCampoDAO());
    }

    public DocenteDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(Docente docente, Map<Long, String> camposComplementares) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preencherCampos(statement, docente);
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Falha ao gerar ID para docente.");
            }
            Long docenteId = Long.valueOf(generatedKeys.getLong(1));
            layoutCampoDAO.salvarValoresDocente(connection, docenteId, camposComplementares);
            connection.commit();
            return docenteId;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public void atualizar(Docente docente, Map<Long, String> camposComplementares) throws SQLException {
        if (docente == null || docente.getId() == null) {
            throw new IllegalArgumentException("Docente/ID nao informado para atualizacao.");
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQL_UPDATE);
            preencherCampos(statement, docente);
            statement.setLong(12, docente.getId().longValue());
            statement.executeUpdate();

            layoutCampoDAO.substituirValoresDocente(connection, docente.getId(), camposComplementares);
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public Docente buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_BY_ID);
            statement.setLong(1, id.longValue());
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return mapDocente(resultSet);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Docente> listar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Docente> docentes = new ArrayList<Docente>();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTA);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                docentes.add(mapDocente(resultSet));
            }
            return docentes;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Docente> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        int page = pagina <= 0 ? 1 : pagina;
        int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
        int offset = (page - 1) * size;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Docente> docentes = new ArrayList<Docente>();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_PAGINADO);
            statement.setInt(1, size);
            statement.setInt(2, offset);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                docentes.add(mapDocente(resultSet));
            }
            return docentes;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public int contar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_COUNT);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            return 0;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public void excluir(Long id) throws SQLException {
        if (id == null) {
            return;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            layoutCampoDAO.removerValoresDocente(connection, id);
            statement = connection.prepareStatement("DELETE FROM docente WHERE id = ?");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long docenteId) throws SQLException {
        return layoutCampoDAO.carregarValoresDocentePorCampoId(docenteId);
    }

    public Map<Integer, String> carregarCamposRegistro31PorNumero(Long docenteId) throws SQLException {
        return layoutCampoDAO.carregarValoresDocentePorNumero(docenteId, ModulosLayout.DOCENTE_31);
    }

    private void preencherCampos(PreparedStatement statement, Docente docente) throws SQLException {
        setNullableString(statement, 1, docente.getIdDocenteIes());
        statement.setString(2, docente.getNome());
        statement.setString(3, docente.getCpf());
        setNullableString(statement, 4, docente.getDocumentoEstrangeiro());
        statement.setDate(5, docente.getDataNascimento());
        setNullableInteger(statement, 6, docente.getCorRaca());
        statement.setInt(7, docente.getNacionalidade().intValue());
        statement.setString(8, docente.getPaisOrigem());
        setNullableInteger(statement, 9, docente.getUfNascimento());
        setNullableString(statement, 10, docente.getMunicipioNascimento());
        setNullableInteger(statement, 11, docente.getDocenteDeficiencia());
    }

    private Docente mapDocente(ResultSet resultSet) throws SQLException {
        Docente docente = new Docente();
        docente.setId(Long.valueOf(resultSet.getLong("id")));
        docente.setIdDocenteIes(resultSet.getString("id_docente_ies"));
        docente.setNome(resultSet.getString("nome"));
        docente.setCpf(resultSet.getString("cpf"));
        docente.setDocumentoEstrangeiro(resultSet.getString("documento_estrangeiro"));
        docente.setDataNascimento(resultSet.getDate("data_nascimento"));
        docente.setCorRaca(getNullableInt(resultSet, "cor_raca"));
        docente.setNacionalidade(Integer.valueOf(resultSet.getInt("nacionalidade")));
        docente.setPaisOrigem(resultSet.getString("pais_origem"));
        docente.setUfNascimento(getNullableInt(resultSet, "uf_nascimento"));
        docente.setMunicipioNascimento(resultSet.getString("municipio_nascimento"));
        docente.setDocenteDeficiencia(getNullableInt(resultSet, "docente_deficiencia"));
        return docente;
    }

    private void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
                // noop
            }
        }
    }

    private void restoreAutoCommitAndClose(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
                // noop
            }
            closeQuietly(connection);
        }
    }
}
