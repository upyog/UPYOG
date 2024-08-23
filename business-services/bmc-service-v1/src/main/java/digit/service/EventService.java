package digit.service;

import org.springframework.stereotype.Service;


import digit.repository.EventRepository;


@Service
public class EventService {
    
    private  final  EventRepository eventRepository;

     public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

     

}
