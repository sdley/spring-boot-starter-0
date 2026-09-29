package sn.sdley.storefrontbackend.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Administrator-only endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    @GetMapping("/hello")
    @Operation(summary = "Say hello", description = "Returns a greeting to authenticated users with the administrator role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Greeting returned"),
            @ApiResponse(responseCode = "401", description = "Authentication is required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Administrator role is required", content = @Content)
    })
    public String sayHello(){
        return "Hello Admin!";
    }
}
