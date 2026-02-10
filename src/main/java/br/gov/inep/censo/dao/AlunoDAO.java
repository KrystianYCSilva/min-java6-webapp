package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Aluno (Registro 41), incluindo opcoes normalizadas e campos complementares.
 */
public class AlunoDAO extends AbstractJdbcDao {

    private static final String SQL_INSERT =
            "INSERT INTO aluno (id_aluno_inep, nome, cpf, data_nascimento, cor_raca, nacionalidade, uf_nascimento, municipio_nascimento, pais_origem) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE aluno SET id_aluno_inep = ?, nome = ?, cpf = ?, data_nascimento = ?, cor_raca = ?, nacionalidade = ?, " +
                    "uf_nascimento = ?, municipio_nascimento = ?, pais_origem = ? WHERE id = ?";

    private static final String SQL_LISTA =
            "SELECT id, id_aluno_inep, nome, cpf, data_nascimento, cor_raca, nacionalidade, uf_nascimento, municipio_nascimento, pais_origem " +
                    "FROM aluno ORDER BY nome";

    private static final String SQL_PAGINADO =
            "SELECT id, id_aluno_inep, nome, cpf, data_nascimento, cor_raca, nacionalidade, uf_nascimento, municipio_nascimento, pais_origem " +
                    "FROM aluno ORDER BY nome LIMIT ? OFFSET ?";

    private static final String SQL_COUNT = "SELECT COUNT(1) AS total FROM aluno";
    private static final String SQL_BY_ID =
            "SELECT id, id_aluno_inep, nome, cpf, data_nascimento, cor_raca, nacionalidade, uf_nascimento, municipio_nascimento, pais_origem " +
                    "FROM aluno WHERE id = ?";

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public AlunoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public AlunoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(Aluno aluno, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preencherCampos(statement, aluno);
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Falha ao gerar ID para aluno.");
            }
            Long alunoId = Long.valueOf(generatedKeys.getLong(1));

            opcaoDAO.salvarVinculosAluno(connection, alunoId, opcaoIds);
            layoutCampoDAO.salvarValoresAluno(connection, alunoId, camposComplementares);

            connection.commit();
            return alunoId;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public void atualizar(Aluno aluno, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        if (aluno == null || aluno.getId() == null) {
            throw new IllegalArgumentException("Aluno/ID nao informado para atualizacao.");
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(SQL_UPDATE);
            preencherCampos(statement, aluno);
            statement.setLong(10, aluno.getId().longValue());
            statement.executeUpdate();

            opcaoDAO.substituirVinculosAluno(connection, aluno.getId(), opcaoIds);
            layoutCampoDAO.substituirValoresAluno(connection, aluno.getId(), camposComplementares);

            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public Aluno buscarPorId(Long id) throws SQLException {
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
            return mapAluno(resultSet);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Aluno> listar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Aluno> alunos = new ArrayList<Aluno>();

        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTA);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                alunos.add(mapAluno(resultSet));
            }
            return alunos;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Aluno> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        int page = pagina <= 0 ? 1 : pagina;
        int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
        int offset = (page - 1) * size;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Aluno> alunos = new ArrayList<Aluno>();

        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_PAGINADO);
            statement.setInt(1, size);
            statement.setInt(2, offset);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                alunos.add(mapAluno(resultSet));
            }
            return alunos;
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

            statement = connection.prepareStatement(
                    "DELETE FROM curso_aluno_opcao WHERE curso_aluno_id IN (SELECT id FROM curso_aluno WHERE aluno_id = ?)");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            statement = connection.prepareStatement(
                    "DELETE FROM curso_aluno_layout_valor WHERE curso_aluno_id IN (SELECT id FROM curso_aluno WHERE aluno_id = ?)");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            statement = connection.prepareStatement("DELETE FROM curso_aluno WHERE aluno_id = ?");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            opcaoDAO.removerVinculosAluno(connection, id);
            layoutCampoDAO.removerValoresAluno(connection, id);

            statement = connection.prepareStatement("DELETE FROM aluno WHERE id = ?");
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

    public List<Long> listarOpcaoDeficienciaIds(Long alunoId) throws SQLException {
        return opcaoDAO.listarIdsAluno(alunoId, CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
    }

    public List<String> listarOpcaoDeficienciaCodigos(Long alunoId) throws SQLException {
        return opcaoDAO.listarCodigosAluno(alunoId, CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long alunoId) throws SQLException {
        return layoutCampoDAO.carregarValoresAlunoPorCampoId(alunoId);
    }

    public Map<Integer, String> carregarCamposRegistro41PorNumero(Long alunoId) throws SQLException {
        return layoutCampoDAO.carregarValoresAlunoPorNumero(alunoId, ModulosLayout.ALUNO_41);
    }

    private void preencherCampos(PreparedStatement statement, Aluno aluno) throws SQLException {
        setNullableLong(statement, 1, aluno.getIdAlunoInep());
        statement.setString(2, aluno.getNome());
        statement.setString(3, aluno.getCpf());
        statement.setDate(4, aluno.getDataNascimento());
        setNullableInteger(statement, 5, aluno.getCorRaca());
        statement.setInt(6, aluno.getNacionalidade().intValue());
        setNullableString(statement, 7, aluno.getUfNascimento());
        setNullableString(statement, 8, aluno.getMunicipioNascimento());
        statement.setString(9, aluno.getPaisOrigem());
    }

    private Aluno mapAluno(ResultSet resultSet) throws SQLException {
        Aluno aluno = new Aluno();
        aluno.setId(Long.valueOf(resultSet.getLong("id")));
        long idAlunoInep = resultSet.getLong("id_aluno_inep");
        if (!resultSet.wasNull()) {
            aluno.setIdAlunoInep(Long.valueOf(idAlunoInep));
        }
        aluno.setNome(resultSet.getString("nome"));
        aluno.setCpf(resultSet.getString("cpf"));
        aluno.setDataNascimento(resultSet.getDate("data_nascimento"));
        aluno.setCorRaca(getNullableInt(resultSet, "cor_raca"));
        aluno.setNacionalidade(Integer.valueOf(resultSet.getInt("nacionalidade")));
        aluno.setUfNascimento(resultSet.getString("uf_nascimento"));
        aluno.setMunicipioNascimento(resultSet.getString("municipio_nascimento"));
        aluno.setPaisOrigem(resultSet.getString("pais_origem"));
        aluno.setTiposDeficienciaResumo(opcaoDAO.resumirAluno(aluno.getId(), CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA));
        return aluno;
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
