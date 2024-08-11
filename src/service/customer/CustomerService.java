package service.customer;

import model.Customer;

import java.util.*;

public class CustomerService {

    private static final CustomerService CUSTOMER_SERVICE_INSTANCE = new CustomerService();

    private final Map<String, Customer> customerMap = new HashMap<>();

    private CustomerService() {}

    public static CustomerService getCustomerServiceInstance() {
        return CUSTOMER_SERVICE_INSTANCE;
    }

    public void addCustomer(final String email, final String firstName, final String lastName) {
        String normalizedEmail = normalizeEmail(email);
        customerMap.computeIfAbsent(normalizedEmail, e -> new Customer(firstName, lastName, e));
    }

    public Optional<Customer> getCustomer(final String email) {
        return Optional.ofNullable(customerMap.get(normalizeEmail(email)));
    }

    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customerMap.values());
    }

    private String normalizeEmail(final String email) {
        return Optional.ofNullable(email)
                .map(String::trim)
                .map(String::toLowerCase)
                .orElseThrow(() -> new IllegalArgumentException("Email must not be null or empty."));
    }
}
