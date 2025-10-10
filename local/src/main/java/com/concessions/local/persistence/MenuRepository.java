package com.concessions.local.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concessions.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

}
