package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.dao.OpcaoDAO;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;

import java.sql.SQLException;
import java.util.List;

/**
 * Servico de leitura para catalogos de opcoes e campos de leiaute.
 */
public class CatalogoService {

    private final OpcaoDAO opcaoDAO;
    private final LayoutCampoDAO layoutCampoDAO;

    public CatalogoService() {
        this(new OpcaoDAO(), new LayoutCampoDAO());
    }

    public CatalogoService(OpcaoDAO opcaoDAO, LayoutCampoDAO layoutCampoDAO) {
        this.opcaoDAO = opcaoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
    }

    public List<OpcaoDominio> listarOpcoesPorCategoria(String categoria) throws SQLException {
        return opcaoDAO.listarPorCategoria(categoria);
    }

    public List<LayoutCampo> listarCamposModulo(String modulo) throws SQLException {
        return layoutCampoDAO.listarPorModulo(modulo);
    }
}
