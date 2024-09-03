package com.zerobase.moviereservation.controller;

import com.zerobase.moviereservation.model.dto.RegisterTheaterDto;
import com.zerobase.moviereservation.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
