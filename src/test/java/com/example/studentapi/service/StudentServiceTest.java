package com.example.studentapi.service;

import com.example.studentapi.dto.StudentRequestDto;
import com.example.studentapi.dto.StudentResponseDto;
import com.example.studentapi.entity.Student;
import com.example.studentapi.mapper.StudentMapper;
import com.example.studentapi.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private StudentRequestDto studentRequestDto;
    private StudentResponseDto studentResponseDto;
    private final Long TEST_STUDENT_ID = 1L;
    private final int TEST_STUDENT_AGE = 25;

    @BeforeEach
    void setUp() {
        testStudent = new Student("FirstName", "LastName", "test@example.com", TEST_STUDENT_AGE);
        studentRequestDto = new StudentRequestDto(
                "FirstName", "LastName", "test@example.com", TEST_STUDENT_AGE
        );

        studentResponseDto = new StudentResponseDto(
                TEST_STUDENT_ID, "FirstName", "LastName", "test@example.com", 20
        );
    }

    @Test
    void shouldCallFindAllStudents() {
        // Arrange
        List<Student> allStudents = List.of(testStudent);
        when(studentRepository.findAll()).thenReturn(allStudents);
        when(mapper.toResponse(testStudent)).thenReturn(studentResponseDto);

        // Act
        List<StudentResponseDto> allStudentsResult = studentService.getAllStudents();

        // Assert
        assertNotNull(allStudentsResult);
        assertEquals(1, allStudentsResult.size());
        verify(studentRepository).findAll();
        verify(mapper).toResponse(testStudent);
    }

    @Test
    void givenId_shouldCallFindByIdAndReturnOneStudent() {
        // Arrange
        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.of(testStudent));
        when(mapper.toResponse(testStudent)).thenReturn(studentResponseDto);

        // Act
        StudentResponseDto studentDtoResult = studentService.getStudent(TEST_STUDENT_ID);

        // Assert
        assertNotNull(studentDtoResult);
        assertEquals(testStudent.getFirstName(), studentDtoResult.firstName());
        verify(studentRepository).findById(TEST_STUDENT_ID);
    }

    @Test
    void shouldThrow_whenStudentNotFound() {
        // Arrange
        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(RuntimeException.class, () -> studentService.getStudent(TEST_STUDENT_ID));
        verify(studentRepository).findById(TEST_STUDENT_ID);
    }

    @Test
    void givenNoStudents_whenGetAllStudents_shouldReturnEmptyList() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<StudentResponseDto> studentsResult = studentService.getAllStudents();

        // Assert
        assertTrue(studentsResult.isEmpty());
        verify(studentRepository).findAll();
    }

    @Test
    void givenStudent_shouldCallSaveToCreateNewStudent() {
        // Arrange
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(mapper.toEntity(studentRequestDto)).thenReturn(testStudent);
        when(studentRepository.save(any())).thenReturn(testStudent);
        when(mapper.toResponse(testStudent)).thenReturn(studentResponseDto);

        // Act
        StudentResponseDto result = studentService.createStudent(studentRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(studentResponseDto.email(), result.email());
        verify(studentRepository).save(testStudent);
    }

    @Test
    void givenExistingStudent_shouldThrowWhenEmailAlreadyExists() {
        // Arrange
        when(mapper.toEntity(studentRequestDto)).thenReturn(testStudent);
        when(studentRepository.findByEmail(testStudent.getEmail())).thenReturn(Optional.of(testStudent));

        // Act / Assert
        assertThrows(Exception.class,
                () -> studentService.createStudent(studentRequestDto));
    }

    @Test
    void updateStudent() {
        // Arrange
        StudentRequestDto updatedStudentDto = new StudentRequestDto(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), 25);
        Student updatedEntity = new Student(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), testStudent.getAge());
        StudentResponseDto updatedStudentResponseDto = new StudentResponseDto(TEST_STUDENT_ID, testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), testStudent.getAge());

        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.of(testStudent));
        when(mapper.toEntity(updatedStudentDto)).thenReturn(updatedEntity);
        when(studentRepository.save(testStudent)).thenReturn(updatedEntity);
        when(mapper.toResponse(updatedEntity)).thenReturn(updatedStudentResponseDto);

        // Act
        StudentResponseDto updatedAndSavedStudentDto = studentService.updateStudent(TEST_STUDENT_ID, updatedStudentDto);

        // Assert
        assertNotNull(updatedAndSavedStudentDto);
        assertEquals(TEST_STUDENT_AGE, updatedAndSavedStudentDto.age());
        verify(studentRepository).findById(TEST_STUDENT_ID);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void shouldDeleteStudent() {
        // Arrange
        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.of(testStudent));
        doNothing().when(studentRepository).deleteById(TEST_STUDENT_ID);

        // Act
        studentService.deleteStudent(TEST_STUDENT_ID);

        // Assert
        verify(studentRepository).findById(TEST_STUDENT_ID);
        verify(studentRepository).deleteById(TEST_STUDENT_ID);
    }

}