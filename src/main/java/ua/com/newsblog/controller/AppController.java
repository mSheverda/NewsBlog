package ua.com.newsblog.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ua.com.newsblog.model.*;
import ua.com.newsblog.service.*;

import java.util.ArrayList;

@Controller
public class AppController {
    /**
     * Объект сервиса для работы с товарами.
     */
    private final ProductService productService;

    /**
     * Объект сервиса для работы с категориями товаров.
     */
    private final CategoryService categoryService;

    /**
     * Объект сервиса для работы с торговой корзиной.
     */
    private final ShoppingCartService shoppingCartService;

    /**
     * Объект сервиса для работы с заказами.
     */
    private final OrderService orderService;

    /**
     * Объект сервиса для работы с статусами заказов.
     */
    private final StatusService statusService;

    /**
     * Объект сервиса для работы с ролями пользователей.
     */
    private final RoleService roleService;

    private final SalePositionService salePositionService;

    /**
     * Конструктор для инициализации основных переменных контроллера главных страниц сайта.
     * Помечен аннотацией @Autowired, которая позволит Spring автоматически инициализировать объекты.
     */
    @Autowired
    public AppController(ProductService productService, CategoryService categoryService,
                         ShoppingCartService shoppingCartService, OrderService orderService,
                         StatusService statusService, RoleService roleService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.statusService = statusService;
        this.roleService = roleService;
        this.salePositionService = salePositionService;
    }

    public AppController(ProductService productService, CategoryService categoryService, ShoppingCartService shoppingCartService, OrderService orderService, StatusService statusService, RoleService roleService, SenderService senderService) {
    }

    /**
     * Возвращает главную cтраницу сайта "client/home". Для формирования страницы с базы подгружаются
     * категории товаров, 4 рандомных товаров и количество товаров в корзине.
     * URL запроса {"/", "/index"}, метод GET.
     */
    @RequestMapping(value = {"/", "/index", "/home"}, method = RequestMethod.GET)
    public ModelAndView home(ModelAndView modelAndView) {
        modelAndView.addObject("categories", this.categoryService.getAll());
        modelAndView.addObject("products", this.productService.getRandom(4));
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.setViewName("client/main");
        return modelAndView;
    }

    @RequestMapping(value = {"/contacts"}, method = RequestMethod.GET)
    public ModelAndView contacts(ModelAndView modelAndView) {
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.setViewName("client/contacts");
        return modelAndView;
    }



    /**
     * Перенаправляет по по запросу "/".
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView redirectToHome(ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    /**
     * Возвращает страницу "client/category" с товарами, которые пренадлежат категории с url.
     * URL запроса "/category_{url}", метод GET.
     */
    @RequestMapping(value = "/category_{url}", method = RequestMethod.GET)
    public ModelAndView viewProductsInCategory(@PathVariable("url") String url, ModelAndView modelAndView) {
        modelAndView.addObject("category", this.categoryService.get(url));
        modelAndView.addObject("products", this.productService.getByCategoryUrl(url));
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.setViewName("client/category");
        return modelAndView;
    }

    /**
     * Возвращает страницу "client/products" с всема товарами.
     * URL запроса "/all_products", метод GET.
     */
    @RequestMapping(value = "/all_products", method = RequestMethod.GET)
    public ModelAndView viewAllProducts(ModelAndView modelAndView) {
        modelAndView.addObject("products", this.productService.getAll());
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.setViewName("client/products");
        return modelAndView;
    }

    /**
     * Возвращает страницу "client/product" с 1-м товаром с уникальним URL, который
     * совпадает с входящим параметром url. URL запроса "/product_{url}", метод GET.
     * В запросе в параметре url можно передавать как URL так и артикль товара.
     */
    @RequestMapping(value = "/product_{url}", method = RequestMethod.GET)
    public ModelAndView viewProduct(@PathVariable("url") String url, ModelAndView modelAndView) {
        Product product;

        try {
            int article = Integer.parseInt(url);
            product = this.productService.getByArticle(article);
        } catch (NumberFormatException ex) {
            product = this.productService.getByUrl(url);
        }

        long categoryId = product.getCategory().getId();

        modelAndView.addObject("product", product);
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.addObject("featured_products", this.productService.getRandomByCategoryId(3, categoryId, product.getId()));
        modelAndView.setViewName("client/product");
        return modelAndView;
    }

    /**
     * Возвращает страницу "client/cart" - страница корзины с торговыми позициями, которие сделал клиент.
     * URL запроса "/cart", метод GET.
     */
    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public ModelAndView viewCart(ModelAndView modelAndView) {
        modelAndView.addObject("sale_positions", this.shoppingCartService.getSalePositions());
        modelAndView.addObject("price_of_cart", this.shoppingCartService.getPrice());
        modelAndView.addObject("cart_size", this.shoppingCartService.getSize());
        modelAndView.setViewName("client/cart");
        return modelAndView;
    }

    /**
     * Добавляет товар с уникальным кодом id в корзину и перенаправляет по запросу "/cart".
     * URL запроса "/cart_add", метод POST.
     */
    @RequestMapping(value = "/cart_add", method = RequestMethod.POST)
    public ModelAndView addProductToCart(@RequestParam long id, ModelAndView modelAndView) {
        SalePosition salePosition = new SalePosition(this.productService.get(id), 1);
        this.shoppingCartService.add(salePosition);
        modelAndView.setViewName("redirect:/cart");
        return modelAndView;
    }

    /**
     * Быстрое добавления товара с уникальным номером id в корзину и перенаправление
     * по запросу входящего параметра url. URL запроса "/cart_add_quickly", метод POST.
     */
    @RequestMapping(value = "/cart_add_quickly", method = RequestMethod.POST)
    public ModelAndView addProductToCartQuickly(@RequestParam long id,
                                                @RequestParam String url,
                                                ModelAndView modelAndView) {
        SalePosition salePosition = new SalePosition(this.productService.get(id), 1);
        shoppingCartService.add(salePosition);
        modelAndView.setViewName("redirect:" + url);
        return modelAndView;
    }

    /**
     * Очищает корзину от торгвых позиции и перенаправление по запросу "/cart".
     * URL запроса "/cart_clear", метод GET.
     */
    @RequestMapping(value = "/cart_clear", method = RequestMethod.GET)
    public ModelAndView clearCart(ModelAndView modelAndView) {
        this.shoppingCartService.clear();
        modelAndView.setViewName("redirect:/cart");
        return modelAndView;
    }

    @RequestMapping(value = "/delete_saleposition_{id}", method = RequestMethod.GET)
    public ModelAndView deleteSalePosition(@PathVariable(value = "id") long id, ModelAndView modelAndView) {
        this.shoppingCartService.remove(id);
        modelAndView.setViewName("redirect:/cart");
        return modelAndView;
    }

    /**
     * Оформляет и сохраняет заказ клиента, возвращает страницу "client/checkout".
     * Если корзина пуста, по перенаправляет на главную страницу.
     * URL запроса "/checkout", метод POST.
     */
    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public ModelAndView viewCheckout(@RequestParam(value = "user_name") String name,
                                     @RequestParam(value = "user_email") String email,
                                     @RequestParam(value = "user_phone") String phone,
                                     ModelAndView modelAndView) {
        if (this.shoppingCartService.getSize() > 0)
        {
            Role role = this.roleService.getDefault();
            User client = new User(name, email, phone, role);

            Status status = this.statusService.getDefault();
            Order order = new Order(status, client, new ArrayList<SalePosition>(this.shoppingCartService.getSalePositions()));
            this.orderService.add(order);

            modelAndView.addObject("order", order);
            modelAndView.addObject("sale_positions", order.getSalePositions());
            modelAndView.addObject("price_of_cart", this.shoppingCartService.getPrice());
            modelAndView.addObject("cart_size", 0);
            modelAndView.setViewName("client/checkout");

            this.shoppingCartService.clear();
        } else {
            modelAndView.setViewName("redirect:/");
        }

        return modelAndView;
    }

    /**
     * Перенаправляет по запросу "/cart", если обратится по запросу "/checkout" методом GET.
     */
    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public ModelAndView viewCheckout(ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/cart");
        return modelAndView;
    }

    /*
    * Поиск по названию продукта
    * */
    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String greetingsAction(@RequestParam(value="pattern") String pattern,
                                  RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("products", this.productService.searchProduct(pattern));
        return "redirect:/results";
    }

    @RequestMapping(value = "/results")
    public ModelAndView viewResults(ModelAndView modelAndView) {

        modelAndView.setViewName("client/products");
        return modelAndView;
    }

    @RequestMapping(value = "/update_quantity_{id}", method = RequestMethod.POST)
    public ModelAndView saveProduct(@PathVariable(value = "id") long id,
                                    @RequestParam(value = "quantity") int quantity,
                                    ModelAndView modelAndView)
    {
        SalePosition salePosition = this.shoppingCartService.get(id);
        salePosition.setNumber(quantity);
        modelAndView.setViewName("redirect:/cart");

        return modelAndView;
    }
}