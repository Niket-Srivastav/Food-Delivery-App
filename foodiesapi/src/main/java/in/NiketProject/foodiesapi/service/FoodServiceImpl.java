package in.NiketProject.foodiesapi.service;

// Importing necessary classes
import java.io.IOException; // Handles input-output exceptions
import java.util.List;
import java.util.UUID; // Used to generate unique identifiers for file names

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Injects values from application.properties or environment variables
import org.springframework.http.HttpStatus; // Represents HTTP status codes
import org.springframework.stereotype.Service; // Marks this class as a Spring service
import org.springframework.web.multipart.MultipartFile; // Represents uploaded files
import org.springframework.web.server.ResponseStatusException; // Used to throw exceptions with HTTP status codes

import in.NiketProject.foodiesapi.entity.FoodEntity;
import in.NiketProject.foodiesapi.io.FoodRequest;
import in.NiketProject.foodiesapi.io.FoodResponse; // Lombok annotation to generate a constructor with all fields as parameters
import in.NiketProject.foodiesapi.repository.FoodRepository;
import software.amazon.awssdk.core.sync.RequestBody; // Represents the body of an S3 request
import software.amazon.awssdk.services.s3.S3Client; // AWS SDK client for interacting with S3
import software.amazon.awssdk.services.s3.model.PutObjectRequest; // Represents a request to upload an object to S3
import software.amazon.awssdk.services.s3.model.PutObjectResponse; // Represents the response from an S3 upload request

/**
 * Service implementation for handling food-related operations.
 * This class provides functionality to upload files to an AWS S3 bucket.
 */
@Service // Marks this class as a Spring-managed service
public class FoodServiceImpl implements FoodService {

    // AWS S3 client for interacting with the S3 service
    @Autowired
    private S3Client s3Client;
    @Autowired
    private FoodRepository foodRepository; // Repository for food-related database operations
    // Injecting the S3 bucket name from application.properties or environment variables
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    /**
     * Uploads a file to the configured AWS S3 bucket.
     *
     * @param file The file to be uploaded (received as a MultipartFile).
     * @return The public URL of the uploaded file.
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // Extract the file extension from the original file name
        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        // Generate a unique key for the file using a UUID and the file extension
        String key = UUID.randomUUID().toString() + "." + filenameExtension;

        try {
            // Build the S3 PutObjectRequest with the bucket name, key, ACL, and content type
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // The name of the S3 bucket
                    .key(key) // The unique key for the file in the bucket
                    .acl("public-read") // Sets the file to be publicly readable
                    .contentType(file.getContentType()) // Sets the content type of the file
                    .build();

            // Upload the file to S3 using the S3 client
            PutObjectResponse putObjectResponse = s3Client.putObject(
                    putObjectRequest, // The request object
                    RequestBody.fromBytes(file.getBytes()) // The file content as bytes
            );

            // Check if the upload was successful
            if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
                // Return the public URL of the uploaded file
                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                // Throw an exception if the upload failed
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file to S3");
            }

        } catch (IOException ex) {
            // Handle any IO exceptions that occur while reading the file
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(request);
        String imageUrl = uploadFile(file); // Upload the file to S3 and get the URL
        newFoodEntity.setImageUrl(imageUrl); // Set the image URL in the FoodEntity
        newFoodEntity = foodRepository.save(newFoodEntity); // Save the FoodEntity to the database

        return convertToResponse(newFoodEntity); // Convert the saved entity to a response object
    }   

    private FoodEntity convertToEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
    }
    
    private FoodResponse convertToResponse(FoodEntity foodEntity) {
        return FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .description(foodEntity.getDescription())
                .price(foodEntity.getPrice())
                .category(foodEntity.getCategory())
                .imageUrl(foodEntity.getImageUrl())
                .build();
    }

    @Override
    public List<FoodResponse> readFoods() {
        List<FoodEntity> foodEntities = foodRepository.findAll(); // Fetch all food entities from the database
        return foodEntities.stream() // Convert the list of FoodEntity to a stream
                .map(object -> convertToResponse(object)) // Map each FoodEntity to a FoodResponse
                .toList(); // Collect the results into a list
    }
}
