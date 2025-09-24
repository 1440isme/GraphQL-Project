package vn.binh.graphqlproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Long productId;
    private String name;
    private int quantity;
    private Long categoryId;
    private String description;
    private String images;
    private Double unitPrice;
    private Boolean isEdit = false;
    private MultipartFile imageFile;
    private double discount;
    private Date createdDate;
    private short status;
}
