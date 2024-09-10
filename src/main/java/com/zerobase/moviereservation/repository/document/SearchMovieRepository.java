package com.zerobase.moviereservation.repository.document;

import com.zerobase.moviereservation.model.document.MovieDocument;
import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchMovieRepository extends ElasticsearchRepository<MovieDocument, Long> {

  @Query("{\"match_phrase_prefix\": {\"title\": \"?0\"}}")
  List<MovieDocument> findByTitleContaining(String title);

  @Query("{\"match_phrase_prefix\": {\"genre\": \"?0\"}}")
  List<MovieDocument> findByGenreContaining(String genre);

}
