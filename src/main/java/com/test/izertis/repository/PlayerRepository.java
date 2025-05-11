package com.test.izertis.repository;

import com.test.izertis.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Page<Player> findByClubIdAndNationalityIgnoreCase(long clubId, String nationality, Pageable pageable);
    Page<Player> findByClubId(long clubId, Pageable pageable);

    long countByClubId(Long clubId);
}
