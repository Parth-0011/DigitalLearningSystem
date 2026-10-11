package com.DLS.DigitalLearningSystem.controller;

import com.DLS.DigitalLearningSystem.entity.Course;
import com.DLS.DigitalLearningSystem.entity.User;
import com.DLS.DigitalLearningSystem.repository.CourseModuleRepository;
import com.DLS.DigitalLearningSystem.repository.CourseRepository;
import com.DLS.DigitalLearningSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id: " + id));
        model.addAttribute("course", course);
        model.addAttribute("modules", courseModuleRepository.findByCourseIdOrderBySequenceOrderAsc(id));
        return "courses/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Course course = new Course();
        course.setInstructor(new User()); // Prevents NullPointerException in Thymeleaf!
        model.addAttribute("course", course);
        model.addAttribute("instructors", userRepository.findAll());
        return "courses/create";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute Course course) {
        // Fetch the fully managed User from the database using the selected ID
        if (course.getInstructor() != null && course.getInstructor().getId() != null) {
            User managedInstructor = userRepository.findById(course.getInstructor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid instructor ID"));
            course.setInstructor(managedInstructor);
        }

        courseRepository.save(course);
        return "redirect:/courses";
    }
}