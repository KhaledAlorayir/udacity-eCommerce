package com.example.demo.security;

import com.auth0.jwt.JWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class AuthMiddlewareHandler extends BasicAuthenticationFilter {
    public AuthMiddlewareHandler(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(Constants.HEADER_STRING);

        if(token == null || !token.startsWith(Constants.TOKEN_PREFIX)) {
            chain.doFilter(request,response);
        }
        String userId = JWT.require(HMAC512(Constants.SECRET.getBytes())).build()
                        .verify(token.replace(Constants.TOKEN_PREFIX,""))
                                .getSubject();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userId,null,new ArrayList<>()));
        chain.doFilter(request,response);
    }
}
