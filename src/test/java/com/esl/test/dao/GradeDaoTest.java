package com.esl.test.dao;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IGradeDAO;
import com.esl.model.Grade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/com/esl/ESL-context.xml"})
@Transactional
public class GradeDaoTest {
	Logger log = LoggerFactory.getLogger(GradeDaoTest.class);

	@Autowired
	public IGradeDAO gradeDao;
	
	private Grade testGrade = new Grade("TEST", 100);
	
	@Before
	public void setupGrade() {
		gradeDao.persist(testGrade);
	}
	
	@After
	public void removeGrade() {
		gradeDao.delete(testGrade);
	}
	
	@Test
	public void getGradeByLevel_givenLevel_ShouldReturn() {
		Grade g = gradeDao.getGradeByLevel(testGrade.getLevel());
		assertEquals(testGrade.getLevel(), g.getLevel());
		assertEquals(testGrade.getTitle(), g.getTitle());
	}
}
