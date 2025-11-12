package ai.pipestream.module.chunker;

import ai.pipestream.data.v1.PipeDoc;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InspectDoubleChunkedData {
    private static final Logger log = LoggerFactory.getLogger(InspectDoubleChunkedData.class);

    @Test
    public void inspectDoubleChunkedStructure() throws IOException {
        Path docPath = Paths.get("src/test/resources/double_chunked_pipedocs/double_chunked_001.pb");
        
        if (!Files.exists(docPath)) {
            log.warn("File not found: {}", docPath.toAbsolutePath());
            return;
        }

        byte[] data = Files.readAllBytes(docPath);
        PipeDoc doc = PipeDoc.parseFrom(data);
        
        log.info("=== DOUBLE-CHUNKED DOCUMENT STRUCTURE ===");
        log.info("Document ID: {}", doc.getDocId());
        
        if (doc.hasSearchMetadata()) {
            var searchMetadata = doc.getSearchMetadata();
            log.info("Title: {}", searchMetadata.hasTitle() ? searchMetadata.getTitle() : "NO TITLE");
            log.info("Body length: {}", searchMetadata.hasBody() ? searchMetadata.getBody().length() : 0);
            log.info("Semantic Results Count: {}", searchMetadata.getSemanticResultsCount());
            
            for (int i = 0; i < searchMetadata.getSemanticResultsCount(); i++) {
                var result = searchMetadata.getSemanticResults(i);
                log.info("  Result {}: {} chunks, config_id={}, result_set_name={}", 
                    i, result.getChunksCount(), result.getChunkConfigId(), result.getResultSetName());
                
                // Show first chunk from each result
                if (result.getChunksCount() > 0) {
                    var firstChunk = result.getChunks(0);
                    log.info("    First chunk text (50 chars): {}", 
                        firstChunk.getEmbeddingInfo().getTextContent().substring(0, 
                            Math.min(50, firstChunk.getEmbeddingInfo().getTextContent().length())));
                }
            }
        } else {
            log.warn("No search metadata found");
        }
        
        log.info("=== END STRUCTURE ===");
    }
}