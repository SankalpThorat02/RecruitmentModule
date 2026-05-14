package com.sankalp.prototype.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

@Component("queryService")
public class QueryService {

    private final Properties sqlQueries;

    public QueryService() {
        sqlQueries = new Properties();
        try {
            ClassPathResource resource = new ClassPathResource("sql/queries.properties");
            try (InputStream inputStream = resource.getInputStream()){
                sqlQueries.load(inputStream);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQL queries from properties file", e);
        }
    }

    public String getQuery(String queryKey) {
        String query = sqlQueries.getProperty(queryKey);
        if(query == null) {
            throw new IllegalArgumentException("Query not found for key: " + queryKey);
        }

        return query.trim();
    }
}
