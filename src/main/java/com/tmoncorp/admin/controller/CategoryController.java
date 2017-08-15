package com.tmoncorp.admin.controller;

import com.tmoncorp.admin.domain.SynonymCategory;
import com.tmoncorp.admin.file.ReadCSV;
import com.tmoncorp.admin.repository.OriginalCategoryRepository;
import com.tmoncorp.admin.repository.SynonymCategoryRepository;
import com.tmoncorp.admin.service.CategoryParseService;
import com.tmoncorp.admin.service.CategoryRenewalService;
import com.tmoncorp.admin.service.UploadFileDecodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sk2rldnr on 2017-07-10.
 */
@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    private enum SynchronizeState {
        CATEGORY_RENEWAL(0), UPLOAD(1), UPDATE(2);
        private final int code;
        SynchronizeState(int code) {
            this.code = code;
        }
    }
    private SynonymCategoryRepository synonymCategoryRepository;
    private OriginalCategoryRepository originalCategoryRepository;

    @Autowired
    public CategoryController(SynonymCategoryRepository synonymCategoryRepository, OriginalCategoryRepository originalCategoryRepository) {
        this.synonymCategoryRepository = synonymCategoryRepository;
        this.originalCategoryRepository = originalCategoryRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Map> dataLoadFromDB() {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            responseMap.put("categoryList", synonymCategoryRepository.findAll());
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (Exception e) {
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Renewal new category
    @RequestMapping(value = "/renewal", method = RequestMethod.GET)
    public ResponseEntity<Map> renewalCategory() {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Parse originalCategoryList as synonymCategoryList
            List<SynonymCategory> synonymCategoryList = new CategoryParseService(originalCategoryRepository.findAll()).getNewSynonymCategoryList();
            // Update synonymCategoryList
            CategoryRenewalService categoryRenewalService = new CategoryRenewalService(synonymCategoryRepository.findAll(), synonymCategoryList);

            synonymCategoryList = categoryRenewalService.getRenewedSynonymCategoryList();
            List<Integer> changedCategoryId = categoryRenewalService.getChangedCategoryId();

            responseMap.put("categoryList", synonymCategoryList);
            responseMap.put("changedList", changedCategoryId);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (Exception e) {
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Upload .csv file to UI
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map> uploadFile(MultipartHttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String fileContent = new UploadFileDecodeService(request).decodeUploadFile();
            List<SynonymCategory> synonymCategoryList = new ReadCSV().readFromFile(fileContent);

            responseMap.put("categoryList", synonymCategoryList);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

            // Validation of import file
        } catch (IndexOutOfBoundsException | ParseException e) {
            System.out.println(e.getMessage());
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);

            // IO exception
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Download .csv file from html
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Map> downloadAsCSV() {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // using Stream, make csv string
            String csvFormatString = synonymCategoryRepository.findAll().stream()
                    .map(SynonymCategory::makeCsvString)
                    .collect(Collectors.joining(System.getProperty("line.separator")));

            responseMap.put("csvContent", csvFormatString);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (NullPointerException e) {
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Synchronize UI & DB
    @RequestMapping(value = "/synchronize", method = RequestMethod.POST)
    public ResponseEntity<Map> synchronizeWithDB(@RequestBody List<SynonymCategory> synonymCategoryList, @RequestParam("renewal") int isRenewal,
                                                 @RequestParam("changedCatNo") List<Integer> changedCatNo) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // 카테고리 개편시 새로운 카테고리로 DB 업데이트
            if (isRenewal == SynchronizeState.CATEGORY_RENEWAL.code) {
                synonymCategoryRepository.deleteAll();
                synonymCategoryRepository.save(synonymCategoryList);

                // csv file upload
            } else if (isRenewal == SynchronizeState.UPLOAD.code) {
                synonymCategoryRepository.save(synonymCategoryList);

                // 일반적인 DB 반영일 때, 변화가 있는 row 만 업데이트
            } else if (isRenewal == SynchronizeState.UPDATE.code) {
                for (SynonymCategory synonymCategory : synonymCategoryList) {
                    if (changedCatNo.contains(synonymCategory.getCatNo())) {
                        synonymCategoryRepository.save(synonymCategory);
                    }
                }
            }
            responseMap.put("ok", "Synchronized");
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (Exception e) {
            responseMap.put("err", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}