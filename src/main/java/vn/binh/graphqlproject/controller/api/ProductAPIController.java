package vn.binh.graphqlproject.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.binh.springbootsproject.entity.Category;
import vn.binh.springbootsproject.entity.Product;
import vn.binh.springbootsproject.model.Response;
import vn.binh.springbootsproject.service.ICategoryService;
import vn.binh.springbootsproject.service.IProductService;
import vn.binh.springbootsproject.service.IStorageService;

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
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {
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
            product.setDiscount(discount);
            product.setDescription(description);
            product.setQuantity(quantity);
            product.setStatus(status);
            Category cateEntity = new Category();
            cateEntity.setCategoryId(categoryId);
            product.setCategory(cateEntity);
            if (imageFile != null && !imageFile.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                product.setImages(storageService.getStorageFilename(imageFile, uuString));
                storageService.store(imageFile, product.getImages());
            }
            product.setCreateDate(timestamp);
            productService.save(product);
            optProduct = productService.findByCreateDate(timestamp);
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
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.BAD_REQUEST);
        }
        try {
            Product p = opt.get();
            p.setProductName(productName);
            p.setUnitPrice(unitPrice);
            p.setDiscount(discount);
            p.setDescription(description);
            p.setQuantity(quantity);
            p.setStatus(status);
            Category cateEntity = new Category();
            cateEntity.setCategoryId(categoryId);
            p.setCategory(cateEntity);
            if (imageFile != null && !imageFile.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                p.setImages(storageService.getStorageFilename(imageFile, uuString));
                storageService.store(imageFile, p.getImages());
            }
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