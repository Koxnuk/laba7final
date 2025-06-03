package com.example.currency.controller;

import com.example.currency.models.CurrencyInfo;
import com.example.currency.models.CurrencyRate;
import com.example.currency.service.CurrencyConversionService;
import com.example.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rates")
public class WebCurrencyRateController {

    private final CurrencyService currencyService;
    private final CurrencyConversionService conversionService;

    @Autowired
    public WebCurrencyRateController(CurrencyService currencyService, CurrencyConversionService conversionService) {
        this.currencyService = currencyService;
        this.conversionService = conversionService;
    }

    @GetMapping("/new")
    public String newRateForm(@RequestParam Integer currencyId, Model model) {
        CurrencyRate rate = new CurrencyRate();
        model.addAttribute("rate", rate);
        model.addAttribute("currencyId", currencyId);
        return "rates/new";
    }

    @PostMapping
    public String createRate(@ModelAttribute CurrencyRate rate, @RequestParam Integer currencyId) {
        CurrencyInfo currency = currencyService.getCurrencyById(currencyId).orElseThrow();
        rate.setCurrency(currency);
        conversionService.createRate(rate);
        return "redirect:/currencies/" + currencyId;
    }

    @GetMapping("/{id}/edit")
    public String editRateForm(@PathVariable Long id, Model model) {
        CurrencyRate rate = conversionService.getRateById(id).orElseThrow();
        model.addAttribute("rate", rate);
        return "rates/edit";
    }

    @PostMapping("/{id}")
    public String updateRate(@PathVariable Long id, @ModelAttribute CurrencyRate rate) {
        conversionService.updateRate(id, rate);
        return "redirect:/currencies/" + rate.getCurrency().getCurId();
    }

    @PostMapping("/{id}/delete")
    public String deleteRate(@PathVariable Long id) {
        CurrencyRate rate = conversionService.getRateById(id).orElseThrow();
        Integer currencyId = rate.getCurrency().getCurId();
        conversionService.deleteRate(id);
        return "redirect:/currencies/" + currencyId;
    }
}