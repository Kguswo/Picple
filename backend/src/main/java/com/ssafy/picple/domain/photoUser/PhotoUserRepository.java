package com.ssafy.picple.domain.photoUser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoUserRepository {

    @PersistenceContext
    private EntityManager em;

}
