package vn.binh.graphqlproject.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.binh.graphqlproject.entity.Category;
import vn.binh.graphqlproject.entity.Product;
import vn.binh.graphqlproject.model.CategoryModel;
import vn.binh.graphqlproject.model.ProductModel;
import vn.binh.graphqlproject.service.ICategoryService;
import vn.binh.graphqlproject.service.IProductService;
import vn.binh.graphqlproject.service.IStorageService;
import vn.binh.graphqlproject.service.IUserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Collections;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IProductService productService;
    @Autowired
    IStorageService storageService;
    @Autowired
    IUserService userService;

    @ModelAttribute("categories")
    public List<CategoryModel> getCategories() {
        return categoryService.findAll().stream().map(item -> {
            CategoryModel cateModel = new CategoryModel();
            BeanUtils.copyProperties(item, cateModel);
            return cateModel;
        }).toList();
    }
    @QueryMapping
    public List<Product> productsSortByPrice() {
        return productService.findAll(Sort.by(Sort.Direction.ASC, "unitPrice"));
    }
    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @QueryMapping
    public Product productById(@Argument Long id) {
        return productService.findById(id).orElse(null);
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        Product p = new Product();
        p.setProductName(input.getProductName());
        p.setUnitPrice(input.getUnitPrice());
        p.setDescription(input.getDescription());
        p.setQuantity(input.getQuantity());
        Category c = new Category();
        c.setCategoryId(input.getCategoryId());
        p.setCategory(c);
        return productService.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) return null;
        Product p = opt.get();
        p.setProductName(input.getProductName());
        p.setUnitPrice(input.getUnitPrice());
        p.setDescription(input.getDescription());
        p.setQuantity(input.getQuantity());
        Category c = new Category();
        c.setCategoryId(input.getCategoryId());
        p.setCategory(c);
        return productService.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) return false;
        productService.delete(opt.get());
        return true;
    }


    @GetMapping(value = "ajax", produces = "application/json")
    @ResponseBody
    public Page<Product> searchAjax(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "3") int size) {

        int safePage = Math.max(1, page);
        Pageable pageable = PageRequest.of(safePage - 1, size, Sort.by("name"));
        if (StringUtils.hasText(name)) {
            return productService.findByNameContaining(name, pageable);
        }
        return productService.findAll(pageable);
    }

    @GetMapping(value = "ajax/{productId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> getOneAjax(@PathVariable("productId") Long productId) {
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // AJAX view pages for Products
    @GetMapping("ajax/list")
    public String listAjax() {
        return "admin/products/list-ajax";
    }

    @GetMapping("ajax/add")
    public String addAjax() {
        return "admin/products/add-ajax";
    }

    @GetMapping("ajax/update")
    public String updateAjax() {
        return "admin/products/update-ajax";
    }

    @GetMapping("ajax/delete")
    public String deleteAjax() {
        return "admin/products/delete-ajax";
    }

    // GraphQL input type mapping
    public static class ProductInput {
        private String productName;
        private Double unitPrice;
        private String description;
        private Integer quantity;
        private Long categoryId;

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }
}
