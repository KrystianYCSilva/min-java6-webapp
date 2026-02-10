package br.gov.inep.censo.spring.security;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.repository.UsuarioRepository;
import br.gov.inep.censo.util.PasswordUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsuarioAuthenticationProviderTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioAuthenticationProvider provider;

    @Before
    public void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        provider = new UsuarioAuthenticationProvider();
        provider.setUsuarioRepository(usuarioRepository);
    }

    @Test
    public void deveAutenticarQuandoUsuarioAtivoESenhaValida() {
        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        usuario.setAtivo(true);
        usuario.setSenhaHash(PasswordUtil.hashPassword("admin123"));
        when(usuarioRepository.findByLogin("admin")).thenReturn(usuario);

        Authentication result = provider.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin123"));

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("admin", result.getName());
    }

    @Test(expected = BadCredentialsException.class)
    public void deveFalharQuandoSenhaInvalida() {
        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        usuario.setAtivo(true);
        usuario.setSenhaHash(PasswordUtil.hashPassword("admin123"));
        when(usuarioRepository.findByLogin("admin")).thenReturn(usuario);

        provider.authenticate(new UsernamePasswordAuthenticationToken("admin", "senhaErrada"));
    }
}
