package br.gov.inep.censo.model.enums;

/**
 * Enumeracao das formas de ingresso do Registro 42 (campos 14 a 23).
 */
public enum FormaIngressoEnum {
    VESTIBULAR("forma_ingresso_vestibular", "formaIngressoVestibular", "Vestibular"),
    ENEM("forma_ingresso_enem", "formaIngressoEnem", "Enem"),
    AVALIACAO_SERIADA("forma_ingresso_avaliacao_seriada", "formaIngressoAvaliacaoSeriada", "Avaliacao seriada"),
    SELECAO_SIMPLIFICADA("forma_ingresso_selecao_simplificada", "formaIngressoSelecaoSimplificada", "Selecao simplificada"),
    EGRESSO_BI_LI("forma_ingresso_egresso_bi_li", "formaIngressoEgressoBiLi", "Egresso BI/LI"),
    PEC_G("forma_ingresso_pec_g", "formaIngressoPecG", "PEC-G"),
    TRANSFERENCIA_EXOFFICIO("forma_ingresso_transferencia_exofficio", "formaIngressoTransferenciaExofficio", "Transferencia Ex Officio"),
    DECISAO_JUDICIAL("forma_ingresso_decisao_judicial", "formaIngressoDecisaoJudicial", "Decisao judicial"),
    VAGAS_REMANESCENTES("forma_ingresso_vagas_remanescentes", "formaIngressoVagasRemanescentes", "Vagas remanescentes"),
    PROGRAMAS_ESPECIAIS("forma_ingresso_programas_especiais", "formaIngressoProgramasEspeciais", "Programas especiais");

    private final String colunaBanco;
    private final String parametroRequest;
    private final String descricao;

    FormaIngressoEnum(String colunaBanco, String parametroRequest, String descricao) {
        this.colunaBanco = colunaBanco;
        this.parametroRequest = parametroRequest;
        this.descricao = descricao;
    }

    public String getColunaBanco() {
        return colunaBanco;
    }

    public String getParametroRequest() {
        return parametroRequest;
    }

    public String getDescricao() {
        return descricao;
    }
}
