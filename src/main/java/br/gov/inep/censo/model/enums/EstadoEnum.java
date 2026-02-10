package br.gov.inep.censo.model.enums;

/**
 * Enumeracao de estados (UF) utilizada nos modulos de Docente e IES.
 */
public enum EstadoEnum {
    RO(11, "Rondonia"),
    AC(12, "Acre"),
    AM(13, "Amazonas"),
    RR(14, "Roraima"),
    PA(15, "Para"),
    AP(16, "Amapa"),
    TO(17, "Tocantins"),
    MA(21, "Maranhao"),
    PI(22, "Piaui"),
    CE(23, "Ceara"),
    RN(24, "Rio Grande do Norte"),
    PB(25, "Paraiba"),
    PE(26, "Pernambuco"),
    AL(27, "Alagoas"),
    SE(28, "Sergipe"),
    BA(29, "Bahia"),
    MG(31, "Minas Gerais"),
    ES(32, "Espirito Santo"),
    RJ(33, "Rio de Janeiro"),
    SP(35, "Sao Paulo"),
    PR(41, "Parana"),
    SC(42, "Santa Catarina"),
    RS(43, "Rio Grande do Sul"),
    MS(50, "Mato Grosso do Sul"),
    MT(51, "Mato Grosso"),
    GO(52, "Goias"),
    DF(53, "Distrito Federal");

    private final int codigo;
    private final String descricao;

    EstadoEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static EstadoEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        EstadoEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].codigo == codigo.intValue()) {
                return values[i];
            }
        }
        return null;
    }
}
