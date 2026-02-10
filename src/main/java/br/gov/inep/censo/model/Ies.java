package br.gov.inep.censo.model;

import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.TipoLaboratorioEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * Entidade de IES com foco no Registro 11 (laboratorio) e campos complementares.
 */
public class Ies implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long idIesInep;
    private String nomeLaboratorio;
    private String registroLaboratorioIes;
    private Integer laboratorioAtivoAno;
    private String descricaoAtividades;
    private String palavrasChave;
    private Integer laboratorioInformatica;
    private Integer tipoLaboratorio;
    private Integer codigoUfLaboratorio;
    private String codigoMunicipioLaboratorio;
    private Map<Long, String> camposComplementares;
    private Map<Integer, String> camposRegistro11;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdIesInep() {
        return idIesInep;
    }

    public void setIdIesInep(Long idIesInep) {
        this.idIesInep = idIesInep;
    }

    public String getNomeLaboratorio() {
        return nomeLaboratorio;
    }

    public void setNomeLaboratorio(String nomeLaboratorio) {
        this.nomeLaboratorio = nomeLaboratorio;
    }

    public String getRegistroLaboratorioIes() {
        return registroLaboratorioIes;
    }

    public void setRegistroLaboratorioIes(String registroLaboratorioIes) {
        this.registroLaboratorioIes = registroLaboratorioIes;
    }

    public Integer getLaboratorioAtivoAno() {
        return laboratorioAtivoAno;
    }

    public void setLaboratorioAtivoAno(Integer laboratorioAtivoAno) {
        this.laboratorioAtivoAno = laboratorioAtivoAno;
    }

    public String getDescricaoAtividades() {
        return descricaoAtividades;
    }

    public void setDescricaoAtividades(String descricaoAtividades) {
        this.descricaoAtividades = descricaoAtividades;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public Integer getLaboratorioInformatica() {
        return laboratorioInformatica;
    }

    public void setLaboratorioInformatica(Integer laboratorioInformatica) {
        this.laboratorioInformatica = laboratorioInformatica;
    }

    public Integer getTipoLaboratorio() {
        return tipoLaboratorio;
    }

    public void setTipoLaboratorio(Integer tipoLaboratorio) {
        this.tipoLaboratorio = tipoLaboratorio;
    }

    public Integer getCodigoUfLaboratorio() {
        return codigoUfLaboratorio;
    }

    public void setCodigoUfLaboratorio(Integer codigoUfLaboratorio) {
        this.codigoUfLaboratorio = codigoUfLaboratorio;
    }

    public String getCodigoMunicipioLaboratorio() {
        return codigoMunicipioLaboratorio;
    }

    public void setCodigoMunicipioLaboratorio(String codigoMunicipioLaboratorio) {
        this.codigoMunicipioLaboratorio = codigoMunicipioLaboratorio;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro11() {
        return camposRegistro11;
    }

    public void setCamposRegistro11(Map<Integer, String> camposRegistro11) {
        this.camposRegistro11 = camposRegistro11;
    }

    public TipoLaboratorioEnum getTipoLaboratorioEnum() {
        return TipoLaboratorioEnum.fromCodigo(tipoLaboratorio);
    }

    public EstadoEnum getUfLaboratorioEnum() {
        return EstadoEnum.fromCodigo(codigoUfLaboratorio);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Ies target = new Ies();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder idIesInep(Long idIesInep) {
            target.setIdIesInep(idIesInep);
            return this;
        }

        public Builder nomeLaboratorio(String nomeLaboratorio) {
            target.setNomeLaboratorio(nomeLaboratorio);
            return this;
        }

        public Builder registroLaboratorioIes(String registroLaboratorioIes) {
            target.setRegistroLaboratorioIes(registroLaboratorioIes);
            return this;
        }

        public Builder laboratorioAtivoAno(Integer laboratorioAtivoAno) {
            target.setLaboratorioAtivoAno(laboratorioAtivoAno);
            return this;
        }

        public Builder descricaoAtividades(String descricaoAtividades) {
            target.setDescricaoAtividades(descricaoAtividades);
            return this;
        }

        public Builder palavrasChave(String palavrasChave) {
            target.setPalavrasChave(palavrasChave);
            return this;
        }

        public Builder laboratorioInformatica(Integer laboratorioInformatica) {
            target.setLaboratorioInformatica(laboratorioInformatica);
            return this;
        }

        public Builder tipoLaboratorio(Integer tipoLaboratorio) {
            target.setTipoLaboratorio(tipoLaboratorio);
            return this;
        }

        public Builder codigoUfLaboratorio(Integer codigoUfLaboratorio) {
            target.setCodigoUfLaboratorio(codigoUfLaboratorio);
            return this;
        }

        public Builder codigoMunicipioLaboratorio(String codigoMunicipioLaboratorio) {
            target.setCodigoMunicipioLaboratorio(codigoMunicipioLaboratorio);
            return this;
        }

        public Ies build() {
            return target;
        }
    }
}
