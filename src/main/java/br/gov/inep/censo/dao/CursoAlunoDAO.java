package br.gov.inep.censo.dao;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO do vinculo aluno-curso (Registro 42).
 */
public class CursoAlunoDAO extends AbstractJpaDao {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CursoAlunoDAO() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CursoAlunoDAO(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public Long salvar(final CursoAluno cursoAluno,
                       final long[] opcaoIds,
                       final Map<Long, String> camposComplementares) throws SQLException {
        return executeInTransaction(new EntityManagerWork<Long>() {
            public Long execute(EntityManager entityManager) throws SQLException {
                if (cursoAluno.getAlunoId() == null || cursoAluno.getCursoId() == null) {
                    throw new IllegalArgumentException("Aluno e Curso sao obrigatorios para o registro 42.");
                }
                Aluno alunoRef = entityManager.getReference(Aluno.class, cursoAluno.getAlunoId());
                Curso cursoRef = entityManager.getReference(Curso.class, cursoAluno.getCursoId());
                cursoAluno.setAluno(alunoRef);
                cursoAluno.setCurso(cursoRef);

                entityManager.persist(cursoAluno);
                entityManager.flush();
                Long cursoAlunoId = cursoAluno.getId();
                if (cursoAlunoId == null) {
                    throw new SQLException("Falha ao gerar ID para curso_aluno.");
                }
                opcaoDAO.salvarVinculosCursoAluno(entityManager, cursoAlunoId, opcaoIds);
                layoutCampoDAO.salvarValoresCursoAluno(entityManager, cursoAlunoId, camposComplementares);
                return cursoAlunoId;
            }
        });
    }

    public List<CursoAluno> listar() throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<CursoAluno>>() {
            public List<CursoAluno> execute(EntityManager entityManager) throws SQLException {
                TypedQuery<CursoAluno> query = entityManager.createQuery(
                        "select distinct ca from CursoAluno ca " +
                                "join fetch ca.aluno " +
                                "join fetch ca.curso " +
                                "order by ca.id desc", CursoAluno.class);
                List<CursoAluno> itens = query.getResultList();
                for (int i = 0; i < itens.size(); i++) {
                    CursoAluno item = itens.get(i);
                    if (item.getAluno() != null) {
                        item.setAlunoId(item.getAluno().getId());
                        item.setAlunoNome(item.getAluno().getNome());
                    }
                    if (item.getCurso() != null) {
                        item.setCursoId(item.getCurso().getId());
                        item.setCursoNome(item.getCurso().getNome());
                        item.setCodigoCursoEmec(item.getCurso().getCodigoCursoEmec());
                    }
                    item.setFinanciamentosResumo(opcaoDAO.resumirCursoAluno(
                            entityManager, item.getId(), CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
                    item.setApoioSocialResumo(opcaoDAO.resumirCursoAluno(
                            entityManager, item.getId(), CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
                    item.setAtividadesResumo(opcaoDAO.resumirCursoAluno(
                            entityManager, item.getId(), CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
                    item.setReservasResumo(opcaoDAO.resumirCursoAluno(
                            entityManager, item.getId(), CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));
                }
                return itens;
            }
        });
    }
}
