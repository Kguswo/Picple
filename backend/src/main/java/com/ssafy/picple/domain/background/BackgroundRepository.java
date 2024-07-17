package com.ssafy.picple.domain.background;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class BackgroundRepository {

    @PersistenceContext
    private EntityManager em;
}
