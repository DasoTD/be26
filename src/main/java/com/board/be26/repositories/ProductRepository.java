package com.board.be26.repositories;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.board.be26.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /*~~(class org.openrewrite.java.tree.J$Erroneous cannot be cast to class org.openrewrite.java.tree.J$Assignment (org.openrewrite.java.tree.J$Erroneous and org.openrewrite.java.tree.J$Assignment are in unnamed module of loader 'app'))~~>*/@Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);
    
}
