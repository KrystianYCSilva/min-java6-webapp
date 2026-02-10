package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.ConnectionFactory;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO do modulo Curso (Registro 21), incluindo opcoes normalizadas e campos complementares.
 */
public class CursoDAO extends AbstractJdbcDao {

    private static final String SQL_INSERT =
            "INSERT INTO curso (codigo_curso_emec, nome, nivel_academico, formato_oferta, curso_teve_aluno_vinculado) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE curso SET codigo_curso_emec = ?, nome = ?, nivel_academico = ?, formato_oferta = ?, " +
                    "curso_teve_aluno_vinculado = ? WHERE id = ?";

    private static final String SQL_LISTA =
            "SELECT id, codigo_curso_emec, nome, nivel_academico, formato_oferta, curso_teve_aluno_vinculado " +
                    "FROM curso ORDER BY nome";

    private static final String SQL_PAGINADO =
            "SELECT id, codigo_curso_emec, nome, nivel_academico, formato_oferta, curso_teve_aluno_vinculado " +
                    "FROM curso ORDER BY nome LIMIT ? OFFSET ?";

    private static final String SQL_COUNT = "SELECT COUNT(1) AS total FROM curso";
    private static final String SQL_BY_ID =
            "SELECT id, codigo_curso_emec, nome, nivel_academico, formato_oferta, curso_teve_aluno_vinculado " +
                    "FROM curso WHERE id = ?";

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(Curso curso, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preencherCampos(statement, curso);
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Falha ao gerar ID para curso.");
            }
            Long cursoId = Long.valueOf(generatedKeys.getLong(1));

            opcaoDAO.salvarVinculosCurso(connection, cursoId, opcaoIds);
            layoutCampoDAO.salvarValoresCurso(connection, cursoId, camposComplementares);

            connection.commit();
            return cursoId;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public void atualizar(Curso curso, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        if (curso == null || curso.getId() == null) {
            throw new IllegalArgumentException("Curso/ID nao informado para atualizacao.");
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(SQL_UPDATE);
            preencherCampos(statement, curso);
            statement.setLong(6, curso.getId().longValue());
            statement.executeUpdate();

            opcaoDAO.substituirVinculosCurso(connection, curso.getId(), opcaoIds);
            layoutCampoDAO.substituirValoresCurso(connection, curso.getId(), camposComplementares);

            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(statement);
            restoreAutoCommitAndClose(connection);
        }
    }

    public Curso buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(SQL_BY_ID);
            statement.setLong(1, id.longValue());
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return mapCurso(resultSet);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Curso> listar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Curso> cursos = new ArrayList<Curso>();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(SQL_LISTA);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cursos.add(mapCurso(resultSet));
            }
            return cursos;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Curso> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        int page = pagina <= 0 ? 1 : pagina;
        int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
        int offset = (page - 1) * size;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Curso> cursos = new ArrayList<Curso>();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(SQL_PAGINADO);
            statement.setInt(1, size);
            statement.setInt(2, offset);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cursos.add(mapCurso(resultSet));
            }
            return cursos;
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
            connection = ConnectionFactory.getConnection();
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
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            statement = connection.prepareStatement(
                    "DELETE FROM curso_aluno_opcao WHERE curso_aluno_id IN (SELECT id FROM curso_aluno WHERE curso_id = ?)");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            statement = connection.prepareStatement(
                    "DELETE FROM curso_aluno_layout_valor WHERE curso_aluno_id IN (SELECT id FROM curso_aluno WHERE curso_id = ?)");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            statement = connection.prepareStatement("DELETE FROM curso_aluno WHERE curso_id = ?");
            statement.setLong(1, id.longValue());
            statement.executeUpdate();
            closeQuietly(statement);

            opcaoDAO.removerVinculosCurso(connection, id);
            layoutCampoDAO.removerValoresCurso(connection, id);

            statement = connection.prepareStatement("DELETE FROM curso WHERE id = ?");
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

    private void preencherCampos(PreparedStatement statement, Curso curso) throws SQLException {
        statement.setString(1, curso.getCodigoCursoEmec());
        statement.setString(2, curso.getNome());
        statement.setString(3, curso.getNivelAcademico());
        statement.setString(4, curso.getFormatoOferta());
        statement.setInt(5, curso.getCursoTeveAlunoVinculado().intValue());
    }

    private Curso mapCurso(ResultSet resultSet) throws SQLException {
        Curso curso = new Curso();
        curso.setId(Long.valueOf(resultSet.getLong("id")));
        curso.setCodigoCursoEmec(resultSet.getString("codigo_curso_emec"));
        curso.setNome(resultSet.getString("nome"));
        curso.setNivelAcademico(resultSet.getString("nivel_academico"));
        curso.setFormatoOferta(resultSet.getString("formato_oferta"));
        curso.setCursoTeveAlunoVinculado(Integer.valueOf(resultSet.getInt("curso_teve_aluno_vinculado")));
        curso.setRecursosTecnologiaAssistivaResumo(opcaoDAO.resumirCurso(
                curso.getId(), CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA));
        return curso;
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
