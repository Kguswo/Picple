package com.ssafy.picple.domain.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepository {

    @PersistenceContext
    private EntityManager em;

}
