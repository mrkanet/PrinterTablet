package net.mrkaan.printer.model;

import java.util.List;

public class User {

    String email, lastName, name, password, phone;
    Response orders;
    List<String> photoLibrary;
    int userId;

    public User(String email,
                String lastName,
                String name,
                Response orders,
                List<String> photoLibrary,
                String password,
                String phone,
                int userId) {
        this.email = email;
        this.lastName = lastName;
        this.name = name;
        this.orders = orders;
        this.photoLibrary = photoLibrary;
        this.password = password;
        this.phone = phone;
        this.userId = userId;
    }


}
