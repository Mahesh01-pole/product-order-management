package com.example.pms.repository;

import com.example.pms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByEnabledTrueOrderByIdAsc();

    List<Product> findAllByOrderByIdAsc();

    @Query("select p.quantity from Product p where p.id = :id")
    Integer findQuantityById(@Param("id") Long id);
    @Modifying
    @Query("update Product p set p.quantity = p.quantity - :qty "
            + "where p.id = :id and p.enabled = true and p.quantity >= :qty")
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);
}
