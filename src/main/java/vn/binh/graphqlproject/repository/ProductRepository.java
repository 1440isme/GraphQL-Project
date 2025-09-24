package vn.binh.graphqlproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.binh.graphqlproject.entity.Product;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContaining(String name);
    Page<Product> findByProductNameContaining(String name,Pageable  pageable);
    Optional<Product> findByProductName(String name);

    // Find all products by the Category's primary key (categoryId)
    List<Product> findByCategoryCategoryId(Long categoryId);
}
