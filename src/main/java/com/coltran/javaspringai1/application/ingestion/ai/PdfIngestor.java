package com.coltran.javaspringai1.application.ingestion.ai;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PdfIngestor {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfIngestor.class);
    private final VectorStore vectorStore;

    @Value("classpath:toIngestion.pdf")
    private Resource pdfResource;

    public PdfIngestor(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void load(){
        var reader = new PagePdfDocumentReader(pdfResource);
        var documents = reader.get();

        var splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);

        logger.info("PDF ingested: " + chunks.size() + " chunks saved to DB");
       
    }
}
