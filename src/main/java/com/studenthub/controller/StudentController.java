package com.studenthub.controller;
import com.studenthub.model.Student;
import com.studenthub.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Controller public class StudentController {
private final StudentRepository repo;
public StudentController(StudentRepository repo){this.repo=repo;}
@GetMapping("/") public String home(@RequestParam(defaultValue="") String search, Model model){
model.addAttribute("students",search.isBlank()?repo.findAllByOrderByIdDesc():repo.findByNameContainingIgnoreCaseOrCourseContainingIgnoreCaseOrderByIdDesc(search,search));
model.addAttribute("count",repo.count()); model.addAttribute("search",search); return "index"; }
@GetMapping("/students/new") public String create(Model model){model.addAttribute("student",new Student());model.addAttribute("heading","Add new student");return "form";}
@GetMapping("/students/{id}/edit") public String edit(@PathVariable Long id,Model model){model.addAttribute("student",repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)));model.addAttribute("heading","Edit student");return "form";}
@PostMapping("/students/save") public String save(@Valid @ModelAttribute("student") Student student,BindingResult errors,Model model){if(errors.hasErrors()){model.addAttribute("heading",student.getId()==null?"Add new student":"Edit student");return "form";}repo.save(student);return "redirect:/";}
@PostMapping("/students/{id}/delete") public String delete(@PathVariable Long id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);repo.deleteById(id);return "redirect:/";}
}
