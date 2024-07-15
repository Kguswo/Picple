package com.ssafy.picple.domain.board.repository;

import com.ssafy.picple.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
