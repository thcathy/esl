package com.esl.test.dao.repo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.esl.entity.practice.PersistentGrammarPractice;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-neo4j.xml"})
public class TestPersistentGrammarPracticeRepo {
	@Autowired Neo4jTemplate template;

	@Test @Transactional
	public void persistedMovieShouldBeRetrievableFromGraphDb() {
		Long creatorId = 1l;
		PersistentGrammarPractice p = new PersistentGrammarPractice();
		p.setCreatorId(creatorId);

		template.save(p);
		GraphRepository<PersistentGrammarPractice> persistentGrammarPracticeRepository = template.repositoryFor(PersistentGrammarPractice.class);
		PersistentGrammarPractice retrievedPractice = persistentGrammarPracticeRepository.findByPropertyValue("creatorId", creatorId);
		assertEquals("retrieved movie matches persisted one", retrievedPractice, p);
		assertEquals("retrieved movie title matches", 1l, retrievedPractice.getId().longValue());
	}
}
