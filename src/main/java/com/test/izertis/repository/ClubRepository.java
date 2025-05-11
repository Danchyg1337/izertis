package com.test.izertis.repository;

import com.test.izertis.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Page<Club> findAllByIsPublicTrue(Pageable pageable);

    Page<Club> findAllByIsPublicTrueAndFederation(String federation, Pageable pageable);

    Optional<Club> findByUsername(String username);
}
