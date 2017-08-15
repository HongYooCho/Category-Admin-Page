package com.tmoncorp.admin.repository;

import com.tmoncorp.admin.domain.SynonymCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sk2rldnr on 2017-07-10.
 */
@Component
public class CategoryRepositorySeeder implements CommandLineRunner {
    private SynonymCategoryRepository synonymCategoryRepository;
    private OriginalCategoryRepository originalCategoryRepository;

    @Autowired
    public CategoryRepositorySeeder(SynonymCategoryRepository synonymCategoryRepository, OriginalCategoryRepository originalCategoryRepository) {
            this.synonymCategoryRepository = synonymCategoryRepository;
            this.originalCategoryRepository = originalCategoryRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        List<SynonymCategory> synonymCategoryData = new ArrayList<>();
    }
}