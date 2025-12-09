package com.example.studentapi.service;

import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.entity.Student;
import com.example.studentapi.mapper.StudentMapper;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<StudentResponseDto> getAllStudents() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public StudentResponseDto getStudent(Long id) {
        Student student = getStudentById(id);

        return mapper.toResponse(student);
    }

    public StudentResponseDto createStudent(StudentRequestDto studentDto) {
        Student newStudent = mapper.toEntity(studentDto);

        repository.findByEmail(newStudent.getEmail()).ifPresent(existingStudent -> {
            throw new RuntimeException("Email already taken");
        });
        Student createdStudent = repository.save(newStudent);

        return mapper.toResponse(createdStudent);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto updatedStudentDto) {
        Student studentToUpdate = mapper.toEntity(updatedStudentDto);

        Student existingStudent = getStudentById(id);

        existingStudent.setFirstName(studentToUpdate.getFirstName());
        existingStudent.setLastName(studentToUpdate.getLastName());
        existingStudent.setEmail(studentToUpdate.getEmail());
        existingStudent.setAge(studentToUpdate.getAge());

        Student updatedStudent = repository.save(existingStudent);

        return mapper.toResponse(updatedStudent);
    }

    public void deleteStudent(Long id) {
        Student existingStudent = getStudentById(id);

        repository.deleteById(id);
    }

    private Student getStudentById(Long id) {
        Student student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student Not Found"));
        return student;
    }
}
