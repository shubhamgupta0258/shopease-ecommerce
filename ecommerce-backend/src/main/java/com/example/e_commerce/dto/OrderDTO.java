//package com.example.e_commerce.dto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//    public class OrderDTO {
//        private Long id;
//        private LocalDateTime createdAt;
//        private Double totalAmount;
//        private String userName;
//        private List<String> productNames;  // instead of sending full Product objects
//
//        // constructor
//        public OrderDTO(Long id, LocalDateTime createdAt, Double totalAmount,String userName, List<String> productNames) {
//            this.id = id;
//            this.createdAt = createdAt;
//            this.totalAmount = totalAmount;
//            this.userName=userName;
//            this.productNames = productNames;
//        }
//
//        // getters
//        public Long getId() { return id; }
//        public LocalDateTime getCreatedAt() { return createdAt; }
//        public Double getTotalAmount() { return totalAmount; }
//        public String getUserName() { return userName; }
//        public List<String> getProductNames() { return productNames; }
//
//    }

//    package com.example.e_commerce.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class OrderDTO {
//    private Long id;
//    private LocalDateTime createdAt;
//    private Double totalAmount;
//    private String username;              // extracted from User entity
//    private List<String> productNames;    // extracted from Product list
//}
package com.example.e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private String username;              // from User entity
    private List<String> productNames;    // from Product list
}


