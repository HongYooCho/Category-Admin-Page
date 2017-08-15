package com.tmoncorp.admin.repository;

import com.tmoncorp.admin.domain.OriginalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalCategoryRepository extends JpaRepository<OriginalCategory, Long> {
}
