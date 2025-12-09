package com.example.studentapi.service;

import com.example.studentapi.entity.Student;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Student getStudent(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Student Not Found"));
    }

    public Student createStudent(Student student) {
        repository.findByEmail(student.getEmail()).ifPresent(existingStudent -> {
            throw new RuntimeException("Email already taken");
        });
        return repository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = getStudent(id);

        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setAge(updatedStudent.getAge());

        return repository.save(existingStudent);
    }

    public void deleteStudent(Long id) {
        Student existingStudent = getStudent(id);

        repository.deleteById(id);
    }
}
