package vn.binh.graphqlproject.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.binh.graphqlproject.entity.Category;
import vn.binh.graphqlproject.model.CategoryModel;
import vn.binh.graphqlproject.service.ICategoryService;
import vn.binh.graphqlproject.service.IStorageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;
    @Autowired
    IStorageService storageService;

    // ===== GraphQL Resolvers for Category =====
    @QueryMapping
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public Category categoryById(@Argument Long id) {
        return categoryService.findById(id).orElse(null);
    }

    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        Category c = new Category();
        c.setCategoryName(input.getCategoryName());
        c.setImages(input.getImages());
        return categoryService.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isEmpty()) return null;
        Category c = opt.get();
        c.setCategoryName(input.getCategoryName());
        c.setImages(input.getImages());
        return categoryService.save(c);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isEmpty()) return false;
        categoryService.delete(opt.get());
        return true;
    }
    @MutationMapping
    public Category createCategoryWithFile(@Argument String categoryName, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Category category = new Category();
        category.setCategoryName(categoryName);

        // Handle file upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Validate file type
                String contentType = imageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new RuntimeException("File must be an image");
                }

                long maxSize = 5 * 1024 * 1024; // 5MB
                if (imageFile.getSize() > maxSize) {
                    throw new RuntimeException("File size must not exceed 5MB");
                }

                // Save file to the local storage path
                String storageLocation = storageService.getStorageLocation(); // Retrieve from `application.properties`
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path targetLocation = Paths.get(storageLocation).resolve(fileName);
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // Set the file name in the category
                category.setImages(fileName);
            } catch (Exception e) {
                throw new RuntimeException("File upload error: " + e.getMessage());
            }
        }

        // Save the category
        return categoryService.save(category);
    }



    // Các view phục vụ render bằng AJAX
    @GetMapping("ajax/list")
    public String listAjax() {
        return "admin/categories/list-ajax";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
                .body(file);
    }



    @GetMapping("ajax/update")
    public String updateAjax() {
        return "admin/categories/update-ajax";
    }

    @GetMapping("ajax/delete")
    public String deleteAjax() {
        return "admin/categories/delete-ajax";
    }

    // GraphQL input type mapping
    public static class CategoryInput {
        private String categoryName;
        private String images;

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
    }
}