// REST
// /products -> Create, Read
// /products/{id} -> Read, Update, Delete

package com.codesoom.assignment.controllers.product;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
//    private final ProductService productService;
//
//    private final AuthenticationService authenticationService;
//
//    public ProductController(ProductService productService,
//                             AuthenticationService authenticationService) {
//        this.productService = productService;
//        this.authenticationService = authenticationService;
//    }
//
//    @GetMapping
//    public List<Product> list() {
//        return productService.getProducts();
//    }
//
//    @GetMapping("{id}")
//    public Product detail(@PathVariable Long id) {
//        return productService.getProduct(id);
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Product create(
//            @RequestAttribute Long userId,
//            @RequestBody @Valid ProductData productData
//    ) {
//        return productService.createProduct(productData);
//    }
//
//    @PatchMapping("{id}")
//    public Product update(
//            @RequestAttribute Long userId,
//            @PathVariable Long id,
//            @RequestBody @Valid ProductData productData
//    ) {
//        return productService.updateProduct(id, productData);
//    }
//
//    @DeleteMapping("{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void destroy(
//            @RequestAttribute Long userId,
//            @PathVariable Long id
//    ) {
//        productService.deleteProduct(id);
//    }
}
