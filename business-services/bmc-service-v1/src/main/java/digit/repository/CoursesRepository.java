package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Courses;
@Repository
public interface CoursesRepository  extends  JpaRepository<Courses, Long>{


    // List<Courses> getActiveCourseByDate (Date startdt, Date enddt);

    // Courses getCourseAmount (String courseName);

    // Courses getCourseDuration (String courseName);

    // List<Courses> getALlList();



}
