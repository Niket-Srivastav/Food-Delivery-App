package in.NiketProject.foodiesapi.config;

// Importing necessary Spring and AWS SDK classes
import org.springframework.beans.factory.annotation.Value; // Used to inject values from application.properties or environment variables
import org.springframework.context.annotation.Bean; // Marks a method as a Spring-managed bean
import org.springframework.context.annotation.Configuration; // Marks this class as a configuration class

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials; // Represents AWS credentials (access key and secret key)
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider; // Provides static AWS credentials
import software.amazon.awssdk.regions.Region; // Represents AWS regions
import software.amazon.awssdk.services.s3.S3Client; // AWS SDK client for interacting with S3

/**
 * Configuration class for AWS services.
 * This class is responsible for setting up and providing AWS service clients (e.g., S3Client).
 */
@Configuration
public class AWSConfig {

    // Injecting the AWS access key from application.properties or environment variables
    @Value("${aws.access.key}")
    private String accessKey;

    // Injecting the AWS secret key from application.properties or environment variables
    @Value("${aws.secret.key}")
    private String secretKey;

    // Injecting the AWS region from application.properties or environment variables
    @Value("${aws.region}")
    private String region;

    /**
     * Configures and provides an S3Client bean.
     * The S3Client is used to interact with Amazon S3 for operations like uploading, downloading, and managing files.
     *
     * @return an instance of S3Client configured with the provided AWS credentials and region.
     */
    @Bean
    public S3Client s3Client() {
        if (accessKey == null || accessKey.isEmpty() ||
            secretKey == null || secretKey.isEmpty() ||
            region == null || region.isEmpty()) {
            throw new IllegalArgumentException("AWS credentials or region are not properly configured");
        }

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
