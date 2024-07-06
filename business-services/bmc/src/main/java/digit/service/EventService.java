package digit.service;

import org.springframework.stereotype.Service;

import digit.bmc.model.Event;
import digit.repository.EventRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class EventService {
    
    private  final  EventRepository eventRepository;

     public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event getBDivyangByApplication(SchemeApplicationRequest  request) {
    Event event = new Event();

    event.setId(request.getId());
    event.setName(request.getName());
    event.setStartDt(request.getStartDt());
    event.setEndDt(request.getEndDt());
    return  eventRepository.save(event);

     }


    //  public List<Event> getActiveEvenetByDate(Date startdt, Date enddt) {
    //     return eventRepository.getActiveEvenetByDate(startdt, enddt);
    // }

   
    // public Event getEvenetDuration(String name) {
    //     return eventRepository.getDuration(name);
    // }

    // public List<Event> getAllEvents() {
    //     return eventRepository.getALlList();
    // }
     

}
