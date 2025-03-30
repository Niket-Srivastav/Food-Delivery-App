package in.NiketProject.foodiesapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.NiketProject.foodiesapi.entity.FoodEntity;

@Repository
public interface FoodRepository extends MongoRepository<FoodEntity, String> {
    // Custom query methods can be defined here if needed
    // For example, findByCategory(String category) to find food items by category
}
