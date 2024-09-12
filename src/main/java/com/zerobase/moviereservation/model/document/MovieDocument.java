package com.zerobase.moviereservation.model.document;

import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "movie")
public class MovieDocument {

  @Id
  private Long id;

  private String title;

  private String director;

  private String genre;

  private Integer runningMinute;

  @Field(type = FieldType.Date, format = DateFormat.basic_date)
  private LocalDate releaseDate;

  private Double rating;

}
