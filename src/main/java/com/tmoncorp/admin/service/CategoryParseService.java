package com.tmoncorp.admin.service;

import com.tmoncorp.admin.domain.SynonymCategory;
import com.tmoncorp.admin.domain.OriginalCategory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Original Category table 형식을 Synonym Category 형식으로 변환하는 클래스
public class CategoryParseService {
    private List<SynonymCategory> newSynonymCategoryList;
    private Map<Integer, String> categoryMap;
    private Map<Integer, Integer> parentMap;

    public CategoryParseService(List<OriginalCategory> originalCategoryList)  throws NullPointerException {
        this.categoryMap = new HashMap<>();
        this.parentMap = new HashMap<>();

        for (OriginalCategory originalCategory : originalCategoryList) {
            this.categoryMap.put(originalCategory.getCatNo(), originalCategory.getCatNm());
            this.parentMap.put(originalCategory.getCatNo(), originalCategory.getParentCatNo());
        }
        this.newSynonymCategoryList = makeCategoryList(originalCategoryList);
    }

    // Make list of SynonymCategory using list of OriginalCategory
    private List<SynonymCategory> makeCategoryList(List<OriginalCategory> originalCategoryList) {
        List<SynonymCategory> newSynonymCategoryList = new ArrayList<>();

        for (OriginalCategory originalCategory : originalCategoryList) {
            SynonymCategory synonymCategory = parseCategoryData(originalCategory);
            boolean isConnectedCategory = synonymCategory != null;

            if (isConnectedCategory) {
                newSynonymCategoryList.add(synonymCategory);
            }
        }

        return newSynonymCategoryList;
    }

    // Parse OriginalCategory to SynonymCategory
    private SynonymCategory parseCategoryData(OriginalCategory originalCategory) throws IndexOutOfBoundsException, NullPointerException {
        boolean isConnectedCategory = true;
        String depthName[] = {"", "", "", ""};

        int depthNumber = originalCategory.getDepth() - 1;
        depthName[depthNumber] = originalCategory.getCatNm();
        int parentCategoryNumber = originalCategory.getParentCatNo();

        // If parentCategoryNumber == 0, category is root or interrupted
        while (parentCategoryNumber != 0) {
            boolean isInterruptedCategory = categoryMap.get(parentCategoryNumber) == null;

            if (isInterruptedCategory) {
                isConnectedCategory = false;
                break;
            }

            depthNumber--;
            String parentCategoryName = categoryMap.get(parentCategoryNumber);
            depthName[depthNumber] = parentCategoryName;
            parentCategoryNumber = parentMap.get(parentCategoryNumber);
        }

        if (isConnectedCategory) {
            return new SynonymCategory(originalCategory.getCatNo(), depthName[0], depthName[1], depthName[2], depthName[3],
                    "", "", 0.0, true, "sys", new Timestamp(System.currentTimeMillis()));
        } else {
            return null;
        }
    }

    public List<SynonymCategory> getNewSynonymCategoryList() {
        return newSynonymCategoryList;
    }
}
