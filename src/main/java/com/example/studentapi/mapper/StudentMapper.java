package com.example.studentapi.mapper;

import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequestDto dto) {
        return new Student(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.age()
        );
    }

    public StudentResponseDto toResponse(Student entity) {
        return new StudentResponseDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getAge()
        );
    }
}
