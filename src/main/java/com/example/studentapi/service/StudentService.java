package com.example.studentapi.service;

import com.example.studentapi.dto.PaginatedResponseDto;
import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.entity.Student;
import com.example.studentapi.mapper.StudentMapper;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PaginatedResponseDto<StudentResponseDto> getAllStudents(
            int pageNumber, int pageSize, String sortBy
    ) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));

        Page<Student> allPagedStudents = repository.findAll(pageable);

        List<StudentResponseDto> studentContent = allPagedStudents.getContent().stream().map(mapper::toResponse).toList();

        return new PaginatedResponseDto<>(
                studentContent,
                allPagedStudents.getNumber(),
                allPagedStudents.getSize(),
                allPagedStudents.getTotalElements(),
                allPagedStudents.getTotalPages(),
                "",""
        );
    }

    public StudentResponseDto getStudent(Long id) {
        Student student = getStudentById(id);

        return mapper.toResponse(student);
    }

    public StudentResponseDto createStudent(StudentRequestDto studentDto) {
        repository.findByEmail(studentDto.email()).ifPresent(existingStudent -> {
            throw new RuntimeException("Email already taken");
        });
        Student newStudent = mapper.toEntity(studentDto);

        Student createdStudent = repository.save(newStudent);

        return mapper.toResponse(createdStudent);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto updatedStudentDto) {
        Student existingStudent = getStudentById(id);

        Student studentToUpdate = mapper.toEntity(updatedStudentDto);

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
