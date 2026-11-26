package com.studenthub.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity @Table(name="students") public class Student {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
@NotBlank @Size(max=100) private String name;
@NotBlank @Email @Size(max=150) private String email;
@NotBlank @Size(max=80) private String course;
@NotNull @Min(5) @Max(100) private Integer age;
@Size(max=25) private String phone;
public Long getId(){return id;} public void setId(Long v){id=v;}
public String getName(){return name;} public void setName(String v){name=v;}
public String getEmail(){return email;} public void setEmail(String v){email=v;}
public String getCourse(){return course;} public void setCourse(String v){course=v;}
public Integer getAge(){return age;} public void setAge(Integer v){age=v;}
public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
}
