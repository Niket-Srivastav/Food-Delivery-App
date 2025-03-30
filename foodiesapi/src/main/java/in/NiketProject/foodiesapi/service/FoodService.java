package in.NiketProject.foodiesapi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import in.NiketProject.foodiesapi.io.FoodRequest;
import in.NiketProject.foodiesapi.io.FoodResponse;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse> readFoods();
}
