package com.wzl.aicodehelper.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * 加载RAG
 */
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Bean
    public ContentRetriever contentRetriever() {
        // 1.加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
        // 2.文档切割，每个文档按照段落进行分割，最大1000个字符，每次最多重叠200个字符
        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1000, 200);
        // 3.自定义文档加载器，把文档转成向量保存到向量数据库中
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)
                // 为了提高搜索质量，为每个 TextSegment 添加文档名称
                .textSegmentTransformer(textSegment -> TextSegment.from(textSegment.metadata().getString("key_name") + "\n" + textSegment.text(), textSegment.metadata()))
                .embeddingModel(qwenEmbeddingModel) //向量模型
                .embeddingStore(embeddingStore) //向量数据库，基于内存
                .build();

        // 4.加载文档
        ingestor.ingest(documents);
        //5.自定义内容加载器
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5) //最多5条结果
                .minScore(0.75) //过滤掉分数小于0.75的结果
                .build();
        return contentRetriever;
    }

}
