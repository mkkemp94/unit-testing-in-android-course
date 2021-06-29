package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase
{
    interface Listener
    {
        void contactsFetched(List<Contact> contactsList);
    
        void failure();
    }
    
    private final GetContactsHttpEndpoint endpoint;
    private final ArrayList<Listener> listeners = new ArrayList<>();
    
    public FetchContactsUseCase(final GetContactsHttpEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }
    
    public void fetchContacts(final String filterTerm)
    {
        endpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback()
        {
            @Override
            public void onGetContactsSucceeded(final List<ContactSchema> contactSchemas)
            {
                final List<Contact> contactList = convertContactSchemasToContacts(contactSchemas);
                for (Listener listener : listeners)
                {
                    listener.contactsFetched(contactList);
                }
            }
    
            @Override
            public void onGetContactsFailed(final GetContactsHttpEndpoint.FailReason failReason)
            {
                switch (failReason)
                {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                    {
                        for (Listener listener : listeners)
                        {
                            listener.failure();
                        }
                        break;
                    }
                    
                    default:
                        throw new RuntimeException("invalid fail reason: " + failReason);
                }
            }
        });
    }
    
    private List<Contact> convertContactSchemasToContacts(final List<ContactSchema> contactSchemas)
    {
        final List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : contactSchemas)
        {
            contacts.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        }
        return contacts;
    }
    
    public void registerListener(Listener listener)
    {
        listeners.add(listener);
    }
    
    public void unregisterListener(final Listener listener)
    {
        listeners.remove(listener);
    }
}
