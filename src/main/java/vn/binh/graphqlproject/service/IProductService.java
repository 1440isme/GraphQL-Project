package vn.binh.graphqlproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.binh.springbootsproject.entity.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    void deleteAll();
    void delete(Product entity);
    void deleteById(Long id);
    long count();
    Optional<Product> findById(Long id);
    List<Product> findAllById(Iterable<Long> ids);
    List<Product> findAll(Sort sort);
    Page<Product> findAll(Pageable pageable);
    List<Product> findAll();
    <S extends Product> S save(S entity);
    List<Product> findByNameContaining(String name);
    Page<Product> findByNameContaining(String name, Pageable pageable);

    Optional<Product> findByCreateDate(Timestamp timestamp);

    Optional<Product> findByProductName(String productName);
}
