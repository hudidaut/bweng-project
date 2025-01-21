package at.technikum.springrestbackend.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.messages.Bucket;
import io.minio.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set the private field 'bucketName'
        Field bucketNameField = FileService.class.getDeclaredField("bucketName");
        bucketNameField.setAccessible(true);
        bucketNameField.set(fileService, "test-bucket");
    }

    @Test
    void uploadFile_successfulUpload_returnsFilePath() throws Exception {
        // Arrange
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("test-image.png");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockFile.getSize()).thenReturn(123L);

        // Mock the Bucket
        Bucket mockBucket = mock(Bucket.class);
        when(mockBucket.name()).thenReturn("test-bucket");

        when(minioClient.listBuckets()).thenReturn(Collections.singletonList(mockBucket));

        // Do not mock `putObject` explicitly; let it execute normally unless an exception is needed.

        // Act
        String result = fileService.uploadFile(mockFile);

        // Assert
        assertThat(result).startsWith("/test-bucket/").contains("test-image.png");
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }



    @Test
    void uploadFile_invalidFileType_throwsIllegalArgumentException() {
        // Arrange
        when(mockFile.getContentType()).thenReturn("application/pdf");

        // Assert
        assertThatThrownBy(() -> fileService.uploadFile(mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only image files are allowed");

        verifyNoInteractions(minioClient);
    }

    @Test
    void getPresignedUrl_success_returnsUrl() throws Exception {
        // Arrange
        String objectName = "test-image.png";
        String presignedUrl = "http://minio.example.com/test-bucket/test-image.png";
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(presignedUrl);

        // Act
        String result = fileService.getPresignedUrl(objectName);

        // Assert
        assertThat(result).isEqualTo(presignedUrl);
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }
}
