package com.example.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class Authenticated{
    public static Long getUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
