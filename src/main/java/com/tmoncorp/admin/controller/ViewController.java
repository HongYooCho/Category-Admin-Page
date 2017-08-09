package com.tmoncorp.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * Created by sk2rldnr on 2017-07-10.
 */
@Controller
public class ViewController {

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("datetime", new Date());

        return "index";
    }
}
