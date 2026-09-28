package sn.sdley.springbootstarter0.exceptions;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException(){
        super("Cart is empty");
    }
}
