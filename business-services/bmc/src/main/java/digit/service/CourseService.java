package digit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Courses;
import digit.repository.CoursesRepository;
import digit.web.models.SchemeApplicationRequest;
@Service
public class CourseService {
    
    @Autowired
    private CoursesRepository coursesRepository;
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    public Courses getCourseByApplication(SchemeApplicationRequest schemeApplicationRequest) {
       Courses courses = schemeApplicationRequest.getSchemeApplications().get(0).getCourse();
        Courses course = new Courses();
        course.setAmount(courses.getAmount());
        course.setCourseName(courses.getCourseName());
        course.setCreatedBy(courses.getCreatedBy());
        course.setUrl(courses.getUrl());
        course.setTypeId(courses.getTypeId());
        course.setStartDt(courses.getStartDt());
        course.setInstituteAddress(courses.getInstituteAddress());
        course.setDescription(courses.getDescription());
        course.setDuration(courses.getDuration());
        course.setEndDt(courses.getEndDt());
        course.setId(courses.getId());
        course.setInstitute(courses.getInstitute());
        course.setImgUrl(courses.getImgUrl());
        
        logger.info("Saving UserSchemeApplication: {}", course);
        return coursesRepository.save(course);
    }


    // public List<Courses> getActiveCourseByDate(Date startdt, Date enddt) {
    //     return coursesRepository.getActiveCourseByDate(startdt, enddt);
    // }

    // public Courses getCourseAmount(String courseName) {
    //     return coursesRepository.getCourseAmount(courseName);
    // }

    // public Courses getCourseDuration(String courseName) {
    //     return coursesRepository.getCourseDuration(courseName);
    // }

    // public List<Courses> getAllCourses() {
    //     return coursesRepository.getALlList();
   // }
     


}
