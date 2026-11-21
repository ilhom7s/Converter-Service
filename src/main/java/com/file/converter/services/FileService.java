package com.file.converter.services;
import com.file.converter.constants.Constants;
import com.file.converter.entity.OutBoxMessage;
import com.file.converter.exceptions.FailedException;
import com.file.converter.repository.OutBoxMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@AllArgsConstructor
@Slf4j
public class FileService {
    private final MinioService minioService;
    private final List<Converter> converters;
    private final OutBoxMessageRepository outBoxMessageRepository;
    private final Tika tika;

    private String detect(byte[] content,String fileName){
        return tika.detect(content, fileName);
    }

    public void convertFile(String filePath){
        log.debug("Передается файл {} на ковертацию",filePath);
        byte[] content = minioService.downloadFile(filePath);
        process(filePath,content,0);
    }

    private void process(String fileName,byte[] content,int depth){
        String type = detect(content,fileName);

            if (Constants.ZIP_TYPE.equals(type)) {
                processZip(fileName, content, depth);
                return;
            }
            Converter converter = findConverter(type,fileName);
            byte[] convertedFile = converter.convert(content);
            String newFileName = makeName(fileName);

            minioService.uploadFile(newFileName, convertedFile);
            try {
                OutBoxMessage outBoxMessage = new OutBoxMessage();
                outBoxMessage.setPublishAt(Instant.now());
                outBoxMessage.setPlayLoad(newFileName);
                outBoxMessageRepository.save(outBoxMessage);
            } catch (RuntimeException exception) {
                minioService.removeFile(newFileName);
                throw new FailedException("Не удалось сохранить outbox для файла: " + newFileName, exception);
            }
    }

    private Converter findConverter(String type,String fileName){
        return converters.stream()
                .filter(s->s.supportsFile(type))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Конвертация не возможна для этого файла: " + fileName));
    }

    private String makeName(String name){
        int slash = name.lastIndexOf(Constants.SLASH);
        int dot = name.lastIndexOf(Constants.DOT);
        String base = dot > slash ? name.substring(0, dot) : name;
        return base+Constants.PDF_EXTENSION;
    }
    private String baseName(String name) {
        int slash = name.lastIndexOf(Constants.SLASH);
        int dot = name.lastIndexOf(Constants.DOT);
        return dot > slash ? name.substring(0, dot) : name;
    }

    private void processZip(String filePath,byte[] content,int depth){
        if (depth >= Constants.MAX_ZIP_DEPTH) {
            throw new IllegalArgumentException("Слишком глубокая вложенность архивов: " + depth);
        }
        String prefix = baseName(filePath) + Constants.STRING_SLASH;

        try(ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content))){
            ZipEntry zipEntry;
            while((zipEntry = zipInputStream.getNextEntry())!=null){
                String name = zipEntry.getName();
                if (zipEntry.isDirectory() || name.contains(Constants.TWO_DOT)||name.startsWith(Constants.STRING_SLASH)) {
                    continue;
                }
                byte[] data = zipInputStream.readAllBytes();
                try{
                    process(prefix+name,data,depth+1);
                }
                catch (IllegalArgumentException exception){
                    log.warn("Файл {} из архива пропущен {}",zipEntry.getName(),exception.getMessage());
                }
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать архив " + filePath, e);
        }
    }
}

