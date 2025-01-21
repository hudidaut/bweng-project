package at.technikum.springrestbackend.security.jwt;

import at.technikum.springrestbackend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext(); // Clear the security context before each test
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String username = "testuser";
        String role = "ROLE_USER";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getClaims(token)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(claims.get("role", String.class)).thenReturn(role);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        assert auth.getPrincipal() == userDetails; // Principal is now UserDetails
        assert auth.getAuthorities().contains(new SimpleGrantedAuthority(role));

        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_doesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_existingAuthentication_skipsSettingAuthentication() throws ServletException, IOException {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existingUser", null, Collections.emptyList())
        );

        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(token);
        verify(jwtUtil, never()).getClaims(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

}

