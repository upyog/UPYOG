package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Event;

@Repository
public interface EventRepository  extends  JpaRepository<Event, Long>{


    //  List<Event> getActiveEvenetByDate (Date startdt, Date enddt);

    //  Event getDuration (String name);

    // List<Event> getALlList();

}
