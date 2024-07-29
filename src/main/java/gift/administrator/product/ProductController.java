package gift.administrator.product;

import gift.administrator.category.CategoryService;
import gift.util.PageUtil;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getAllProduct(Model model,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
        @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection) {
        size = PageUtil.validateSize(size);
        sortBy = PageUtil.validateSortBy(sortBy, Arrays.asList("id", "name"));
        Direction direction = PageUtil.validateDirection(sortDirection);
        Page<ProductDTO> paging = productService.getAllProducts(page, size, sortBy, direction);
        model.addAttribute("products", paging);
        model.addAttribute("currentPage", paging.getNumber());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("categories",productService.getAllCategoryName());
        return "products";
    }

    @GetMapping("/add")
    public String showPostProduct(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add";
    }

    @GetMapping("/update/{id}")
    public String showPutProduct(@PathVariable("id") long id, Model model)
        throws NotFoundException {
        ProductDTO product = productService.getProductById(id);
        model.addAttribute("productDTO", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "update";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id) throws NotFoundException {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/add")
    public String postProduct(@Valid @ModelAttribute("productDTO") ProductDTO product,
        BindingResult result, Model model) throws NotFoundException {
        productService.existsByNamePutResult(product.getName(), result);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "add";
        }
        productService.addProduct(product);
        model.addAttribute("productDTO", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/products";
    }

    @PostMapping("/update/{id}")
    public String putProduct(@PathVariable("id") Long id,
        @Valid @ModelAttribute("productDTO") ProductDTO product, BindingResult result,
        Model model) {
        ProductDTO product1 = new ProductDTO(id, product.getName(), product.getPrice(),
            product.getImageUrl(), product.getCategoryId(), product.getOptions());
        productService.existsByNameAndIdPutResult(product1.getName(), id, result);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "update";
        }
        productService.updateProduct(product1, id);
        return "redirect:/products";
    }
}
