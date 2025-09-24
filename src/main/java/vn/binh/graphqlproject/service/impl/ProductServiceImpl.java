package vn.binh.graphqlproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import vn.binh.springbootsproject.entity.Product;
import vn.binh.springbootsproject.repository.ProductRepository;
import vn.binh.springbootsproject.service.IProductService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
    }

    @Override
    public void delete(Product entity) {
        productRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public long count() {
        return productRepository.count();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAllById(Iterable<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    public List<Product> findAll(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public <S extends Product> S save(S entity) {
        if (entity.getProductId() == null) {
            return productRepository.save(entity);
        } else {
            Optional<Product> optImages = findById(entity.getProductId());
            if (StringUtils.isEmpty(entity.getImages())) {
                entity.setImages(optImages.get().getImages());
            } else
                entity.setImages(entity.getImages());
        }
        return productRepository.save(entity);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByProductNameContaining(name);
    }

    @Override
    public Page<Product> findByNameContaining(String name, Pageable pageable) {
        return productRepository.findByProductNameContaining(name, pageable);
    }

    @Override
    public Optional<Product> findByCreateDate(Timestamp timestamp) {
        return productRepository.findByCreateDate(timestamp);
    }

    @Override
    public Optional<Product> findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }

}
