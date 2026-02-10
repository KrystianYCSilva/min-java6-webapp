package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.CursoAluno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO do vinculo aluno-curso (Registro 42).
 */
public class CursoAlunoDAO extends AbstractJdbcDao {

    private static final String SQL_INSERT =
            "INSERT INTO curso_aluno (" +
                    "aluno_id, curso_id, id_aluno_ies, periodo_referencia, codigo_polo_ead, " +
                    "turno_aluno, situacao_vinculo, curso_origem, semestre_conclusao, " +
                    "aluno_parfor, segunda_licenciatura_formacao, tipo_segunda_licenciatura_formacao, semestre_ingresso, " +
                    "forma_ingresso_vestibular, forma_ingresso_enem, forma_ingresso_avaliacao_seriada, " +
                    "forma_ingresso_selecao_simplificada, forma_ingresso_egresso_bi_li, forma_ingresso_pec_g, " +
                    "forma_ingresso_transferencia_exofficio, forma_ingresso_decisao_judicial, " +
                    "forma_ingresso_vagas_remanescentes, forma_ingresso_programas_especiais" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_LISTA =
            "SELECT ca.id, ca.aluno_id, ca.curso_id, ca.id_aluno_ies, ca.periodo_referencia, ca.codigo_polo_ead, " +
                    "ca.turno_aluno, ca.situacao_vinculo, ca.curso_origem, ca.semestre_conclusao, ca.aluno_parfor, " +
                    "ca.segunda_licenciatura_formacao, ca.tipo_segunda_licenciatura_formacao, ca.semestre_ingresso, " +
                    "ca.forma_ingresso_vestibular, ca.forma_ingresso_enem, ca.forma_ingresso_avaliacao_seriada, " +
                    "ca.forma_ingresso_selecao_simplificada, ca.forma_ingresso_egresso_bi_li, ca.forma_ingresso_pec_g, " +
                    "ca.forma_ingresso_transferencia_exofficio, ca.forma_ingresso_decisao_judicial, " +
                    "ca.forma_ingresso_vagas_remanescentes, ca.forma_ingresso_programas_especiais, " +
                    "a.nome AS aluno_nome, c.nome AS curso_nome, c.codigo_curso_emec " +
                    "FROM curso_aluno ca " +
                    "INNER JOIN aluno a ON a.id = ca.aluno_id " +
                    "INNER JOIN curso c ON c.id = ca.curso_id " +
                    "ORDER BY ca.criado_em DESC";

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoAlunoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoAlunoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(CursoAluno cursoAluno, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = HibernateConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, cursoAluno.getAlunoId().longValue());
            statement.setLong(2, cursoAluno.getCursoId().longValue());
            statement.setString(3, cursoAluno.getIdAlunoIes());
            statement.setString(4, cursoAluno.getPeriodoReferencia());
            setNullableString(statement, 5, cursoAluno.getCodigoPoloEad());
            setNullableInteger(statement, 6, cursoAluno.getTurnoAluno());
            setNullableInteger(statement, 7, cursoAluno.getSituacaoVinculo());
            setNullableString(statement, 8, cursoAluno.getCursoOrigem());
            setNullableString(statement, 9, cursoAluno.getSemestreConclusao());
            setNullableInteger(statement, 10, cursoAluno.getAlunoParfor());
            setNullableInteger(statement, 11, cursoAluno.getSegundaLicenciaturaFormacao());
            setNullableInteger(statement, 12, cursoAluno.getTipoSegundaLicenciaturaFormacao());
            setNullableString(statement, 13, cursoAluno.getSemestreIngresso());
            setNullableInteger(statement, 14, cursoAluno.getFormaIngressoVestibular());
            setNullableInteger(statement, 15, cursoAluno.getFormaIngressoEnem());
            setNullableInteger(statement, 16, cursoAluno.getFormaIngressoAvaliacaoSeriada());
            setNullableInteger(statement, 17, cursoAluno.getFormaIngressoSelecaoSimplificada());
            setNullableInteger(statement, 18, cursoAluno.getFormaIngressoEgressoBiLi());
            setNullableInteger(statement, 19, cursoAluno.getFormaIngressoPecG());
            setNullableInteger(statement, 20, cursoAluno.getFormaIngressoTransferenciaExofficio());
            setNullableInteger(statement, 21, cursoAluno.getFormaIngressoDecisaoJudicial());
            setNullableInteger(statement, 22, cursoAluno.getFormaIngressoVagasRemanescentes());
            setNullableInteger(statement, 23, cursoAluno.getFormaIngressoProgramasEspeciais());
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Falha ao gerar ID para curso_aluno.");
            }
            Long id = Long.valueOf(generatedKeys.getLong(1));

            opcaoDAO.salvarVinculosCursoAluno(connection, id, opcaoIds);
            layoutCampoDAO.salvarValoresCursoAluno(connection, id, camposComplementares);

            connection.commit();
            return id;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                    // noop
                }
            }
            throw e;
        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(statement);
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // noop
                }
            }
            closeQuietly(connection);
        }
    }

    public List<CursoAluno> listar() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<CursoAluno> itens = new ArrayList<CursoAluno>();
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(SQL_LISTA);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                CursoAluno cursoAluno = new CursoAluno();
                cursoAluno.setId(Long.valueOf(resultSet.getLong("id")));
                cursoAluno.setAlunoId(Long.valueOf(resultSet.getLong("aluno_id")));
                cursoAluno.setCursoId(Long.valueOf(resultSet.getLong("curso_id")));
                cursoAluno.setIdAlunoIes(resultSet.getString("id_aluno_ies"));
                cursoAluno.setPeriodoReferencia(resultSet.getString("periodo_referencia"));
                cursoAluno.setCodigoPoloEad(resultSet.getString("codigo_polo_ead"));
                cursoAluno.setTurnoAluno(getNullableInt(resultSet, "turno_aluno"));
                cursoAluno.setSituacaoVinculo(getNullableInt(resultSet, "situacao_vinculo"));
                cursoAluno.setCursoOrigem(resultSet.getString("curso_origem"));
                cursoAluno.setSemestreConclusao(resultSet.getString("semestre_conclusao"));
                cursoAluno.setAlunoParfor(getNullableInt(resultSet, "aluno_parfor"));
                cursoAluno.setSegundaLicenciaturaFormacao(getNullableInt(resultSet, "segunda_licenciatura_formacao"));
                cursoAluno.setTipoSegundaLicenciaturaFormacao(getNullableInt(resultSet, "tipo_segunda_licenciatura_formacao"));
                cursoAluno.setSemestreIngresso(resultSet.getString("semestre_ingresso"));
                cursoAluno.setFormaIngressoVestibular(getNullableInt(resultSet, "forma_ingresso_vestibular"));
                cursoAluno.setFormaIngressoEnem(getNullableInt(resultSet, "forma_ingresso_enem"));
                cursoAluno.setFormaIngressoAvaliacaoSeriada(getNullableInt(resultSet, "forma_ingresso_avaliacao_seriada"));
                cursoAluno.setFormaIngressoSelecaoSimplificada(getNullableInt(resultSet, "forma_ingresso_selecao_simplificada"));
                cursoAluno.setFormaIngressoEgressoBiLi(getNullableInt(resultSet, "forma_ingresso_egresso_bi_li"));
                cursoAluno.setFormaIngressoPecG(getNullableInt(resultSet, "forma_ingresso_pec_g"));
                cursoAluno.setFormaIngressoTransferenciaExofficio(getNullableInt(resultSet, "forma_ingresso_transferencia_exofficio"));
                cursoAluno.setFormaIngressoDecisaoJudicial(getNullableInt(resultSet, "forma_ingresso_decisao_judicial"));
                cursoAluno.setFormaIngressoVagasRemanescentes(getNullableInt(resultSet, "forma_ingresso_vagas_remanescentes"));
                cursoAluno.setFormaIngressoProgramasEspeciais(getNullableInt(resultSet, "forma_ingresso_programas_especiais"));
                cursoAluno.setAlunoNome(resultSet.getString("aluno_nome"));
                cursoAluno.setCursoNome(resultSet.getString("curso_nome"));
                cursoAluno.setCodigoCursoEmec(resultSet.getString("codigo_curso_emec"));
                cursoAluno.setFinanciamentosResumo(opcaoDAO.resumirCursoAluno(
                        cursoAluno.getId(), CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
                cursoAluno.setApoioSocialResumo(opcaoDAO.resumirCursoAluno(
                        cursoAluno.getId(), CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
                cursoAluno.setAtividadesResumo(opcaoDAO.resumirCursoAluno(
                        cursoAluno.getId(), CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
                cursoAluno.setReservasResumo(opcaoDAO.resumirCursoAluno(
                        cursoAluno.getId(), CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));

                itens.add(cursoAluno);
            }

            return itens;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
