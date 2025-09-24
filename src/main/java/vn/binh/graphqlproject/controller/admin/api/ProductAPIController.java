package vn.binh.graphqlproject.controller.admin.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.binh.graphqlproject.entity.Category;
import vn.binh.graphqlproject.entity.Product;
import vn.binh.graphqlproject.model.Response;
import vn.binh.graphqlproject.service.ICategoryService;
import vn.binh.graphqlproject.service.IProductService;
import vn.binh.graphqlproject.service.IStorageService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductAPIController {
    @Autowired
    IProductService productService;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IStorageService storageService;
    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        return new ResponseEntity<Response>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }
    @PostMapping(path = "/addProduct")
    public ResponseEntity<?> addProduct(
            @Validated @RequestParam("productName") String productName,

            @Validated @RequestParam("unitPrice") Double unitPrice,

            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity)
             {
        Optional<Product> optProduct = productService.findByProductName(productName);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()), HttpStatus.BAD_REQUEST);
        }
        Product product = new Product();
        Timestamp timestamp = new Timestamp(new Date(System.currentTimeMillis()).getTime());
        try {
            product.setProductName(productName);
            product.setUnitPrice(unitPrice);

            product.setDescription(description);
            product.setQuantity(quantity);

            Category cateEntity = new Category();
            cateEntity.setCategoryId(categoryId);
            product.setCategory(cateEntity);

            productService.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Response>(new Response(false, "Lỗi lưu sản phẩm", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Response>(new Response(true, "Thành công", optProduct.orElse(product)), HttpStatus.OK);
    }

    @PostMapping(path = "/getProduct")
    public ResponseEntity<?> getProduct(@Validated @RequestParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<Response>(new Response(true, "Thành công", product.get()), HttpStatus.OK);
        }
        return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "/updateProduct")
    public ResponseEntity<?> updateProduct(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.BAD_REQUEST);
        }
        try {
            Product p = opt.get();
            p.setProductName(productName);
            p.setUnitPrice(unitPrice);

            p.setDescription(description);
            p.setQuantity(quantity);

            Category cateEntity = new Category();
            cateEntity.setCategoryId(categoryId);
            p.setCategory(cateEntity);

            productService.save(p);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật thành công", p), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Response>(new Response(false, "Lỗi cập nhật sản phẩm", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<?> deleteProduct(@Validated @RequestParam("productId") Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.BAD_REQUEST);
        }
        try {
            productService.delete(opt.get());
            return new ResponseEntity<Response>(new Response(true, "Xóa thành công", opt.get()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Response>(new Response(false, "Lỗi xóa sản phẩm", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}