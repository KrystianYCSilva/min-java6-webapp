package br.gov.inep.censo.model;

import java.io.Serializable;

/**
 * Metadado de campo de leiaute oficial (CSV).
 */
public class LayoutCampo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String modulo;
    private Integer numeroCampo;
    private String nomeCampo;
    private String obrigatoriedade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Integer getNumeroCampo() {
        return numeroCampo;
    }

    public void setNumeroCampo(Integer numeroCampo) {
        this.numeroCampo = numeroCampo;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getObrigatoriedade() {
        return obrigatoriedade;
    }

    public void setObrigatoriedade(String obrigatoriedade) {
        this.obrigatoriedade = obrigatoriedade;
    }
}
