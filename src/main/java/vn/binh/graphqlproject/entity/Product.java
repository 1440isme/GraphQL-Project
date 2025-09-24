package vn.binh.graphqlproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(length = 500, columnDefinition = "nvarchar(500) not null")
    private String productName;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private double unitPrice;
    @Column(length = 200)
    private String description;
    @Column(nullable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    @JsonIgnore
    @ManyToMany
    @JoinColumn(name = "productId")
    private  User user;
}