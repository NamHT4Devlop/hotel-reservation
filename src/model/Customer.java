package model;

import validation.Validation;

public record Customer(String firstName, String lastName, String email) {

    public Customer {
        Validation.isValidEmail(email);
    }

    @Override
    public String toString() {
        return "First Name: " + this.firstName
                + " Last Name: " + this.lastName
                + " Email: " + this.email;
    }

}
