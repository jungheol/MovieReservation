package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.model.dto.UpdateTheaterDto;
import com.zerobase.moviereservation.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/theater")
@RequiredArgsConstructor
public class TheaterController {

  private final TheaterService theaterService;

  @PostMapping("/register")
  public RegisterTheaterDto.Response register(@RequestBody RegisterTheaterDto.Request request) {
    return RegisterTheaterDto.Response.from(this.theaterService.registerTheater(request));
  }

  @PutMapping("/update/{theaterId}")
  public UpdateTheaterDto.Response updateTheater(
      @PathVariable("theaterId") Long theaterId,
      @RequestBody @Valid UpdateTheaterDto.Request request) {
    return UpdateTheaterDto.Response.from(this.theaterService.updateTheater(theaterId, request));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteTheater(
      @RequestParam("theaterId") Long theaterId
  ) {
    this.theaterService.deleteTheater(theaterId);
    return ResponseEntity.ok("영화관 정보가 삭제되었습니다.");
  }
}
