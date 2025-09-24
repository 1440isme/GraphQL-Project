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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.binh.graphqlproject.entity.Category;
import vn.binh.graphqlproject.model.CategoryModel;
import vn.binh.graphqlproject.service.ICategoryService;
import vn.binh.graphqlproject.service.IStorageService;

import java.util.List;
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

    @GetMapping("add")
    public String add(ModelMap model) {
        CategoryModel cateModel = new CategoryModel();
        cateModel.setIsEdit(false);
        model.addAttribute("category", cateModel);
        return "admin/categories/addOrEdit";
    }

    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryModel cateModel,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/categories/addOrEdit";
        }
        Category entity = new Category();
        BeanUtils.copyProperties(cateModel, entity);
        categoryService.save(entity);

        String message = "";
        if (Boolean.TRUE.equals(cateModel.getIsEdit())) {
            message = "Category is Edited";
        } else {
            message = "Category is saved";
        }
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/categories/searchpaginate";
    }

    @RequestMapping("")
    public String list(ModelMap model) {
        List<Category> list = categoryService.findAll();
        model.addAttribute("categories", list);
        return "admin/categories/list";
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(ModelMap model, @PathVariable("id") Long id) {
        Optional<Category> optCategory = categoryService.findById(id);
        CategoryModel cateModel = new CategoryModel();

        if (optCategory.isPresent()) {
            Category entity = optCategory.get();
            BeanUtils.copyProperties(entity, cateModel);
            cateModel.setIsEdit(true);
            model.addAttribute("category", cateModel);
            return new ModelAndView("admin/categories/addOrEdit", model);
        } else {
            model.addAttribute("message", "Category is not exist");
            return new ModelAndView("forward:/admin/categories", model);
        }
    }

    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id) {
        categoryService.deleteById(id);
        model.addAttribute("message", "Category is deleted");
        return new ModelAndView("forward:/admin/categories", model);
    }

    @GetMapping("search")
    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
        List<Category> list;
        if (StringUtils.hasText(name)) {
            list = categoryService.findByCategoryNameContaining(name);
        } else {
            list = categoryService.findAll();
        }
        model.addAttribute("categories", list);
        return "admin/categories/search";
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