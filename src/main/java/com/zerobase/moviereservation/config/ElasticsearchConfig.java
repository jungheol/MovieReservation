package com.zerobase.moviereservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.zerobase.moviereservation.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.data.elasticsearch.url}")
  String url;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(url)
        .build();
  }
}
