package ai.pipestream.module.chunker;

import ai.pipestream.data.v1.PipeDoc;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InspectParsedData {
    private static final Logger log = LoggerFactory.getLogger(InspectParsedData.class);

    @Test
    public void inspectParsedDocStructure() throws IOException {
        // Load first few parsed documents to see structure
        for (int i = 1; i <= 3; i++) {
            String resourceName = String.format("/parser_pipedoc_parsed/parsed_document_%03d.pb", i);
            try (var inputStream = getClass().getResourceAsStream(resourceName)) {
                if (inputStream != null) {
                    byte[] data = inputStream.readAllBytes();
                    PipeDoc doc = PipeDoc.parseFrom(data);
                    
                    log.info("=== PARSED DOCUMENT {} ===", i);
                    log.info("Document ID: {}", doc.getDocId());
                    
                    if (doc.hasSearchMetadata()) {
                        var searchMetadata = doc.getSearchMetadata();
                        log.info("Has Title: {} - Value: {}", 
                            searchMetadata.hasTitle(), 
                            searchMetadata.hasTitle() ? searchMetadata.getTitle().substring(0, Math.min(50, searchMetadata.getTitle().length())) : "NONE");
                        log.info("Has Body: {} - Length: {}", 
                            searchMetadata.hasBody(), 
                            searchMetadata.hasBody() ? searchMetadata.getBody().length() : 0);
                        if (searchMetadata.hasBody() && searchMetadata.getBody().length() > 0) {
                            log.info("Body preview: {}", searchMetadata.getBody().substring(0, Math.min(100, searchMetadata.getBody().length())));
                        }
                        log.info("Semantic Results: {}", searchMetadata.getSemanticResultsCount());
                    } else {
                        log.warn("No search metadata found");
                    }
                    
                    // Check other fields
                    log.info("Has StructuredData: {}", doc.hasStructuredData());
                    
                    log.info("=== END DOCUMENT {} ===\n", i);
                } else {
                    log.warn("Resource not found: {}", resourceName);
                    break;
                }
            } catch (Exception e) {
                log.error("Failed to load document {}: {}", i, e.getMessage());
            }
        }
    }
}