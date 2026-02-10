package br.gov.inep.censo.model.enums;

/**
 * Enumeracao para nivel academico do curso.
 */
public enum NivelAcademicoEnum {
    GRADUACAO("GRADUACAO", "Graduacao"),
    SEQUENCIAL("SEQUENCIAL", "Sequencial"),
    POS_GRADUACAO("POS_GRADUACAO", "Pos-graduacao");

    private final String codigo;
    private final String descricao;

    NivelAcademicoEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NivelAcademicoEnum fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        NivelAcademicoEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].codigo.equalsIgnoreCase(codigo)) {
                return values[i];
            }
        }
        return null;
    }
}
