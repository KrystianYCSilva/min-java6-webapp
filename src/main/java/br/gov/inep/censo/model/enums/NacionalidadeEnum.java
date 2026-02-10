package br.gov.inep.censo.model.enums;

/**
 * Enumeracao de Nacionalidade do leiaute de Aluno (Registro 41 - campo 8).
 */
public enum NacionalidadeEnum {
    BRASILEIRA_NATA(1, "Brasileira nata"),
    BRASILEIRA_NATURALIZACAO(2, "Brasileira por naturalizacao"),
    ESTRANGEIRA(3, "Estrangeira");

    private final int codigo;
    private final String descricao;

    NacionalidadeEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NacionalidadeEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        NacionalidadeEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].codigo == codigo.intValue()) {
                return values[i];
            }
        }
        return null;
    }
}
