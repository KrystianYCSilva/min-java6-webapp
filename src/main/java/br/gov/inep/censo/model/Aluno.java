package br.gov.inep.censo.model;

import br.gov.inep.censo.model.enums.CorRacaEnum;
import br.gov.inep.censo.model.enums.NacionalidadeEnum;

import java.io.Serializable;
import java.sql.Date;
import java.util.Map;

/**
 * Entidade de aluno (Registro 41) com apoio a campos complementares de leiaute.
 */
public class Aluno implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long idAlunoInep;
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private Integer corRaca;
    private Integer nacionalidade;
    private String ufNascimento;
    private String municipioNascimento;
    private String paisOrigem;
    private String tiposDeficienciaResumo;
    private Map<Long, String> camposComplementares;
    private Map<Integer, String> camposRegistro41;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAlunoInep() {
        return idAlunoInep;
    }

    public void setIdAlunoInep(Long idAlunoInep) {
        this.idAlunoInep = idAlunoInep;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getCorRaca() {
        return corRaca;
    }

    public void setCorRaca(Integer corRaca) {
        this.corRaca = corRaca;
    }

    public Integer getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Integer nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public CorRacaEnum getCorRacaEnum() {
        return CorRacaEnum.fromCodigo(corRaca);
    }

    public NacionalidadeEnum getNacionalidadeEnum() {
        return NacionalidadeEnum.fromCodigo(nacionalidade);
    }

    public String getUfNascimento() {
        return ufNascimento;
    }

    public void setUfNascimento(String ufNascimento) {
        this.ufNascimento = ufNascimento;
    }

    public String getMunicipioNascimento() {
        return municipioNascimento;
    }

    public void setMunicipioNascimento(String municipioNascimento) {
        this.municipioNascimento = municipioNascimento;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public String getTiposDeficienciaResumo() {
        return tiposDeficienciaResumo;
    }

    public void setTiposDeficienciaResumo(String tiposDeficienciaResumo) {
        this.tiposDeficienciaResumo = tiposDeficienciaResumo;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro41() {
        return camposRegistro41;
    }

    public void setCamposRegistro41(Map<Integer, String> camposRegistro41) {
        this.camposRegistro41 = camposRegistro41;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Aluno target = new Aluno();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder idAlunoInep(Long idAlunoInep) {
            target.setIdAlunoInep(idAlunoInep);
            return this;
        }

        public Builder nome(String nome) {
            target.setNome(nome);
            return this;
        }

        public Builder cpf(String cpf) {
            target.setCpf(cpf);
            return this;
        }

        public Builder dataNascimento(Date dataNascimento) {
            target.setDataNascimento(dataNascimento);
            return this;
        }

        public Builder corRaca(Integer corRaca) {
            target.setCorRaca(corRaca);
            return this;
        }

        public Builder nacionalidade(Integer nacionalidade) {
            target.setNacionalidade(nacionalidade);
            return this;
        }

        public Builder ufNascimento(String ufNascimento) {
            target.setUfNascimento(ufNascimento);
            return this;
        }

        public Builder municipioNascimento(String municipioNascimento) {
            target.setMunicipioNascimento(municipioNascimento);
            return this;
        }

        public Builder paisOrigem(String paisOrigem) {
            target.setPaisOrigem(paisOrigem);
            return this;
        }

        public Aluno build() {
            return target;
        }
    }
}
