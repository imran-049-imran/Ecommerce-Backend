package com.restapis.ecommerce.service;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade  {

    @Override
    public @Nullable Authentication getAuthentication() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

}
