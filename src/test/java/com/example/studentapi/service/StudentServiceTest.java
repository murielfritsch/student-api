package com.example.studentapi.service;

import com.example.studentapi.dto.PaginatedResponseDto;
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
import org.springframework.data.domain.*;

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
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 1;
    private final String sortByDefault = "id";

    @BeforeEach
    void setUp() {
        String FIRST_NAME = "FirstName";
        String LAST_NAME = "LastName";
        String EMAIL = "test@example.com";
        int TEST_STUDENT_AGE = 25;
        testStudent = new Student(FIRST_NAME, LAST_NAME, EMAIL, TEST_STUDENT_AGE);
        studentRequestDto = new StudentRequestDto(
                FIRST_NAME, LAST_NAME, EMAIL, TEST_STUDENT_AGE
        );
        studentResponseDto = new StudentResponseDto(
                TEST_STUDENT_ID, FIRST_NAME, LAST_NAME, EMAIL, 20
        );
    }

    @Test
    void shouldCallFindAllStudentsAndReturnAllStudentsPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(PAGE_NUMBER,PAGE_SIZE, Sort.by(sortByDefault));
        List<Student> allStudents = List.of(testStudent);
        Page<Student> pageWithStudents = new PageImpl<>(allStudents, pageable, 1);
        when(studentRepository.findAll(pageable)).thenReturn(pageWithStudents);
        when(mapper.toResponse(testStudent)).thenReturn(studentResponseDto);

        // Act
        PaginatedResponseDto<StudentResponseDto> allStudentsPagedDtoResult = studentService.getAllStudents(PAGE_NUMBER,PAGE_SIZE, sortByDefault);

        // Assert
        assertNotNull(allStudentsPagedDtoResult);
        assertEquals(allStudentsPagedDtoResult.size(), PAGE_SIZE);
        assertEquals(allStudentsPagedDtoResult.currentPage(), PAGE_NUMBER);
        assertEquals(1, allStudentsPagedDtoResult.totalPages());
        assertEquals(1, allStudentsPagedDtoResult.totalItems());
        assertEquals("", allStudentsPagedDtoResult.previousPageUrl());
        assertEquals("", allStudentsPagedDtoResult.nextPageUrl());
        verify(studentRepository).findAll(pageable);
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
        Pageable pageable = PageRequest.of(PAGE_NUMBER,PAGE_SIZE, Sort.by(sortByDefault));
        List<Student> emptyStudentsList = List.of();
        Page<Student> pageWithStudents = new PageImpl<>(emptyStudentsList, pageable, 0);
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(pageWithStudents);

        // Act
        PaginatedResponseDto<StudentResponseDto> studentsPaginatedResponseDtoResult = studentService.getAllStudents(PAGE_NUMBER, PAGE_SIZE, sortByDefault);

        // Assert
        assertTrue(studentsPaginatedResponseDtoResult.items().isEmpty());
        assertEquals(PAGE_NUMBER, studentsPaginatedResponseDtoResult.currentPage());
        assertEquals(PAGE_SIZE, studentsPaginatedResponseDtoResult.size());
        verify(studentRepository).findAll(pageable);
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
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void givenExistingStudent_shouldThrowWhenEmailAlreadyExists() {
        // Arrange
        when(mapper.toEntity(studentRequestDto)).thenReturn(testStudent);
        when(studentRepository.findByEmail(testStudent.getEmail())).thenReturn(Optional.of(testStudent));

        // Act / Assert
        assertThrows(RuntimeException.class,
                () -> studentService.createStudent(studentRequestDto));
    }

    @Test
    void updateStudent() {
        // Arrange
        StudentRequestDto updatedStudentDto = new StudentRequestDto(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), 20);
        Student updatedEntity = new Student(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), 20);
        StudentResponseDto updatedStudentResponseDto = new StudentResponseDto(TEST_STUDENT_ID, testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), 20);

        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.of(testStudent));
        when(mapper.toEntity(updatedStudentDto)).thenReturn(updatedEntity);
        when(studentRepository.save(testStudent)).thenReturn(updatedEntity);
        when(mapper.toResponse(updatedEntity)).thenReturn(updatedStudentResponseDto);

        // Act
        StudentResponseDto updatedAndSavedStudentDto = studentService.updateStudent(TEST_STUDENT_ID, updatedStudentDto);

        // Assert
        assertNotNull(updatedAndSavedStudentDto);
        assertEquals(20, updatedAndSavedStudentDto.age());
        verify(studentRepository).findById(TEST_STUDENT_ID);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void whenIdNotFound_updateStudentShouldThrow() {
        // Arrange
        when(studentRepository.findById(TEST_STUDENT_ID)).thenReturn(Optional.empty());
        StudentRequestDto studentRequestDto = new StudentRequestDto("newFirstName", "NewLastName", "newemail@example.com", 19);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> studentService.updateStudent(TEST_STUDENT_ID, studentRequestDto));
        verify(studentRepository).findById(TEST_STUDENT_ID);
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