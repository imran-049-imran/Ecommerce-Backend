package com.restapis.ecommerce.service;


import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;


public interface AuthenticationFacade  {

    @Nullable Authentication getAuthentication();
}
