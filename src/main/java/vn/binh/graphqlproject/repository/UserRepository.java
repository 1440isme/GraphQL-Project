package vn.binh.graphqlproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import vn.binh.graphqlproject.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserNameContaining(String userName);
    // Or case-insensitive:
    
    Page<User> findByUserNameContaining(String name, Pageable pageable);
    Optional<User> findByUserName(String name);

}
