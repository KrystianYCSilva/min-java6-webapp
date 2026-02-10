package br.gov.inep.censo.dao;

import br.gov.inep.censo.config.HibernateConnectionProvider;
import br.gov.inep.censo.model.Municipio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela de apoio de municipios.
 */
public class MunicipioDAO extends AbstractJdbcDao {

    public boolean existeCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.trim().length() == 0) {
            return false;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement("SELECT 1 FROM municipio WHERE codigo = ?");
            statement.setString(1, codigo.trim());
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public boolean existeCodigoNaUf(String codigo, Integer codigoUf) throws SQLException {
        if (codigo == null || codigo.trim().length() == 0 || codigoUf == null) {
            return false;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(
                    "SELECT 1 FROM municipio WHERE codigo = ? AND codigo_uf = ?");
            statement.setString(1, codigo.trim());
            statement.setInt(2, codigoUf.intValue());
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<Municipio> listarPorUf(Integer codigoUf) throws SQLException {
        List<Municipio> municipios = new ArrayList<Municipio>();
        if (codigoUf == null) {
            return municipios;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = HibernateConnectionProvider.getConnection();
            statement = connection.prepareStatement(
                    "SELECT codigo, nome, codigo_uf, nome_uf FROM municipio WHERE codigo_uf = ? ORDER BY nome");
            statement.setInt(1, codigoUf.intValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Municipio municipio = new Municipio();
                municipio.setCodigo(resultSet.getString("codigo"));
                municipio.setNome(resultSet.getString("nome"));
                municipio.setCodigoUf(Integer.valueOf(resultSet.getInt("codigo_uf")));
                municipio.setNomeUf(resultSet.getString("nome_uf"));
                municipios.add(municipio);
            }
            return municipios;
        } finally {
            closeQuietly(resultSet);
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
