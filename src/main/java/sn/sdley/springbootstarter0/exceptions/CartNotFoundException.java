package sn.sdley.springbootstarter0.exceptions;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(){
        super("Cart not found");
    }

}
