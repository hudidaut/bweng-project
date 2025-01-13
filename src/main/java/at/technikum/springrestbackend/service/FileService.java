package at.technikum.springrestbackend.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            // Validate file type
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            // Ensure the bucket exists
            ensureBucketExists(bucketName);

            // Generate a unique file name
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Upload the file to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            // Return the file URL or path
            return String.format("/%s/%s", bucketName, fileName);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation exceptions to be handled in the controller
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }


    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(1, TimeUnit.HOURS) // URL valid for 1 hour
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    private void ensureBucketExists(String bucketName) throws Exception {
        List<Bucket> buckets = minioClient.listBuckets();
        boolean exists = buckets.stream().anyMatch(bucket -> bucket.name().equals(bucketName));
        if (!exists) {
            minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
