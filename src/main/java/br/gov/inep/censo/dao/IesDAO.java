package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo IES com foco no Registro 11 (laboratorio) e campos complementares.
 */
public class IesDAO extends AbstractJdbcDao {

    private static final String SQL_INSERT =
            "INSERT INTO ies (" +
                    "id_ies_inep, nome_laboratorio, registro_laboratorio_ies, laboratorio_ativo_ano, " +
                    "descricao_atividades, palavras_chave, laboratorio_informatica, tipo_laboratorio, " +
                    "codigo_uf_laboratorio, codigo_municipio_laboratorio" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE ies SET id_ies_inep = ?, nome_laboratorio = ?, registro_laboratorio_ies = ?, " +
                    "laboratorio_ativo_ano = ?, descricao_atividades = ?, palavras_chave = ?, " +
                    "laboratorio_informatica = ?, tipo_laboratorio = ?, codigo_uf_laboratorio = ?, " +
                    "codigo_municipio_laboratorio = ? WHERE id = ?";

    private static final String SQL_LISTA =
            "SELECT id, id_ies_inep, nome_laboratorio, registro_laboratorio_ies, laboratorio_ativo_ano, " +
                    "descricao_atividades, palavras_chave, laboratorio_informatica, tipo_laboratorio, " +
                    "codigo_uf_laboratorio, codigo_municipio_laboratorio " +
                    "FROM ies ORDER BY nome_laboratorio";

    private static final String SQL_PAGINADO =
            "SELECT id, id_ies_inep, nome_laboratorio, registro_laboratorio_ies, laboratorio_ativo_ano, " +
                    "descricao_atividades, palavras_chave, laboratorio_informatica, tipo_laboratorio, " +
                    "codigo_uf_laboratorio, codigo_municipio_laboratorio " +
                    "FROM ies ORDER BY nome_laboratorio LIMIT ? OFFSET ?";

    private static final String SQL_COUNT = "SELECT COUNT(1) AS total FROM ies";
    private static final String SQL_BY_ID =
            "SELECT id, id_ies_inep, nome_laboratorio, registro_laboratorio_ies, laboratorio_ativo_ano, " +
                    "descricao_atividades, palavras_chave, laboratorio_informatica, tipo_laboratorio, " +
                    "codigo_uf_laboratorio, codigo_municipio_laboratorio " +
                    "FROM ies WHERE id = ?";

    private final LayoutCampoDAO layoutCampoDAO;

    public IesDAO() {
        this(new LayoutCampoDAO());
    }

    public IesDAO(LayoutCampoDAO layoutCampoDAO) {
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(Ies ies, Map<Long, String> camposComplementares) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preencherCampos(statement, ies);
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Falha ao gerar ID para IES.");
            }
            Long iesId = Long.valueOf(generatedKeys.getLong(1));
            layoutCampoDAO.salvarValoresIes(connection, iesId, camposComplementares);
            connection.commit();
            return iesId;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public void atualizar(Ies ies, Map<Long, String> camposComplementares) throws SQLException {
        if (ies == null || ies.getId() == null) {
            throw new IllegalArgumentException("IES/ID nao informada para atualizacao.");
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQL_UPDATE);
            preencherCampos(statement, ies);
            statement.setLong(11, ies.getId().longValue());
            statement.executeUpdate();

            layoutCampoDAO.substituirValoresIes(connection, ies.getId(), camposComplementares);
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public Ies buscarPorId(Long id) throws SQLException {
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
            return mapIes(resultSet);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Ies> listar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Ies> itens = new ArrayList<Ies>();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTA);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                itens.add(mapIes(resultSet));
            }
            return itens;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Ies> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        int page = pagina <= 0 ? 1 : pagina;
        int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
        int offset = (page - 1) * size;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Ies> itens = new ArrayList<Ies>();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_PAGINADO);
            statement.setInt(1, size);
            statement.setInt(2, offset);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                itens.add(mapIes(resultSet));
            }
            return itens;
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
            layoutCampoDAO.removerValoresIes(connection, id);
            statement = connection.prepareStatement("DELETE FROM ies WHERE id = ?");
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

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long iesId) throws SQLException {
        return layoutCampoDAO.carregarValoresIesPorCampoId(iesId);
    }

    public Map<Integer, String> carregarCamposRegistro11PorNumero(Long iesId) throws SQLException {
        return layoutCampoDAO.carregarValoresIesPorNumero(iesId, ModulosLayout.IES_11);
    }

    private void preencherCampos(PreparedStatement statement, Ies ies) throws SQLException {
        setNullableLong(statement, 1, ies.getIdIesInep());
        statement.setString(2, ies.getNomeLaboratorio());
        setNullableString(statement, 3, ies.getRegistroLaboratorioIes());
        setNullableInteger(statement, 4, ies.getLaboratorioAtivoAno());
        setNullableString(statement, 5, ies.getDescricaoAtividades());
        setNullableString(statement, 6, ies.getPalavrasChave());
        setNullableInteger(statement, 7, ies.getLaboratorioInformatica());
        setNullableInteger(statement, 8, ies.getTipoLaboratorio());
        setNullableInteger(statement, 9, ies.getCodigoUfLaboratorio());
        setNullableString(statement, 10, ies.getCodigoMunicipioLaboratorio());
    }

    private Ies mapIes(ResultSet resultSet) throws SQLException {
        Ies ies = new Ies();
        ies.setId(Long.valueOf(resultSet.getLong("id")));
        long idIesInep = resultSet.getLong("id_ies_inep");
        if (!resultSet.wasNull()) {
            ies.setIdIesInep(Long.valueOf(idIesInep));
        }
        ies.setNomeLaboratorio(resultSet.getString("nome_laboratorio"));
        ies.setRegistroLaboratorioIes(resultSet.getString("registro_laboratorio_ies"));
        ies.setLaboratorioAtivoAno(getNullableInt(resultSet, "laboratorio_ativo_ano"));
        ies.setDescricaoAtividades(resultSet.getString("descricao_atividades"));
        ies.setPalavrasChave(resultSet.getString("palavras_chave"));
        ies.setLaboratorioInformatica(getNullableInt(resultSet, "laboratorio_informatica"));
        ies.setTipoLaboratorio(getNullableInt(resultSet, "tipo_laboratorio"));
        ies.setCodigoUfLaboratorio(getNullableInt(resultSet, "codigo_uf_laboratorio"));
        ies.setCodigoMunicipioLaboratorio(resultSet.getString("codigo_municipio_laboratorio"));
        return ies;
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
