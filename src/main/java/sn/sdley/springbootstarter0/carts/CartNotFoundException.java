package sn.sdley.springbootstarter0.carts;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(){
        super("Cart not found");
    }

}
