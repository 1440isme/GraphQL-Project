package vn.binh.graphqlproject.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    private  String fullName;
    @Column(nullable = false)
    private  String email;
    @Column(nullable = false)
    private  String password;
    @Column(nullable = false)
    private  String phone;

    @JsonIgnore
    @ManyToMany
    @JoinColumn(name = "userId")
    private  Product product;
}
