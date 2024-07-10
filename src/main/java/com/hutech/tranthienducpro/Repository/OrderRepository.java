package com.hutech.tranthienducpro.Repository;

import com.hutech.tranthienducpro.model.Order;
import com.hutech.tranthienducpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByTransactionId(String transactionId);
    @Query("select o from Order o where o.user.id=?1")
    List<Order> findOrderByUserId(Long userId);
}
