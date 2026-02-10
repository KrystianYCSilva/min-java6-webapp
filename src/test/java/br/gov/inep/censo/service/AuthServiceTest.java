package br.gov.inep.censo.service;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.support.TestDatabaseSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes de integracao do servico de autenticacao com JDBC.
 */
public class AuthServiceTest {

    @Before
    public void setUp() throws Exception {
        TestDatabaseSupport.resetDatabase();
    }

    @Test
    public void deveAutenticarUsuarioPadraoQuandoSenhaCorreta() throws Exception {
        AuthService service = new AuthService();
        Usuario usuario = service.autenticar("admin", "admin123");

        Assert.assertNotNull(usuario);
        Assert.assertEquals("admin", usuario.getLogin());
        Assert.assertTrue(usuario.isAtivo());
    }

    @Test
    public void naoDeveAutenticarQuandoSenhaIncorreta() throws Exception {
        AuthService service = new AuthService();
        Usuario usuario = service.autenticar("admin", "senha-incorreta");
        Assert.assertNull(usuario);
    }
}
