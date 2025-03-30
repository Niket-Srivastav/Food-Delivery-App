package in.NiketProject.foodiesapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.NiketProject.foodiesapi.io.FoodRequest;
import in.NiketProject.foodiesapi.io.FoodResponse;
import in.NiketProject.foodiesapi.service.FoodService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public FoodResponse addFood(@RequestPart("food") String foodString,
                                @RequestPart("file") MultipartFile file) {
        // Constructor logic if needed
        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest foodRequest = null;
        try {
            foodRequest = objectMapper.readValue(foodString, FoodRequest.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid food data", e);
        }
        
        FoodResponse foodResponse = foodService.addFood(foodRequest, file);
        return foodResponse;
    }

    @GetMapping
    public List<FoodResponse> readFoods() {
        return foodService.readFoods();
    }

    @GetMapping("/{foodId}")
    public FoodResponse getFoodById(@PathVariable String foodId) {
        return foodService.getFoodById(foodId);
    }
}
