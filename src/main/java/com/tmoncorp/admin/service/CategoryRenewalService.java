package com.tmoncorp.admin.service;

import com.tmoncorp.admin.domain.SynonymCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 카테고리를 변경시 기존 Synonym Category의 데이터와 동기화 및 추가된 카테고리 정보 보존
public class CategoryRenewalService {
    private List<SynonymCategory> renewedSynonymCategoryList;
    private List<SynonymCategory> preSynonymCategoryList;
    private List<Integer> changedCategoryId = new ArrayList<>();

    public CategoryRenewalService(List<SynonymCategory> preSynonymCategoryList, List<SynonymCategory> renewedSynonymCategoryList) {
        this.renewedSynonymCategoryList = renewedSynonymCategoryList;
        this.preSynonymCategoryList = preSynonymCategoryList;

        renewalCategoryList();
    }

    private void renewalCategoryList() throws NullPointerException {
        Map<Integer, SynonymCategory> preCategoryMap = new HashMap<>();

        for (SynonymCategory preSynonymCategory : preSynonymCategoryList) {
            preCategoryMap.put(preSynonymCategory.getCatNo(), preSynonymCategory);
        }

        for (SynonymCategory renewedSynonymCategory : renewedSynonymCategoryList) {
            boolean isChangedCategory = checkCategoryIsChanged(preCategoryMap, renewedSynonymCategory);

            if (isChangedCategory) {
                changedCategoryId.add(renewedSynonymCategory.getCatNo());
            } else {
                updateCategoryData(preCategoryMap.get(renewedSynonymCategory.getCatNo()), renewedSynonymCategory);
            }
        }
    }

    private boolean checkCategoryIsChanged(Map<Integer, SynonymCategory> preCategoryMap, SynonymCategory renewedSynonymCategory) {
        boolean isNewCategory = !preCategoryMap.containsKey(renewedSynonymCategory.getCatNo());

        if (isNewCategory) {
            return true;
        }
        SynonymCategory preSynonymCategory = preCategoryMap.get(renewedSynonymCategory.getCatNo());

        // Check whether category names equal or not
        return !(preSynonymCategory.getFirstCatName().equals(renewedSynonymCategory.getFirstCatName())
                && preSynonymCategory.getSecondCatName().equals(renewedSynonymCategory.getSecondCatName())
                && preSynonymCategory.getThirdCatName().equals(renewedSynonymCategory.getThirdCatName())
                && preSynonymCategory.getFourthCatName().equals(renewedSynonymCategory.getFourthCatName()));
    }

    private void updateCategoryData(SynonymCategory preSynonymCategory, SynonymCategory renewedSynonymCategory) {
        renewedSynonymCategory.setSameWords(preSynonymCategory.getSameWords());
        renewedSynonymCategory.setSimilarWords(preSynonymCategory.getSimilarWords());
        renewedSynonymCategory.setWeight(preSynonymCategory.getWeight());
        renewedSynonymCategory.setAvailability(preSynonymCategory.getAvailability());
        renewedSynonymCategory.setWorker(preSynonymCategory.getWorker());
    }

    public List<Integer> getChangedCategoryId() {
        return changedCategoryId;
    }

    public List<SynonymCategory> getRenewedSynonymCategoryList() {
        return renewedSynonymCategoryList;
    }
}
