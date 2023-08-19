package com.codesoom.assignment.aop;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.ProductNotFoundException;
import com.codesoom.assignment.errors.UserNoPermission;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OwnerCheckAspect {
    private final ProductService productService;

    public OwnerCheckAspect(ProductService productService) {
        this.productService = productService;
    }

    @Before("@annotation(com.codesoom.assignment.aop.annotation.CheckOwner) && args(id,..)")
    public void checkOwner(Long id) throws InvalidTokenException, UserNoPermission, ProductNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        authenticationValidate(authentication);

        Long loginUserId = (Long) authentication.getPrincipal();

        Product product = productService.getProduct(id);

        Long createdUserId = product.getCreateUserId();

        if (loginUserId != createdUserId) {
            throw new UserNoPermission("You do not have permission.");
        }
    }

    private void authenticationValidate(Authentication authentication) {
        if (authentication == null) {
            throw new InvalidTokenException("AccessToken is Invalid.");
        }
        if (authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("You do not have permission.");
        }
    }
}

