package com.file.converter.services;

import com.file.converter.constants.Constants;
import com.file.converter.exceptions.FailedException;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String minioBucket;

    public byte[] downloadFile(String fileName){
        log.debug("Загрузка файла '{}' из MinIO",fileName);
        try(InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(minioBucket).object(fileName).build())){
            byte[] data = stream.readAllBytes();
            log.info("Файл '{}' из MinIO загружен и передан на конвертацию",fileName);
            return data;
        }
        catch (ErrorResponseException exception){
            if("NoSuchKey".equals(exception.errorResponse().code())){
                throw new IllegalArgumentException(String.format("Не удалось скачать файл: %s",fileName),exception);
            }
            throw new FailedException(String.format("Ошибка при скачивании файла: %s",fileName),exception);
        }
        catch (Exception exception) {
            throw new FailedException(
                    String.format("Не удалось скачать файл %s", fileName), exception);
        }
    }
    public void uploadFile(String fileName,byte[] data){
       try(InputStream inputStream = new ByteArrayInputStream(data)){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioBucket)
                    .object(fileName)
                    .stream(inputStream, data.length, -1)
                    .contentType(Constants.PDF_CONTENT_TYPE)
                    .build());
           log.debug("Файл '{}' загружен в MinIO ({} байт)", fileName, data.length);
       }
       catch (ErrorResponseException exception) {
           String code = exception.errorResponse().code();
           throw new FailedException(String.format("MinIO ошибка '%s' во время загрузки %s",code,fileName),exception);
       } catch (Exception exception) {
           throw new FailedException(String.format("Загрузка не удалась: %s",fileName),exception);
       }
    }
    public void removeFile(String fileName){
        try{
            log.debug("Начало удаление файла '{}' из MinIO ",fileName);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioBucket)
                    .object(fileName)
                    .build());
            log.info("Файл '{}' удален из MinIO",fileName);
        }
        catch (Exception exception){
            log.warn("Произошла ошибка при удалении файла '{}'.Возможно файл отсутвует",fileName,exception);
        }
    }
}
