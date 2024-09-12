package com.zerobase.moviereservation.repository.document;

import com.zerobase.moviereservation.model.document.MovieDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchMovieRepository extends ElasticsearchRepository<MovieDocument, Long> {

  @Query("{\"match_phrase_prefix\": {\"title\": \"?0\"}}")
  Page<MovieDocument> findByTitleContaining(String title, Pageable pageable);

  @Query("{\"match_phrase_prefix\": {\"genre\": \"?0\"}}")
  Page<MovieDocument> findByGenreContaining(String genre, Pageable pageable);

  Page<MovieDocument> findByRatingGreaterThanEqual(Double rating, Pageable pageable);

}
