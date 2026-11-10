package com.manara.crm.dto;

public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String date; // optionnel

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if (phone == null) {
            this.phone = null;
            return;
        }
        this.phone = phone.replaceAll("\\D", "");
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}