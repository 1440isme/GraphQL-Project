package vn.binh.graphqlproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.binh.graphqlproject.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    void deleteAll();
    void delete(User entity);
    void deleteById(Long id);
    long count();
    Optional<User> findById(Long id);
    List<User> findAllById(Iterable<Long> ids);
    List<User> findAll(Sort sort);
    Page<User> findAll(Pageable pageable);
    List<User> findAll();
    <S extends User> S save(S entity);
    List<User> findByNameContaining(String name);
    Page<User> findByNameContaining(String name, Pageable pageable);


    Optional<User> findByUserName(String UserName);
}
