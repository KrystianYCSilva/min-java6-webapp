package br.gov.inep.censo.model.enums;

/**
 * Enumeracao de Cor/Raca do leiaute de Aluno (Registro 41 - campo 7).
 */
public enum CorRacaEnum {
    NAO_DECLARADO(0, "Aluno nao quis declarar cor/raca"),
    BRANCA(1, "Branca"),
    PRETA(2, "Preta"),
    PARDA(3, "Parda"),
    AMARELA(4, "Amarela"),
    INDIGENA(5, "Indigena"),
    NAO_DISPOE_INFORMACAO(6, "Nao dispoe da informacao");

    private final int codigo;
    private final String descricao;

    CorRacaEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CorRacaEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        CorRacaEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].codigo == codigo.intValue()) {
                return values[i];
            }
        }
        return null;
    }
}
