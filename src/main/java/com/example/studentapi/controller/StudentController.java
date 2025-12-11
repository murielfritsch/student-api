package com.example.studentapi.controller;

import com.example.studentapi.dto.PaginatedResponseDto;
import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<StudentResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "2") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(service.getAllStudents(pageNumber, pageSize, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStudent(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@RequestBody StudentRequestDto studentRequestDto) {
        StudentResponseDto createdstudent = service.createStudent(studentRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdstudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(@PathVariable Long id, @RequestBody StudentRequestDto studentRequestDto) {
        return ResponseEntity.ok(service.updateStudent(id, studentRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

}