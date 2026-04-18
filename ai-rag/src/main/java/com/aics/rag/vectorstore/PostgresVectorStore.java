package com.aics.rag.vectorstore;

import com.aics.rag.config.RagProperties;
import com.aics.rag.domain.ScoredMatch;
import com.aics.rag.domain.VectorRecord;
import com.aics.rag.math.VectorMath;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL 存向量、应用层余弦检索（中小数据量）；大数据量请换 {@code milvus} 或 pgvector 下推检索。
 */
@Component("postgresVectorStore")
@ConditionalOnProperty(prefix = "aics.rag", name = "vector-store", havingValue = "postgres")
public class PostgresVectorStore implements VectorStore {

    private final JdbcTemplate jdbc;

    public PostgresVectorStore(RagProperties properties) {
        DataSource ds = dataSource(properties);
        this.jdbc = new JdbcTemplate(ds);
        initSchema();
    }

    private static DataSource dataSource(RagProperties p) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(p.getPostgresJdbcUrl());
        ds.setUsername(p.getPostgresUsername());
        ds.setPassword(p.getPostgresPassword());
        return ds;
    }

    private void initSchema() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS rag_vector (
                    id VARCHAR(128) PRIMARY KEY,
                    content TEXT NOT NULL,
                    dim INT NOT NULL,
                    embedding BYTEA NOT NULL
                )
                """);
    }

    @Override
    public void upsertAll(Collection<VectorRecord> records) {
        for (VectorRecord r : records) {
            byte[] blob = floatsToBytes(r.embedding());
            jdbc.update("DELETE FROM rag_vector WHERE id = ?", r.id());
            jdbc.update(
                    "INSERT INTO rag_vector (id, content, dim, embedding) VALUES (?,?,?,?)",
                    r.id(),
                    r.text(),
                    r.embedding().length,
                    blob);
        }
    }

    @Override
    public List<ScoredMatch> similaritySearch(float[] queryEmbedding, int topK, double minScore) {
        List<Row> loaded = jdbc.query(
                "SELECT id, content, embedding FROM rag_vector",
                (rs, rowNum) -> {
                    String id = rs.getString("id");
                    String text = rs.getString("content");
                    byte[] blob = rs.getBytes("embedding");
                    float[] vec = bytesToFloats(blob);
                    return new Row(id, text, vec, Map.of());
                });
        List<ScoredMatch> hits = new ArrayList<>();
        for (Row row : loaded) {
            double score = VectorMath.cosineSimilarity(queryEmbedding, row.embedding);
            if (score >= minScore) {
                hits.add(new ScoredMatch(row.id, row.text, score, row.metadata));
            }
        }
        hits.sort(Comparator.comparingDouble(ScoredMatch::score).reversed());
        if (hits.size() > topK) {
            return List.copyOf(hits.subList(0, topK));
        }
        return hits;
    }

    @Override
    public void removeAll() {
        jdbc.execute("DELETE FROM rag_vector");
    }

    private record Row(String id, String text, float[] embedding, Map<String, String> metadata) {
    }

    private static byte[] floatsToBytes(float[] f) {
        ByteBuffer bb = ByteBuffer.allocate(4 * f.length).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : f) {
            bb.putFloat(v);
        }
        return bb.array();
    }

    private static float[] bytesToFloats(byte[] raw) {
        ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        float[] out = new float[raw.length / 4];
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getFloat();
        }
        return out;
    }
}
