package sn.sdley.storefrontbackend.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Home", description = "Endpoints for the application's web interface")
public class HomeController {

    @GetMapping({"/", "/index.html"})
    @Operation(summary = "Render the home page", description = "Renders the public storefront landing page.")
    public String index(Model model) {
        model.addAttribute("siteName", "Storefront API");
        return "index";
    }
}
