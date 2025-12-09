package com.example.studentapi.mapper;

import com.example.studentapi.TestHelpers;
import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentMapperTest {

    @InjectMocks StudentMapper mapper;

    private final String FIRST_NAME = "FirstName";
    private final String LAST_NAME = "LastName";
    private final String EMAIL = "test@example.com";
    private final int TEST_STUDENT_AGE = 25;

    @Test
    void givenStudentRequestDto_shouldMaptoStudentEntity() {
        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto(
                FIRST_NAME, LAST_NAME, EMAIL, TEST_STUDENT_AGE
        );

        // Act
        Student student = mapper.toEntity(requestDto);

        // Assert
        assertEquals(requestDto.firstName(), student.getFirstName());
        assertEquals(requestDto.lastName(), student.getLastName());
        assertEquals(requestDto.email(), student.getEmail());
        assertEquals(requestDto.age(), student.getAge());
    }

    @Test
    void givenStudentEntity_shouldMaptoStudentResponseDto() {
        // Arrange
        Student student = new Student(
                FIRST_NAME, LAST_NAME, EMAIL, TEST_STUDENT_AGE
        );
        Long TEST_STUDENT_ID = 1L;
        TestHelpers.setId(student, "id", TEST_STUDENT_ID);

        // Act
        StudentResponseDto studentResponseDto = mapper.toResponse(student);

        // Assert
        assertEquals(student.getFirstName(), studentResponseDto.firstName());
        assertEquals(student.getLastName(), studentResponseDto.lastName());
        assertEquals(student.getEmail(), studentResponseDto.email());
        assertEquals(student.getAge(), studentResponseDto.age());
        assertEquals(TEST_STUDENT_ID, student.getId());
    }

}