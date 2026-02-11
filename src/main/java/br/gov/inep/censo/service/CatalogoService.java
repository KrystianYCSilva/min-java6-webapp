package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.dao.OpcaoDAO;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.repository.LayoutCampoRepository;
import br.gov.inep.censo.repository.OpcaoDominioRepository;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.List;

/**
 * Servico de leitura para catalogos de opcoes e campos de leiaute.
 */
public class CatalogoService {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;
    private final OpcaoDominioRepository opcaoDominioRepository;
    private final LayoutCampoRepository layoutCampoRepository;

    public CatalogoService() {
        this(new OpcaoDAO(), new LayoutCampoDAO(), resolveOpcaoRepository(), resolveLayoutCampoRepository());
    }

    public CatalogoService(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this(opcaoDAO, layoutCampoDAO, null, null);
    }

    public CatalogoService(OpcaoDAO opcaoDAO,
                           LayoutCampoDAO layoutCampoDAO,
                           OpcaoDominioRepository opcaoDominioRepository,
                           LayoutCampoRepository layoutCampoRepository) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
        this.opcaoDominioRepository = opcaoDominioRepository;
        this.layoutCampoRepository = layoutCampoRepository;
    }

    public List<OpcaoDominio> listarOpcoesPorCategoria(String categoria) throws SQLException {
        if (opcaoDominioRepository != null) {
            try {
                return opcaoDominioRepository.findByCategoriaOrderByNomeAsc(categoria);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar opcoes via repository.", e);
            }
        }
        return opcaoDAO.listarPorCategoria(categoria);
    }

    public List<LayoutCampo> listarCamposModulo(String modulo) throws SQLException {
        if (layoutCampoRepository != null) {
            try {
                return layoutCampoRepository.findByModuloOrderByNumeroCampoAsc(modulo);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar campos de layout via repository.", e);
            }
        }
        return layoutCampoDAO.listarPorModulo(modulo);
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private static OpcaoDominioRepository resolveOpcaoRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(OpcaoDominioRepository.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static LayoutCampoRepository resolveLayoutCampoRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(LayoutCampoRepository.class);
        } catch (Exception e) {
            return null;
        }
    }
}
