package br.gov.inep.censo.service;

import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes de integracao do servico de IES cobrindo importacao e exportacao TXT pipe.
 */
public class IesServiceTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveImportarExportarEOperarCrudRegistro11() throws Exception {
        IesService service = new IesService();

        String[] campos = new String[29];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = "";
        }
        campos[0] = "11";
        campos[1] = "Laboratorio Integrado";
        campos[2] = "LAB-IM-01";
        campos[3] = "1";
        campos[4] = "Descricao teste";
        campos[5] = "integracao";
        campos[6] = "1";
        campos[18] = "1";
        campos[26] = "35";
        campos[27] = "3550308";
        campos[28] = "59";

        String conteudo = "10|1001\n" + joinPipe(campos);
        int total = service.importarTxtPipe(conteudo);
        Assert.assertEquals(1, total);
        Assert.assertEquals(0, service.importarTxtPipe(null));

        List<Ies> itens = service.listar();
        Assert.assertEquals(1, itens.size());
        Ies ies = itens.get(0);
        Assert.assertNotNull(ies.getId());
        Assert.assertEquals(Long.valueOf(1001L), ies.getIdIesInep());
        Assert.assertEquals(Integer.valueOf(59), ies.getTipoLaboratorio());

        Assert.assertEquals(1, service.contar());
        Assert.assertEquals(1, service.listarPaginado(0, 0).size());
        Assert.assertNotNull(service.buscarPorId(ies.getId()));
        Assert.assertNull(service.buscarPorId(Long.valueOf(999999L)));

        String exportado = service.exportarPorIdTxtPipe(ies.getId());
        Assert.assertTrue(exportado.startsWith("11|Laboratorio Integrado|LAB-IM-01|1|Descricao teste|integracao|1|"));
        Assert.assertTrue(exportado.contains("|35|3550308|59"));
        Assert.assertTrue(service.exportarTodosTxtPipe().contains("Laboratorio Integrado"));
        Assert.assertEquals("", service.exportarPorIdTxtPipe(Long.valueOf(999999L)));

        Map<Long, String> complementares = service.carregarCamposComplementaresPorCampoId(ies.getId());
        Assert.assertTrue(complementares.containsValue("1"));

        service.excluir(ies.getId());
        Assert.assertEquals(0, service.contar());
        service.excluir(null);
    }

    @Test
    public void deveAplicarFallbackDeMunicipioEValidarRegras() throws Exception {
        IesService service = new IesService();

        String[] campos = new String[29];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = "";
        }
        campos[0] = "11";
        campos[1] = "Laboratorio Fallback";
        campos[2] = "LAB-FB-01";
        campos[3] = "1";
        campos[16] = "35";
        campos[17] = "3550308";
        campos[28] = "59";

        int total = service.importarTxtPipe(joinPipe(campos));
        Assert.assertEquals(1, total);
        Ies importado = service.listar().get(0);
        Assert.assertEquals(Integer.valueOf(35), importado.getCodigoUfLaboratorio());
        Assert.assertEquals("3550308", importado.getCodigoMunicipioLaboratorio());

        Ies semNome = new Ies();
        semNome.setNomeLaboratorio(" ");
        try {
            service.cadastrar(semNome, new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException por nome obrigatorio.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Nome do laboratorio"));
        }

        Ies semUf = novoIesValido();
        semUf.setCodigoUfLaboratorio(null);
        semUf.setCodigoMunicipioLaboratorio("3550308");
        assertIllegalArgumentCadastro(service, semUf, "UF do laboratorio");

        Ies ufInvalida = novoIesValido();
        ufInvalida.setCodigoUfLaboratorio(Integer.valueOf(41));
        ufInvalida.setCodigoMunicipioLaboratorio("3550308");
        assertIllegalArgumentCadastro(service, ufInvalida, "nao pertence");

        Ies semId = novoIesValido();
        try {
            service.atualizar(semId, new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException por ID obrigatorio.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("ID da IES"));
        }

        Ies comAtivoPadrao = new Ies();
        comAtivoPadrao.setNomeLaboratorio("Laboratorio Com Ativo Padrao");
        Long id = service.cadastrar(comAtivoPadrao, new LinkedHashMap<Long, String>());
        Ies salvo = service.buscarPorId(id);
        Assert.assertEquals(Integer.valueOf(1), salvo.getLaboratorioAtivoAno());
    }

    private Ies novoIesValido() {
        Ies ies = new Ies();
        ies.setIdIesInep(Long.valueOf(123L));
        ies.setNomeLaboratorio("Laboratorio Valido");
        ies.setRegistroLaboratorioIes("LAB-VAL-1");
        ies.setLaboratorioAtivoAno(Integer.valueOf(1));
        ies.setDescricaoAtividades("Descricao valida");
        ies.setPalavrasChave("teste");
        ies.setLaboratorioInformatica(Integer.valueOf(1));
        ies.setTipoLaboratorio(Integer.valueOf(59));
        ies.setCodigoUfLaboratorio(Integer.valueOf(35));
        ies.setCodigoMunicipioLaboratorio("3550308");
        return ies;
    }

    private void assertIllegalArgumentCadastro(IesService service, Ies ies, String trechoMensagem)
            throws Exception {
        try {
            service.cadastrar(ies, new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(trechoMensagem));
        }
    }

    private String joinPipe(String[] campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(campos[i] != null ? campos[i] : "");
        }
        return sb.toString();
    }
}
