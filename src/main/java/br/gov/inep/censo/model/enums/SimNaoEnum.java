package br.gov.inep.censo.model.enums;

/**
 * Enumeracao binaria para campos de resposta Sim/Nao.
 */
public enum SimNaoEnum {
    NAO(0, "Nao"),
    SIM(1, "Sim");

    private final int codigo;
    private final String descricao;

    SimNaoEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SimNaoEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        return codigo.intValue() == 1 ? SIM : NAO;
    }
}
