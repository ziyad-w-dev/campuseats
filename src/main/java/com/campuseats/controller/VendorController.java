package com.campuseats.controller;

import com.campuseats.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Rails equivalent: VendorsController < ApplicationController
@Controller
@RequiredArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;

    // Rails: GET /vendors -> vendors#index, renders app/views/vendors/index.html.erb
    @GetMapping("/vendors")
    public String index(Model model) {
        model.addAttribute("vendors", vendorRepository.findAll()); // Rails: @vendors = Vendor.all
        return "vendors/index";
    }
}
