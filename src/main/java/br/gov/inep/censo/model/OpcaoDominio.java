package br.gov.inep.censo.model;

import java.io.Serializable;

/**
 * Item de catalogo de dominio para campos multivalorados.
 */
public class OpcaoDominio implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String categoria;
    private String codigo;
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

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
}
