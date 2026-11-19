package com.file.converter.services;

import com.file.converter.constants.Constants;
import com.file.converter.exceptions.FailedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


@Service
public class PngOrJpgConverterService implements Converter{

    @Override
    public boolean supportsFile(String type) {
        return Constants.SUPPORTED_IMAGE.contains(type);
    }

    @Override
    public byte[] convert(byte[] file) {
       try(PDDocument document = new PDDocument(); ByteArrayOutputStream memory = new ByteArrayOutputStream()){
           PDImageXObject image =  PDImageXObject.createFromByteArray(document, file,"image");
           PDPage page = new PDPage(PDRectangle.A4);
           document.addPage(page);

           float pageWidth = PDRectangle.A4.getWidth();
           float pageHeigth = PDRectangle.A4.getHeight();
           float scale = Math.min(pageWidth/ image.getWidth(),pageHeigth/ image.getHeight());
           if(scale>1f){
               scale = 1f;
           }
           float imageWidth = image.getWidth()*scale;
           float imageHeight = image.getHeight()*scale;
           float x = (pageWidth-imageWidth)/2;
           float y = (pageHeigth-imageHeight)/2;


           try (PDPageContentStream drawer = new PDPageContentStream(document, page)) {
               drawer.drawImage(image, x, y, imageWidth, imageHeight);
           }
           document.save(memory);
           return memory.toByteArray();
       }
       catch (Exception exception){
           throw new FailedException("Не удалось конвертировать изображение в PDF", exception);
       }
    }
}
