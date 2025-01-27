package com.mckesson.cgat.pde.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

}
