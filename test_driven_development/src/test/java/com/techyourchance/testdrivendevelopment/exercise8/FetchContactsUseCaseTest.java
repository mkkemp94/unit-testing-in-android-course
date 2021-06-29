package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

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
public class FetchContactsUseCaseTest
{
    // region constants
    public static final String FILTER_TERM = "filter term";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    public static final String FULL_PHONE_NUMBER = "full phone number";
    public static final double AGE = 7.6;
    
    // endregion constants
    
    // region helper fields
    EndpointTd endpointTd;
    @Mock FetchContactsUseCase.Listener listenerMock1;
    @Mock FetchContactsUseCase.Listener listenerMock2;
    @Captor ArgumentCaptor<List<Contact>> contactCaptor;
    // endregion helper fields
    
    FetchContactsUseCase SUT;
    
    @Before
    public void setup() throws Exception
    {
        endpointTd = new EndpointTd();
        SUT = new FetchContactsUseCase(endpointTd);
        success();
    }
    
    @Test
    public void fetchContacts_givenFilterTerm_passesFilterTermToEndpoint() throws Exception
    {
        // Arrange
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
        
        // Assert
        assertThat(endpointTd.filterTerm, is(FILTER_TERM));
    }
    
    @Test
    public void fetchContacts_successFromEndpoint_callsEndpointOneTime() throws Exception
    {
        // Arrange
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
    
        // Assert
        assertThat(endpointTd.invocationCount, is(1));
    }
    
    @Test
    public void fetchContacts_successFromEndpoint_notifyListenersOfSuccess() throws Exception
    {
        // Arrange
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
    
        // Assert
        verify(listenerMock1).contactsFetched(contactCaptor.capture());
        verify(listenerMock2).contactsFetched(contactCaptor.capture());
        final List<List<Contact>> allValues = contactCaptor.getAllValues();
        final List<Contact> capture1 = allValues.get(0);
        final List<Contact> capture2 = allValues.get(1);
        assertThat(capture1, is(getContactsList()));
        assertThat(capture2, is(getContactsList()));
    }
    
    @Test
    public void fetchContacts_successFromEndpoint_unsubscribedObserversNotNotified() throws Exception
    {
        // Arrange
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock2);
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
        
        // Assert
        verify(listenerMock1).contactsFetched(any(List.class));
        verifyNoMoreInteractions(listenerMock2);
    }
    
    @Test
    public void fetchContacts_generalErrorFromEndpoint_notifyListenersWithFailure() throws Exception
    {
        // Arrange
        generalErrorFromEndpoint();
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
        
        // Assert
        verify(listenerMock1).failure();
        verify(listenerMock2).failure();
    }
    
    @Test
    public void fetchContacts_networkErrorFromEndpoint_notifyListenersWithFailure() throws Exception
    {
        // Arrange
        networkErrorFromEndpoint();
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        
        // Act
        SUT.fetchContacts(FILTER_TERM);
        
        // Assert
        verify(listenerMock1).failure();
        verify(listenerMock2).failure();
    }
    
    // region helper methods
    private void success()
    {
        // no op
    }
    
    private List<Contact> getContactsList()
    {
        final ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }
    
    private List<ContactSchema> getContactSchemaList()
    {
        final ArrayList<ContactSchema> schemas = new ArrayList<>();
        schemas.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return schemas;
    }
    
    private void generalErrorFromEndpoint()
    {
        endpointTd.generalError = true;
    }
    
    private void networkErrorFromEndpoint()
    {
        endpointTd.networkError = true;
    }
    // endregion helper methods
    
    // region helper classes
    private class EndpointTd implements GetContactsHttpEndpoint
    {
        public String filterTerm;
        public int invocationCount;
        
        public boolean generalError = false;
        public boolean networkError = false;
    
        @Override
        public void getContacts(final String filterTerm, final Callback callback)
        {
            this.filterTerm = filterTerm;
            this.invocationCount++;
            
            if (generalError)
            {
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
            }
            else if (networkError)
            {
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
            }
            else
            {
                callback.onGetContactsSucceeded(getContactSchemaList());
            }
        }
    }
    // endregion helper classes
}