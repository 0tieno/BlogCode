package com.learn.schoolsystem.student;

import com.learn.schoolsystem.classroom.Classroom;
import com.learn.schoolsystem.classroom.ClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;

    public StudentServiceImpl(StudentRepository studentRepository, ClassroomRepository classroomRepository) {
        this.studentRepository = studentRepository;
        this.classroomRepository = classroomRepository;
    }


    @Override
    public StudentResponse createStudent(CreateStudentRequest createStudentRequest) {

        Student student = new Student();

        student.setAdmissionNumber(createStudentRequest.getAdmissionNumber());
        student.setFirstName(createStudentRequest.getFirstName());
        student.setLastName(createStudentRequest.getLastName());
        student.setEmail(createStudentRequest.getEmail());
        student.setPhoneNumber(createStudentRequest.getPhoneNumber());
        student.setDateOfBirth(createStudentRequest.getDateOfBirth());
        student.setGender(createStudentRequest.getGender());

        if(createStudentRequest.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(createStudentRequest.getClassroomId()).orElse(null);
            student.setClassroom(classroom);
        }

        Student savedStudent = studentRepository.save(student);

        return mapToResponse(savedStudent);
    }



    @Override
    public StudentResponse getStudent(Long id) {

        Student student = studentRepository.findById(id).orElse(null);

        if(student == null){
            return null;
        }
        return mapToResponse(student);
    }

    @Override
    public List<StudentResponse> getStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StudentResponse> searchStudents(String name) {
        return studentRepository.findByFirstNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public StudentResponse updateStudent(Long id, CreateStudentRequest createStudentRequest) {

        Student student = studentRepository.findById(id).orElse(null);

        if(student == null){
            return null;
        }

        student.setAdmissionNumber(createStudentRequest.getAdmissionNumber());
        student.setFirstName(createStudentRequest.getFirstName());
        student.setLastName(createStudentRequest.getLastName());
        student.setEmail(createStudentRequest.getEmail());
        student.setPhoneNumber(createStudentRequest.getPhoneNumber());
        student.setDateOfBirth(createStudentRequest.getDateOfBirth());
        student.setGender(createStudentRequest.getGender());

        if(createStudentRequest.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(createStudentRequest.getClassroomId()).orElse(null);
            student.setClassroom(classroom);
        }

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }


    private StudentResponse mapToResponse(Student student) {
        StudentResponse studentResponse = new StudentResponse();

        studentResponse.setId(student.getId());
        studentResponse.setAdmissionNumber(student.getAdmissionNumber());
        studentResponse.setFirstName(student.getFirstName());
        studentResponse.setLastName(student.getLastName());
        studentResponse.setEmail(student.getEmail());
        studentResponse.setPhoneNumber(student.getPhoneNumber());
        studentResponse.setDateOfBirth(student.getDateOfBirth());
        studentResponse.setGender(student.getGender());

        if (student.getClassroom() != null) {

            studentResponse.setClassroomId(
                    student.getClassroom().getId()
            );

            studentResponse.setClassroomName(
                    student.getClassroom().getName()
            );
        }
        return studentResponse;

    }
}
