package vn.binh.graphqlproject.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IProductService productService;
    @Autowired
    IStorageService storageService;

    @ModelAttribute("categories")
    public List<CategoryModel> getCategories() {
        return categoryService.findAll().stream().map(item -> {
            CategoryModel cateModel = new CategoryModel();
            BeanUtils.copyProperties(item, cateModel);
            return cateModel;
        }).toList();
    }

    @GetMapping("add")
    public String add(ModelMap model) {
        ProductModel proModel = new ProductModel();
        proModel.setIsEdit(false);
        model.addAttribute("product", proModel);
        return "admin/products/addOrEdit";
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("product") ProductModel proModel,
            BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("admin/products/addOrEdit");
        }
        Product entity = new Product();
        BeanUtils.copyProperties(proModel, entity);
        Category cateEntity = new Category();
        cateEntity.setCategoryId(proModel.getCategoryId());
        entity.setCategory(cateEntity);
        if (!proModel.getImageFile().isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String uuString = uuid.toString();
            entity.setImages(storageService.getStorageFilename(proModel.getImageFile(), uuString));
            storageService.store(proModel.getImageFile(), entity.getImages());
        }
        productService.save(entity);
        String message = "";
        if (proModel.getIsEdit()) {
            message = "Product is Edited";

        } else
            message = "Product is saved";
        model.addAttribute("message", message);
        return new ModelAndView("forward:/admin/products/searchpaginated", model);
    }

    @RequestMapping("")
    public String list(ModelMap model) {
        List<Product> list = productService.findAll();
        model.addAttribute("products", list);
        return "admin/products/list";
    }

    @GetMapping("edit/{productId}")
    public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        ProductModel proModel = new ProductModel();
        if (optProduct.isPresent()) {
            Product entity = optProduct.get();
            BeanUtils.copyProperties(optProduct.get(), proModel);
            proModel.setCategoryId(entity.getCategory().getCategoryId());
            proModel.setIsEdit(true);
            model.addAttribute("product", proModel);
            return new ModelAndView("admin/products/addOrEdit");

        }
        model.addAttribute("message", "Product is not exist");
        return new ModelAndView("forward:/admin/products", model);
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getFilename() + "\"")
                .body(file);

    }

    @GetMapping("delete/{productId}")
    public ModelAndView delete(ModelMap model, @PathVariable("productId") Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isPresent()) {
            if (!StringUtils.isEmpty(opt.get().getImages())) {
                try {
                    storageService.delete(opt.get().getImages());
                } catch (Exception e) {
                    model.addAttribute("message", "Cannot delete product image: " + e.getMessage());
                }
            }
            productService.delete(opt.get());
            model.addAttribute("message", "Product is deleted");
        } else
            model.addAttribute("message", "Product is not found");
        return new ModelAndView("forward:/admin/products/searchpaginated", model);
    }

    @GetMapping("search")
    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
        List<Product> list;
        if (StringUtils.hasText(name)) {
            list = productService.findByNameContaining(name);
        } else {
            list = productService.findAll();
        }
        model.addAttribute("products", list);
        return "admin/products/search";
    }

    @GetMapping("searchpaginated")
    public String seach(ModelMap model,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int count = (int) productService.count();
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
        Page<Product> resultPage = null;
        if (StringUtils.hasText(name)) {
            resultPage = productService.findByNameContaining(name, pageable);
        } else {
            resultPage = productService.findAll(pageable);
        }
        int totalPages = resultPage.getTotalPages();
        if (currentPage > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);
            if (totalPages < count) {
                if (end == totalPages)
                    start = end - count;
                else if (start == 1)
                    end = start + count;
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("productPage", resultPage);
        return "admin/products/searchpaginated";
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
}
