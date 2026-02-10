package br.gov.inep.censo.model;

import java.io.Serializable;

/**
 * Entidade de apoio para municipios pre-carregados por script.
 */
public class Municipio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private Integer codigoUf;
    private String nomeUf;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCodigoUf() {
        return codigoUf;
    }

    public void setCodigoUf(Integer codigoUf) {
        this.codigoUf = codigoUf;
    }

    public String getNomeUf() {
        return nomeUf;
    }

    public void setNomeUf(String nomeUf) {
        this.nomeUf = nomeUf;
    }
}
