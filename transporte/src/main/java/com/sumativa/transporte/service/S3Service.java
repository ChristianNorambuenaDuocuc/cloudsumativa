package com.sumativa.transporte.service;

import java.io.File;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String subirArchivo(File archivo, String rutaS3) {
    PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(rutaS3)
            .contentType("application/pdf")
            .build();

    s3Client.putObject(request, RequestBody.fromFile(archivo));

    return "s3://" + bucketName + "/" + rutaS3;
}

public byte[] descargarArchivo(String rutaS3) {

    String key = rutaS3.replace(
            "s3://" + bucketName + "/", "");

    GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

    return s3Client.getObjectAsBytes(request)
            .asByteArray();
}

}