package com.tmoncorp.admin.controller;

import com.tmoncorp.admin.domain.SynonymCategory;
import com.tmoncorp.admin.service.ElasticService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticController {
    private ElasticService elasticService = new ElasticService();

    @RequestMapping(value = "/synonym", method = RequestMethod.POST)
    public void index(@RequestBody List<SynonymCategory> synonymCategoryList) {
        int id = 0;
        elasticService.makeIndex("Category", "Synonym", id + "", synonymCategoryList.get(id));
    }
}
