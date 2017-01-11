package com.esl.test.dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

import com.esl.dao.dictation.IDictationDAO;
import com.esl.entity.dictation.Dictation;
import com.esl.model.Member;

@ContextConfiguration(locations={"classpath:test-dao.xml"})
@Transactional
@SpringBootTest
public class DictationDaoTest {
	Logger log = LoggerFactory.getLogger(DictationDaoTest.class);
	Dictation dic1, dic2;
	Member member1;

	@Autowired
	public IDictationDAO dictationDao;
	
	public DictationDaoTest() {
		member1 = new Member();
		
		dic1 = new Dictation("dic1");
		dic1.setCreator(member1);
		
		dic2 = new Dictation("dic2");
		dic2.setTotalRecommended(3);
		dic2.setCreator(member1);
	}

	@Before
	public void before() {
		dictationDao.persist(member1);
		dictationDao.persist(dic1);
		dictationDao.persist(dic2);
	}

	@After
	public void after() {
		dictationDao.delete(dic1);
		dictationDao.delete(dic2);
	}

	@Test
	public void testGetAll() {
		log.info("<<<<<<<<<<<<<<testGetAll>>>>>>>>>>>>>");
		List<Dictation> result = dictationDao.getAll();

		assertEquals(2, result.size());
		assertTrue(result.contains(dic1));
		assertTrue(result.contains(dic2));
	}

	@Test
	@Rollback(true)
	public void testPersistOne() {
		log.info("<<<<<<<<<<<<<<testPersistOne>>>>>>>>>>>>>");
		Dictation d1 = new Dictation("d1");
		dictationDao.persist(d1);
		List<Dictation> result = dictationDao.getAll();
		assertEquals(3, result.size());
		assertTrue(result.contains(d1));
	}

	@Test
	public void testRecommended() {
		log.info("<<<<<<<<<<<<<<testRecommended>>>>>>>>>>>>>");

		List<Dictation> result = dictationDao.getAll();
		Dictation d1 = result.get(0);

		assertEquals(0, d1.getTotalRecommended());
		d1.setTotalRecommended(d1.getTotalRecommended() + 1);
		dictationDao.persist(d1);
		dictationDao.flush();

		Dictation newD1 = dictationDao.get(d1.getId());
		assertEquals(1, newD1.getTotalRecommended());
		
		newD1.setTotalRecommended(0);
		dictationDao.persist(newD1);
	}
	
	@Test	
	public void testListMostRecommendedWithMinValueAndMaxResult() {
		log.info("<<<<<<<<<<<<<<testListMostRecommended>>>>>>>>>>>>>");
		
		List<Dictation> result = dictationDao.listMostRecommended(1, 1);
		assertEquals("Only 1 dictation reach min value", 1, result.size());
		
		result = dictationDao.listMostRecommended(0, 5);
		assertEquals("All dictation retreat", 2, result.size());
		assertEquals("First dictation should be the most recommended", dic2, result.get(0));
	}
	
	@Test
	public void testGetMostRecommendedByMember() {
		log.info("<<<<<<<<<<<<<<testGetMostRecommendedByMember>>>>>>>>>>>>>");
		
		Dictation result = dictationDao.getMostRecommendedByMember(member1);
		assertEquals("Dic2 should return as most recommended", dic2, result);
	}

}
