package com.eventza.Eventza.controller;

import com.eventza.Eventza.Events.EventModel;
import com.eventza.Eventza.Events.EventService;
import com.eventza.Eventza.Exception.EventNotFoundException;
import com.eventza.Eventza.Service.ImageDecompressorService;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageRetriever {

  @Autowired
  EventService eventService;

  @Autowired
  ImageDecompressorService imageDecompressorService;

  @GetMapping(path = "/{eventName}/image")
  public String retrieveImage(@PathVariable("eventName") String eventName)
      throws IOException, EventNotFoundException, DataFormatException {
    EventModel event = eventService.getRequestedEvent(eventName);
    byte[] imageByte = imageDecompressorService.decompressImage(event.getImageByte());
    String image = Base64.getEncoder().encodeToString(imageByte);
    return image;
  }
}