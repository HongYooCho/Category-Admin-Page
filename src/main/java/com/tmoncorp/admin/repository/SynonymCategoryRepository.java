package com.tmoncorp.admin.repository;

import com.tmoncorp.admin.domain.SynonymCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sk2rldnr on 2017-07-10.
 */
@Repository
public interface SynonymCategoryRepository extends JpaRepository<SynonymCategory, Long> {
}
