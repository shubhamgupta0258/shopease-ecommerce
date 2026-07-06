//package com.example.e_commerce.models;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
////entity shows that this class represents a table in the database.
//// Each object of this class will correspond to a row in that table.
//// It's a way of marking the class as something that will be saved in the database.
////A no-argument constructor is required. This is mandatory for JPA to instantiate the entity using reflection.
//// If no constructors are defined, Java provides a default no-argument constructor automatically.
////WITHOUT @Entity, your class is just a RANDOM CLASS.JPA will completely ignore it.
//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//@NoArgsConstructor
////@AllArgsConstructor
//public class User {
//    //    commented below because applied @Getters and @Setters
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String email;
//    private String password;
////    private String username;
//
//    //commented below because applied @NoArgsConstructor
////    public User(){}
////    commented below because applied @AllArgsConstructor
//    public User(Long id ,String name, String email, String password,String Username) {
//
//        this.id=id;
//        this.name=name;
//        this.email=email;
//        this.password=password;
//        this.username=username;
//    }
//
//    public String getUsername() {
//        return email;
//    }
//
//    public void setUsername(String username) {this.username = username;}
//}
package com.example.e_commerce.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // columnDefinition backfills existing rows with a default when Hibernate adds this column
    @Column(columnDefinition = "varchar(255) default 'ROLE_USER'", nullable = false)
    private String role = "ROLE_USER";

    /**
     * 🔥 CRITICAL:
     * Spring Security uses getUsername() internally.
     * We want EMAIL to be treated as the username.
     */
    public String getUsername() {
        return this.email;
    }
}
