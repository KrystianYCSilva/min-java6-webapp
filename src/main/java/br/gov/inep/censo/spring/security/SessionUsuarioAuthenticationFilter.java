package br.gov.inep.censo.spring.security;

import br.gov.inep.censo.model.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reaproveita a sessao legado (usuarioLogado) para autenticacao de endpoints Spring.
 */
public class SessionUsuarioAuthenticationFilter extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null || !currentAuth.isAuthenticated()) {
            HttpSession session = request.getSession(false);
            Object usuarioLogado = session != null ? session.getAttribute("usuarioLogado") : null;
            if (usuarioLogado instanceof Usuario) {
                Usuario usuario = (Usuario) usuarioLogado;
                if (usuario.isAtivo()) {
                    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            usuario.getLogin(),
                            null,
                            authorities
                    );
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
