package in.NiketProject.foodiesapi.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FoodResponse {
    private String id;
    private String imageUrl;
    private String name;
    private String description;
    private double price;
    private String category;    
}
