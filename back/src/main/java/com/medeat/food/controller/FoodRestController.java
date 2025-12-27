package com.medeat.food.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medeat.food.dto.FoodDto;
import com.medeat.food.service.FoodService;

@RestController
@RequestMapping("/api/food")

public class FoodRestController {

    @Autowired
    private FoodService foodService;

    /** ****************************************
     * 1. 음식 검색 (자동완성, 모달 검색)
     ******************************************/
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        List<FoodDto> result = foodService.search(keyword);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/recommend")
    public Map<String, Object> recommend() {
        Map<String, Object> map = new HashMap<>();

        map.put("lowCalorie", foodService.findLowCalorieFoods());
        map.put("substitutes", foodService.findSubstitutes());
        map.put("highProteinLowFat", foodService.findHighProteinLowFat());
        map.put("lowCarb", foodService.findLowCarbFoods());
        map.put("lowFat", foodService.findLowFatFoods());
        map.put("healthyAlternatives", foodService.findHealthyAlternatives());

        return map;
    }
}