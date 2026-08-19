package com.nikhil.BankingApplication.repository;

import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.AccountType;
import com.nikhil.BankingApplication.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
    boolean existsByOwnerAndAccountType(Customer owner, AccountType accountType);
}
