package com.example.studentapi.service;

import com.example.studentapi.entity.Student;
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

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student("FirstName", "LastName", "testStudent@example.com", 20);
    }

    @Test
    void shouldCallFindAllStudents() {
        // Arrange
        Student student2 = new Student();
        student2.setEmail("test2@example.com");
        List<Student> allStudents = new ArrayList<>();
        allStudents.add(testStudent);
        allStudents.add(student2);

        when(studentRepository.findAll()).thenReturn(allStudents);

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertNotNull(result);
        verify(studentRepository).findAll();
    }

    @Test
    void givenId_shouldCallFindByIdAndReturnOneStudent() {
        // Arrange
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));

        // Act
        Student studentResult = studentService.getStudent(id);

        // Assert
        assertNotNull(studentResult);
        assertEquals(testStudent.getFirstName(), studentResult.getFirstName());
        verify(studentRepository).findById(id);
    }

    @Test
    void shouldThrow_whenStudentNotFound() {
        // Arrange
        Long id= 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(RuntimeException.class, () -> studentService.getStudent(id));
        verify(studentRepository).findById(id);
    }

    @Test
    void givenNoStudents_whenGetAllStudents_shouldReturnEmptyList() {
        // Arrange
        List<Student> allStudentsEmpty = new ArrayList<>();
        when(studentRepository.findAll()).thenReturn(allStudentsEmpty);

        // Act
        List<Student> studentsResult = studentService.getAllStudents();

        // Assert
        assertTrue(studentsResult.isEmpty());
        verify(studentRepository).findAll();
    }

    @Test
    void givenStudent_shouldCallSaveToCreateNewStudent() {
        // Arrange
        when(studentRepository.save(any())).thenReturn(testStudent);

        // Act
        Student result = studentService.createStudent(testStudent);

        // Assert
        assertNotNull(result);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void givenExistingStudent_shouldThrowWhenEmailAlreadyExists() {
        // Arrange
        when(studentRepository.findByEmail(testStudent.getEmail())).thenReturn(Optional.of(testStudent));

        // Act / Assert
        assertThrows(Exception.class,
                () -> studentService.createStudent(testStudent));
    }

    @Test
    void updateStudent() {
        // Arrange
        Student updatedStudent = new Student(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getEmail(), 25);
        Long id = 1L;

        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(updatedStudent);

        // Act
        Student updatedAndSavedStudent = studentService.updateStudent(1L, updatedStudent);

        // Assert
        assertNotNull(updatedAndSavedStudent);
        assertEquals(25, updatedAndSavedStudent.getAge());
        verify(studentRepository).findById(id);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void deleteStudent() {
        // Arrange
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));
        doNothing().when(studentRepository).deleteById(id);

        // Act
        studentService.deleteStudent(id);

        // Assert
        verify(studentRepository).findById(id);
        verify(studentRepository).deleteById(id);
    }

}