
package com.eventza.Eventza.Events;

import com.eventza.Eventza.Categories.CategoryService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import com.eventza.Eventza.Exception.EventNotFoundException;
import com.eventza.Eventza.Service.FileUploadService;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.eventza.Eventza.model.NewEventRequest;
import com.eventza.Eventza.model.RequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.text.ParseException;

import com.eventza.Eventza.Service.UserService;
import com.eventza.Eventza.model.User;
import org.springframework.web.bind.annotation.*;


@RestController
public class EventController {

  @Autowired
  private EventService eventService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private UserService userService;
  @Autowired
  private FileUploadService fileUploadService;
  @Autowired
  private EventRepository eventRepository;

  //    @PostMapping("/categories/{categoryName}/events")
//    public String addNewEvent(@PathVariable String categoryName, @RequestBody EventModel event){
//        try {
//            UUID id = categoryService.getCategoryId(categoryName);
//            event.setCategory(categoryService.getRequestedCategory(id));
//            eventService.addNewEvent(event);
//            return "New event added";
//        }
//        catch (Exception e){
//            return e.getMessage();
//        }
//    }


  @RequestMapping(method = RequestMethod.GET, path = "/events/{eventId}")
  public EventModel getRequestedEvent(@PathVariable String eventId)
      throws EventNotFoundException {
    try {
      // UUID uuid = new UUID(
      //   new BigInteger(eventId.substring(0, 16), 16).longValue(),
      //    new BigInteger(eventId.substring(16), 16).longValue());
      UUID id = UUID.fromString(eventId);
      return eventService.getEventById(id);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /*
  @GetMapping("/categories/{categoryName}/events")
  public EventModel getRequestedEvent(@RequestBody Map<String,String> event_id)
      throws EventNotFoundException {
    UUID id = UUID.fromString(event_id.get("id"));
    System.out.println(id);
    return eventService.getEventById(id);
  }1
*/
  @RequestMapping(method = RequestMethod.GET, path = "/categories/{categoryName}/events")
  public List<EventModel> getAllEventsFromRequestedCategory(@PathVariable String categoryName) {
    return eventService.getAllEventsFromRequestedCategory(categoryName);
  }

  @PostMapping("/categories/{categoryName}/events/{username}")
  public ResponseEntity<String> addNewEvent(@PathVariable String categoryName,
      @RequestBody NewEventRequest event, @PathVariable String username) throws IOException {
    try {
      UUID id = categoryService.getCategoryId(categoryName);
      User user = userService.getUserByUsername(username);
      if(!user.isOrganizer()){
        return new ResponseEntity<>("You are not a verified organizer yet!", HttpStatus.EXPECTATION_FAILED);
      }
      event.setCategoryModel(categoryService.getRequestedCategory(id));

      EventModel new_event = new EventModel(event.getEventName(), user.getName(), user.getEmail(),
          event.getStartDate(), event.getStartTime(), event.getEndDate(), event.getEndTime(),
          event.getLocation(), event.getPrice(), event.getTotalTickets(),
          event.getEventDescription(), event.getCategoryModel());

      eventService.addNewEvent(new_event, user);
      return new ResponseEntity<String>("New Event added", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<String>(
          e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

 /* @RequestMapping(method = RequestMethod.PUT, path = "/categories/{categoryName}/events/{eventName}")
  public String updateExistingEvent(@PathVariable String categoryName,
      @PathVariable String eventName, @RequestBody EventModel event) {
    UUID id = categoryService.getCategoryId(categoryName);
    event.setCategory(new CategoryModel(id, categoryName));
    UUID event_id = eventService.getEventId(eventName);
    eventService.updateExistingEvent(event_id, event);
    return eventName + " updated";
  }
  */


  @PutMapping("/categories/{categoryName}/events/{eventName}")
  public String updateExistingEvent(@PathVariable String eventName, @RequestBody EventModel event) {
    eventService.updateExistingEvent(event);
    return eventName + " updated";
  }

//  @RequestMapping(method = RequestMethod.DELETE, path = "/categories/{categoryName}/events/{eventName}")
//  public String deleteEvent(@PathVariable String eventName) {
//    eventService.deleteEvent(eventName);
//    return eventName + " deleted";
//  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/events/{eventId}")
  public String delete_Event(@PathVariable String eventId) {
    UUID id;
    try {
      id = UUID.fromString(eventId);
    } catch (Exception e) {
      id = new UUID(
          new BigInteger(eventId.substring(0, 16), 16).longValue(),
          new BigInteger(eventId.substring(16), 16).longValue());
    }
    eventService.deleteEvent(id);
    return "deleted";
  }

  @RequestMapping(method = RequestMethod.POST, path = "/{eventName}/{rating}")
  public String rateAnEvent(@PathVariable String eventName, @PathVariable Integer rating,
      @RequestBody Map<String, String> username) {

    UUID id = eventService.getEventId(eventName);
    User user = userService.getUserByUsername(username.get("username"));
    Double rate = eventService.rateAnEvent(id, rating, user);
    return eventName + " rated";
  }

  @RequestMapping(method = RequestMethod.GET, path = "/trendingEvents")
  public List<String> getTrendingEvents() {
    //return eventService.getTrendingEvents();
    List<String> names = new ArrayList<>();
    eventService.getTrendingEvents().forEach(name -> names.add(
        name.getEventName()));
    return names;
  }


  @RequestMapping(method = RequestMethod.GET, path = "/search/{location}")
  public List<EventModel> searchEventsByLocation(@PathVariable String location) {
    return eventService.searchEventsByLocation(location);

  }


  @GetMapping("/categories/getAllEvents")
  public List<String> getAllEvents() {

    //return eventService.getAllEvents();
    List<String> names = new ArrayList<>();
    eventService.getAllEvents().forEach(name -> names.add(
        name.getEventName()));
    return names;
  }


  @GetMapping("/getPastEvents")
  public List<RequestEvent> getPastEvents() throws ParseException {
    return eventService.getPastEvents();
  }


  @RequestMapping(method = RequestMethod.POST, path = "/{eventName}/register")
  public ResponseEntity<?> registerUserInEvent(@PathVariable String eventName,
      @RequestBody Map<String, String> username) {
    // UUID id = UUID.fromString(eventId);
    UUID id = eventService.getEventId(eventName);
    if (eventService.getEventById(id).getRemainingTickets() == 0) {
      return new ResponseEntity<String>("No tickets available", HttpStatus.EXPECTATION_FAILED);
    }
    User user = userService.getUserByUsername(username.get("username"));
    eventService.registerUserInEvent(id, user);
    user.registerEvent(eventService.getEventById(id));
    userService.updateUser(user);
    return new ResponseEntity<String>("User registered", HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/featuredEvents")
  public List<EventModel> getFeaturedEvents() {

    return eventService.getFeaturedEvents();
   /* List<String> names = new ArrayList<>();
    eventService.getFeaturedEvents().forEach(name -> names.add(
        name.getEventName()));
    return names;*/
  }

  @RequestMapping(method = RequestMethod.GET, path = "/upcomingEvents")
  public List<String> getUpcomingEvents() {

    //return eventService.getUpcomingEvents();
    List<String> names = new ArrayList<>();
    eventService.getUpcomingEvents().forEach(name -> names.add(
        name.getEventName()));
    return names;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/ongoingEvents")
  public List<String> getOngoingEvents() {

    //return eventService.getOngoingEvents();
    List<String> names = new ArrayList<>();
    eventService.getOngoingEvents().forEach(name -> names.add(
        name.getEventName()));
    return names;
  }

  @GetMapping("/search")
  public List<EventModel> searchFor(@RequestParam String keyword) {
    return eventRepository.findAll(keyword);
  }


  @PatchMapping("/update")
  public ResponseEntity<?> updateEvent(@RequestBody Map<String,String> data){
    if(data.get("id")!=null) {
      UUID id;
      try {
        id = UUID.fromString(data.get("id"));
      } catch (IllegalArgumentException e) {
        id = new UUID(
            new BigInteger(data.get("id").substring(0, 16), 16).longValue(),
            new BigInteger(data.get("id").substring(16), 16).longValue());
      }
      if (data.get("price") != null) {
        int price = Integer.parseInt(data.get("price"));
        eventRepository.updatePrice(id, price);
      }
      if (data.get("eventLocation") != null) {
        eventRepository.updateLocation(id, data.get("eventLocation"));
      }
      if (data.get("eventName") != null) {
        eventRepository.updateName(id, data.get("eventName"));
      }
      if (data.get("totalTickets") != null) {
        eventRepository.updateTickets(id, Integer.parseInt(data.get("totalTickets")));
      }
      if (data.get("startDate") != null) {
        eventRepository.updateStartDate(id, data.get("startDate"));
      }
      if (data.get("endDate") != null) {
        eventRepository.updateEndDate(id, data.get("endDate"));
      }
      if (data.get("startTime") != null) {
        eventRepository.updateStartTime(id, data.get("startTime"));
      }
      if (data.get("endTime") != null) {
        eventRepository.updateEndTime(id, data.get("endTime"));
      }
      if (data.get("eventDescription") != null) {
        eventRepository.updateDescription(id, data.get("eventDescription"));
      }
      if (data.get("category") != null) {
        String name = data.get("category");
        EventModel eventModel = eventRepository.getEventModelById(id);
        eventModel.setCategory(categoryService.getRequestedCategory(name));
      }
      EventModel e = eventService.getEventById(id);
      RequestEvent req = new RequestEvent(e.getId(),e.getEventName(),e.getOrganiserName(),e.getEventLocation(),e.getPrice(),e.getAverageRating(),e.getRatingCounter(),e.getTotalTickets(),e.getRemainingTickets(),e.getRegistrations(),e.getStartDate(),e.getEndDate(),e.getStartTime(),e.getEndTime(),e.getEventDescription(),e.getCategory());
      return new ResponseEntity<RequestEvent>(req, HttpStatus.OK);
    }
    return new ResponseEntity<String>("Please provide the UUID of the event.",
        HttpStatus.BAD_REQUEST);
  }

}
