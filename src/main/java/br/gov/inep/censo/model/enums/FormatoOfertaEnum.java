package br.gov.inep.censo.model.enums;

/**
 * Enumeracao para formato de oferta de curso.
 */
public enum FormatoOfertaEnum {
    PRESENCIAL("PRESENCIAL", "Presencial"),
    EAD("EAD", "EAD"),
    SEMIPRESENCIAL("SEMIPRESENCIAL", "Semipresencial");

    private final String codigo;
    private final String descricao;

    FormatoOfertaEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static FormatoOfertaEnum fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        FormatoOfertaEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].codigo.equalsIgnoreCase(codigo)) {
                return values[i];
            }
        }
        return null;
    }
}
