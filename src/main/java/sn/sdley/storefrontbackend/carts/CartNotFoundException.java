package sn.sdley.storefrontbackend.carts;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(){
        super("Cart not found");
    }

}
