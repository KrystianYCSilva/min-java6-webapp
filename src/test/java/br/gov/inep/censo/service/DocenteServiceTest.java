package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Testes de integracao do servico de Docente cobrindo importacao e exportacao TXT pipe.
 */
public class DocenteServiceTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveImportarExportarEOperarCrudRegistro31() throws Exception {
        DocenteService service = new DocenteService();
        LayoutCampoDAO layoutCampoDAO = new LayoutCampoDAO();
        Long campo29 = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.DOCENTE_31).get(Integer.valueOf(29));
        Assert.assertNotNull(campo29);

        String[] campos = new String[42];
        for (int i = 0; i < campos.length; i++) {
            campos[i] = "";
        }
        campos[0] = "31";
        campos[1] = "DOC-IM-001";
        campos[2] = "Docente Importado";
        campos[3] = "12345678901";
        campos[4] = "PASS-44";
        campos[5] = "19850501";
        campos[6] = "1";
        campos[7] = "1";
        campos[8] = "BRA";
        campos[9] = "35";
        campos[10] = "3550308";
        campos[11] = "1";
        campos[28] = "2";

        int total = service.importarTxtPipe(joinPipe(campos));
        Assert.assertEquals(1, total);
        Assert.assertEquals(0, service.importarTxtPipe("   "));

        List<Docente> docentes = service.listar();
        Assert.assertEquals(1, docentes.size());
        Long docenteId = docentes.get(0).getId();
        Assert.assertNotNull(docenteId);

        Assert.assertEquals(1, service.contar());
        Assert.assertEquals(1, service.listarPaginado(0, 0).size());
        Assert.assertNotNull(service.buscarPorId(docenteId));
        Assert.assertNull(service.buscarPorId(Long.valueOf(999999L)));

        String exportado = service.exportarPorIdTxtPipe(docenteId);
        Assert.assertTrue(exportado.startsWith("31|DOC-IM-001|Docente Importado|12345678901|PASS-44|"));
        Assert.assertTrue(exportado.contains("|19850501|1|1|BRA|35|3550308|1|"));
        Assert.assertTrue(service.exportarTodosTxtPipe().contains("Docente Importado"));
        Assert.assertEquals("", service.exportarPorIdTxtPipe(Long.valueOf(999999L)));

        Map<Long, String> complementares = service.carregarCamposComplementaresPorCampoId(docenteId);
        Assert.assertEquals("2", complementares.get(campo29));

        service.excluir(docenteId);
        Assert.assertEquals(0, service.contar());
        service.excluir(null);
    }

    @Test
    public void deveValidarCamposObrigatoriosEConsistenciaMunicipio() throws Exception {
        DocenteService service = new DocenteService();

        Docente cpfInvalido = novoDocenteValido();
        cpfInvalido.setCpf("123");
        assertIllegalArgumentCadastro(service, cpfInvalido, "CPF");

        Docente semUf = novoDocenteValido();
        semUf.setUfNascimento(null);
        semUf.setMunicipioNascimento("3550308");
        assertIllegalArgumentCadastro(service, semUf, "UF de nascimento");

        Docente ufInvalida = novoDocenteValido();
        ufInvalida.setUfNascimento(Integer.valueOf(41));
        ufInvalida.setMunicipioNascimento("3550308");
        assertIllegalArgumentCadastro(service, ufInvalida, "nao pertence");

        Docente semId = novoDocenteValido();
        try {
            service.atualizar(semId, new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException por ID obrigatorio.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("ID do docente"));
        }

        Docente paisPadrao = novoDocenteValido();
        paisPadrao.setPaisOrigem("");
        Long docenteId = service.cadastrar(paisPadrao, new LinkedHashMap<Long, String>());
        Docente salvo = service.buscarPorId(docenteId);
        Assert.assertEquals("BRA", salvo.getPaisOrigem());
    }

    private Docente novoDocenteValido() {
        Docente docente = new Docente();
        docente.setIdDocenteIes("DOC-VALID-1");
        docente.setNome("Docente Valido");
        docente.setCpf("11122233344");
        docente.setDataNascimento(Date.valueOf("1987-01-10"));
        docente.setCorRaca(Integer.valueOf(1));
        docente.setNacionalidade(Integer.valueOf(1));
        docente.setPaisOrigem("BRA");
        docente.setUfNascimento(Integer.valueOf(35));
        docente.setMunicipioNascimento("3550308");
        docente.setDocenteDeficiencia(Integer.valueOf(0));
        return docente;
    }

    private void assertIllegalArgumentCadastro(DocenteService service, Docente docente, String message)
            throws Exception {
        try {
            service.cadastrar(docente, new LinkedHashMap<Long, String>());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(message));
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
