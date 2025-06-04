package com.example.currency.controller;

import com.example.currency.models.CurrencyInfo;
import com.example.currency.service.CurrencyConversionService;
import com.example.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/currencies")
public class WebCurrencyController {

    private final CurrencyService currencyService;
    private final CurrencyConversionService conversionService;

    @Autowired
    public WebCurrencyController(CurrencyService currencyService, CurrencyConversionService conversionService) {
        this.currencyService = currencyService;
        this.conversionService = conversionService;
    }

    @GetMapping
    public String listCurrencies(Model model) {
        model.addAttribute("currencies", currencyService.getAllCurrenciesFromDb());
        return "currencies/list";
    }

    @GetMapping("/new")
    public String newCurrencyForm(Model model) {
        model.addAttribute("currency", new CurrencyInfo());
        return "currencies/new";
    }

    @PostMapping
    public String createCurrency(@ModelAttribute CurrencyInfo currency) {
        currencyService.createCurrency(currency);
        return "redirect:/currencies";
    }

    @GetMapping("/{id}")
    public String viewCurrency(@PathVariable Integer id, Model model) {
        CurrencyInfo currency = currencyService.getCurrencyByIdWithRates(id).orElseThrow();
        model.addAttribute("currency", currency);
        return "currencies/view";
    }

    @GetMapping("/{id}/edit")
    public String editCurrencyForm(@PathVariable Integer id, Model model) {
        CurrencyInfo currency = currencyService.getCurrencyById(id).orElseThrow();
        model.addAttribute("currency", currency);
        return "currencies/edit";
    }

    @PostMapping("/{id}")
    public String updateCurrency(@PathVariable Integer id, @ModelAttribute CurrencyInfo currency) {
        currencyService.updateCurrency(id, currency);
        return "redirect:/currencies";
    }

    @PostMapping("/{id}/delete")
    public String deleteCurrency(@PathVariable Integer id) {
        currencyService.deleteCurrency(id);
        return "redirect:/currencies";
    }

    @GetMapping("/convert")
    public String convertForm(Model model) {
        model.addAttribute("currencies", currencyService.getAllCurrenciesFromDb());
        return "convert";
    }

    @PostMapping("/convert")
    public String convert(
            @RequestParam Integer from,
            @RequestParam Integer to,
            @RequestParam BigDecimal amount,
            Model model) {
        Map<String, Object> result = conversionService.convertCurrencyWithValidation(from, to, amount);
        model.addAttribute("result", result.get("result"));
        model.addAttribute("from", result.get("from"));
        model.addAttribute("to", result.get("to"));
        model.addAttribute("amount", result.get("amount"));
        model.addAttribute("currencies", currencyService.getAllCurrenciesFromDb());
        return "convert";
    }
}
