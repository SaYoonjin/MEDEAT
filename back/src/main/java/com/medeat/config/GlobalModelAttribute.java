package com.medeat.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute
    public void setDefaultSidebar(Model model) {
        model.addAttribute("sidebarPath", "../common/sidebar_eat.jsp");
    }
}
