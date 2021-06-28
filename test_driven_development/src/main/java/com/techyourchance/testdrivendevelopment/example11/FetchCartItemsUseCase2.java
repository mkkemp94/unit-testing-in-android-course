package com.techyourchance.testdrivendevelopment.example11;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.example11.networking.CartItemSchema;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchCartItemsUseCase2
{
    public interface Listener
    {
        void onCartItemsFetched(List<CartItem> cartItems);
    
        void onFetchCartItemsFailed();
    }
    
    private final GetCartItemsHttpEndpoint getCartItemsHttpEndpoint;
    
    private final ArrayList<Listener> listeners = new ArrayList<>();
    
    public FetchCartItemsUseCase2(final GetCartItemsHttpEndpoint getCartItemsHttpEndpoint)
    {
        this.getCartItemsHttpEndpoint = getCartItemsHttpEndpoint;
    }
    
    public void fetchCartItemsAndNotify(final int limit)
    {
        getCartItemsHttpEndpoint.getCartItems(limit, new GetCartItemsHttpEndpoint.Callback()
        {
            @Override
            public void onGetCartItemsSucceeded(final List<CartItemSchema> cartItemSchemas)
            {
                for (Listener listener : listeners)
                {
                    final List<CartItem> cartItems = convertCartItemSchemasToCartItems(cartItemSchemas);
                    listener.onCartItemsFetched(cartItems);
                }
            }
    
            @Override
            public void onGetCartItemsFailed(final GetCartItemsHttpEndpoint.FailReason failReason)
            {
                switch (failReason)
                {
                    case NETWORK_ERROR:
                    case GENERAL_ERROR:
                    {
                        for (Listener listener : listeners)
                        {
                            listener.onFetchCartItemsFailed();
                        }
                    }
                    default:
                        throw new RuntimeException("invalid fail reason: " + failReason);
                }
                
            }
        });
    }
    
    private List<CartItem> convertCartItemSchemasToCartItems(final List<CartItemSchema> cartItemSchemas)
    {
        final ArrayList<CartItem> cartItems = new ArrayList<>();
        for (CartItemSchema cartItemSchema : cartItemSchemas)
        {
            cartItems.add(new CartItem(
                    cartItemSchema.getId(),
                    cartItemSchema.getTitle(),
                    cartItemSchema.getDescription(),
                    cartItemSchema.getPrice()
            ));
        }
        return cartItems;
    }
    
    public void addListener(final Listener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(final Listener listener)
    {
        listeners.remove(listener);
    }
}
