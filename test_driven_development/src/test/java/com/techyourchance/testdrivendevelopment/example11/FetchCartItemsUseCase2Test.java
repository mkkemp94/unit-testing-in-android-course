package com.techyourchance.testdrivendevelopment.example11;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.example11.networking.CartItemSchema;
import com.techyourchance.testdrivendevelopment.example11.networking.GetCartItemsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@RunWith(MockitoJUnitRunner.class)
public class FetchCartItemsUseCase2Test
{
    // region constants
    public static final int LIMIT = 5;
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final int PRICE = 5;
    // endregion constants
    
    // region helper fields
    
    // endregion helper fields
    
    @Mock FetchCartItemsUseCase2.Listener listenerMock1;
    @Mock FetchCartItemsUseCase2.Listener listenerMock2;
    GetCartItemsHttpEndpointTd httpEndpointTd;
    
    FetchCartItemsUseCase2 SUT;
    
    @Captor ArgumentCaptor<List<CartItem>> mAcListCartItem;
    
    @Before
    public void setup()
    {
        httpEndpointTd = new GetCartItemsHttpEndpointTd();
        SUT = new FetchCartItemsUseCase2(httpEndpointTd);
    }
    
    @Test
    public void getCartItems_correctLimitPassedToEndpoint()
    {
        // Arrange
        
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        
        // Assert
        assertThat(httpEndpointTd.invocationCount, is(1));
        assertThat(httpEndpointTd.lastLimit, is(LIMIT));
    }
    
    @Test
    public void getCartItems_success_allObserversNotifiedWithCorrectData()
    {
        // Arrange
        SUT.addListener(listenerMock1);
        SUT.addListener(listenerMock2);
        
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        
        // Assert
        verify(listenerMock1).onCartItemsFetched(mAcListCartItem.capture());
        verify(listenerMock2).onCartItemsFetched(mAcListCartItem.capture());
        List<List<CartItem>> captures = mAcListCartItem.getAllValues();
        List<CartItem> capture1 = captures.get(0);
        List<CartItem> capture2 = captures.get(1);
        assertThat(capture1, is(getCartItems()));
        assertThat(capture2, is(getCartItems()));
    }
    
    @Test
    public void getCartItems_success_unsubscribedObserversNotNotified()
    {
        // Arrange
        SUT.addListener(listenerMock1);
        SUT.addListener(listenerMock2);
        SUT.removeListener(listenerMock2);
        
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        
        // Assert
        verify(listenerMock1).onCartItemsFetched(any(List.class));
        verifyNoMoreInteractions(listenerMock2);
    }
    
    @Test
    public void getCartItems_generalError_observersNotifiedOfFailure() throws Exception
    {
        // Arrange
        SUT.addListener(listenerMock1);
        SUT.addListener(listenerMock2);
        setup_generalError();
        
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        
        // Assert
        verify(listenerMock1).onFetchCartItemsFailed();
        verify(listenerMock2).onFetchCartItemsFailed();
    }
    
    @Test
    public void getCartItems_networkError_observersNotifiedOfFailure()
    {
        // Arrange
        SUT.addListener(listenerMock1);
        SUT.addListener(listenerMock2);
        setup_networkError();
        
        // Act
        SUT.fetchCartItemsAndNotify(LIMIT);
        
        // Assert
        verify(listenerMock1).onFetchCartItemsFailed();
        verify(listenerMock2).onFetchCartItemsFailed();
    }
    
    // region helper methods
    private List<CartItem> getCartItems()
    {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(ID, TITLE, DESCRIPTION, PRICE));
        return cartItems;
    }
    
    private List<CartItemSchema> getCartItemSchemas()
    {
        ArrayList<CartItemSchema> cartItemSchemas = new ArrayList<>();
        cartItemSchemas.add(new CartItemSchema(ID, TITLE, DESCRIPTION, PRICE));
        return cartItemSchemas;
    }
    
    private void setup_generalError()
    {
        httpEndpointTd.generalError = true;
    }
    
    private void setup_networkError()
    {
        httpEndpointTd.networkError = true;
    }
    // endregion helper methods
    
    // region helper classes
    private class GetCartItemsHttpEndpointTd implements GetCartItemsHttpEndpoint
    {
        public boolean generalError = false;
        public boolean networkError = false;
    
        private int invocationCount;
        private int lastLimit;
        
        @Override
        public void getCartItems(final int limit, final Callback callback)
        {
            this.invocationCount++;
            this.lastLimit = limit;
            
            if (generalError)
            {
                callback.onGetCartItemsFailed(FailReason.GENERAL_ERROR);
            }
            else if (networkError)
            {
                callback.onGetCartItemsFailed(FailReason.NETWORK_ERROR);
            }
            else
            {
                callback.onGetCartItemsSucceeded(getCartItemSchemas());
            }
        }
    }
    // endregion helper classes
}