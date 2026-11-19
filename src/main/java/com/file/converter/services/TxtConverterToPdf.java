package com.file.converter.services;

import com.file.converter.constants.Constants;
import com.file.converter.exceptions.FailedException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class TxtConverterToPdf implements Converter{
    @Override
    public boolean supportsFile(String type) {
        return Constants.SUPPORTED_TEXT_FILE_TYPE.equals(type);
    }

    @Override
    public byte[] convert(byte[] file){
        String text = new String(file, StandardCharsets.UTF_8);
        ByteArrayOutputStream memory = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4,50,50,50,50);
        try{
            PdfWriter.getInstance(document,memory);
            document.open();
            BaseFont baseFont = BaseFont.createFont(
                    "/fonts/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);
            for(String line:text.split("\\R",-1)){
                document.add(new Paragraph(line.isEmpty() ? " " : line,font));
            }
        }
        catch (DocumentException| IOException exception){
            throw new FailedException(String.format("Не удалось конвертировать txt в pdf"),exception);
        }
        finally {
            if(document.isOpen()){
                document.close();
            }
        }
        return memory.toByteArray();
    }
}
