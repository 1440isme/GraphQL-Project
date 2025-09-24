package vn.binh.graphqlproject.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryModel {
    private Long categoryId;
    @NotEmpty
    @Length(min = 5)
    private String name;
    private Boolean isEdit = false;

}
