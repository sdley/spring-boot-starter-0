package sn.sdley.springbootstarter0.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.sdley.springbootstarter0.dtos.CheckoutRequest;
import sn.sdley.springbootstarter0.dtos.CheckoutResponse;
import sn.sdley.springbootstarter0.dtos.ErrorDto;
import sn.sdley.springbootstarter0.exceptions.CartEmptyException;
import sn.sdley.springbootstarter0.exceptions.CartNotFoundException;
import sn.sdley.springbootstarter0.service.CheckoutService;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request){
        return checkoutService.checkout(request);
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex){
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
