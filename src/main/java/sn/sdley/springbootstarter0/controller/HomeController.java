package sn.sdley.springbootstarter0.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Tag(name = "Home", description = "Endpoints for the application's web interface")
public class HomeController {

    @RequestMapping("/")
    @Operation(summary = "Render the home page", description = "Renders the application's home page.")
    public String index(Model model) {
        model.addAttribute("name", "Souleymane");
        return "index";
    }
}
