package com.codegym.controller;

import com.codegym.model.Cart;
import com.codegym.model.Product;
import com.codegym.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class ProductController {
    @Autowired
    private IProductService productService;

    @ModelAttribute("cart")
    public Cart setUpCart() {
        return new Cart();
    }

    @GetMapping("/shop")
    public ModelAndView showShop() {
        ModelAndView modelAndView = new ModelAndView("/shop");
        modelAndView.addObject("products", productService.findAll());
        return modelAndView;
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, @ModelAttribute Cart cart, @RequestParam("action") String action) {
        Optional<Product> product = productService.findById(id);
        if (!product.isPresent()) {
            return "/error.404";
        }
        if (action.equals("show")) {
            cart.addProduct(product.get());
            return "redirect:/shopping-cart";
        }
        cart.addProduct(product.get());
        return "redirect:/shop";
    }

    @GetMapping("/sub/{id}")
    public String subFromCart(@PathVariable Long id, @ModelAttribute Cart cart, @RequestParam("action") String action) {
        Optional<Product> product = productService.findById(id);
        if (!product.isPresent()) {
            return "/error.404";
        }
        if (action.equals("show")) {
            cart.subProduct(product.get());
            return "redirect:/shopping-cart";
        }
        cart.subProduct(product.get());
        return "redirect:/shop";
    }

    @GetMapping("/view/{id}")
    public ModelAndView showDetail(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/detail");
            modelAndView.addObject("product", product.get());
            return modelAndView;
        } else {
            return new ModelAndView("error.404");
        }
    }

    @GetMapping("/create")
    public ModelAndView showFormCreate() {
        return new ModelAndView("/create");
    }

    @PostMapping("/create")
    public ModelAndView saveProduct(Product product, @RequestParam MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(file.getBytes(), new File("C:\\Users\\Nguyen Viet Tien\\Desktop\\Codegym\\03_Baitap\\04_Module_4\\10_SessionvaCookie\\03-shopping-cart\\src\\main\\webapp\\picture\\" + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        product.setImage(fileName);
        productService.save(product);
        return new ModelAndView("redirect:/shop");
    }
}
