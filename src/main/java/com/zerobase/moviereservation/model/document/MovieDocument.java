package com.zerobase.moviereservation.model.document;

import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "movie")
public class MovieDocument {

  @Id
  private Long id;

  private String title;

  private String director;

  private String genre;

  private String runningTime;

  @Field(type = FieldType.Date, format = DateFormat.basic_date)
  private LocalDate releaseDate;

  private Double rating;

}
