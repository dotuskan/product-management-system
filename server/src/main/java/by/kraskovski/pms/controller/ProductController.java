package by.kraskovski.pms.controller;

import by.kraskovski.pms.controller.dto.ProductDto;
import by.kraskovski.pms.domain.model.Product;
import by.kraskovski.pms.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

/**
 * Controller for the {@link ProductService}.
 */
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final Mapper mapper;

    @Autowired
    public ProductController(final ProductService productService, final Mapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    /**
     * Find all products in database.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity loadAllProducts() {
        log.info("Start loadAllProducts");
        try {
            return ResponseEntity.ok(productService.findAll().stream()
                    .map(product -> mapper.map(product, ProductDto.class))
                    .collect(toList()));
        } catch (DataAccessException e) {
            log.error("Exception in loadAllProducts. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Find products in database with setting id in browser.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity loadProductById(@PathVariable("id") final String id) {
        log.info("Start loadProductById: {}", id);
        try {
            final Product product = productService.find(id);
            Assert.notNull(product, "Unable to find product with id: " + id);
            return ResponseEntity.ok(mapper.map(product, ProductDto.class));
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Find products in database with setting name in browser
     */
    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
    public ResponseEntity loadProductsByName(@PathVariable("name") final String name) {
        log.info("Start loadProductsByName: {}", name);
        return ResponseEntity.ok(productService.findByName(name).stream()
                .map(product -> mapper.map(product, ProductDto.class))
                .collect(toList()));
    }

    /**
     * Find products in database with setting type in browser.
     */
    @RequestMapping(value = "/type/{type}", method = RequestMethod.GET)
    public ResponseEntity loadProductsByType(@PathVariable("type") final String type) {
        log.info("Start loadProductsByType: {}", type);
        return ResponseEntity.ok(productService.findByType(type).stream()
                .map(product -> mapper.map(product, ProductDto.class))
                .collect(toList()));
    }

    /**
     * Creating {@link Product} from client form.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProduct(@RequestBody final ProductDto productDto) {
        log.info("Start createProduct: {}", productDto.getName());
        final Product product = productService.create(mapper.map(productDto, Product.class));
        return new ResponseEntity<>(mapper.map(product, ProductDto.class), HttpStatus.CREATED);
    }

    /**
     * Update {@link Product}'s information in database.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@RequestBody final ProductDto productDto) {
        log.info("start updateProduct: {}", productDto.getId());
        final Product product = productService.update(mapper.map(productDto, Product.class));
        return ResponseEntity.ok(mapper.map(product, ProductDto.class));
    }

    /**
     * Delete {@link Product} from database by identifier.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteProduct(@PathVariable("id") final String id) {
        log.info("Start deleteProduct: {}", id);
        try {
            productService.delete(id);
            log.info("Product with id: {}, was deleted successful", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            log.info("Error in deleteProduct. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
