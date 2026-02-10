package br.gov.inep.censo.spring.security;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.repository.UsuarioRepository;
import br.gov.inep.censo.util.PasswordUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider de autenticacao para API Spring Security baseado na tabela usuario.
 */
public class UsuarioAuthenticationProvider implements AuthenticationProvider {

    private UsuarioRepository usuarioRepository;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (usuarioRepository == null) {
            throw new BadCredentialsException("Repositorio de usuario nao configurado.");
        }

        String login = authentication.getName();
        Object credential = authentication.getCredentials();
        String senha = credential == null ? null : credential.toString();

        if (login == null || login.trim().length() == 0 || senha == null || senha.trim().length() == 0) {
            throw new BadCredentialsException("Credenciais invalidas.");
        }

        Usuario usuario = usuarioRepository.findByLogin(login.trim());
        if (usuario == null || !usuario.isAtivo()) {
            throw new BadCredentialsException("Usuario ou senha invalidos.");
        }

        String senhaHash = usuario.getSenhaHash();
        if (senhaHash == null || !PasswordUtil.verifyPassword(senha, senhaHash)) {
            throw new BadCredentialsException("Usuario ou senha invalidos.");
        }

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                usuario.getLogin(),
                null,
                authorities
        );
        token.setDetails(usuario);
        return token;
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUsuarioRepository(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
}
