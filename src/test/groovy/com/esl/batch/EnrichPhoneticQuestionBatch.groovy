package com.esl.batch

import com.esl.ESLApplication
import com.esl.dao.IPhoneticQuestionDAO
import com.esl.entity.rest.DictionaryResult
import com.esl.model.PhoneticQuestion
import com.esl.service.rest.WebParserRestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

@ContextConfiguration(classes=ESLApplication.class)
//@SpringBootTest
@Ignore
class EnrichPhoneticQuestionBatch extends Specification {
    @Autowired JdbcTemplate jdbcTemplate
    @Autowired IPhoneticQuestionDAO phoneticQuestionDao
    @Autowired WebParserRestService webService

    @Rollback(false)
    def "update the pronounced link"() {
        when:
        jdbcTemplate.queryForList(
            "select q.word from phonetic_question q WHERE PRONOUNCED_LINK is null and WORD='and'", String.class)
            .stream()
            .map({phoneticQuestionDao.getPhoneticQuestionByWord(it)})
            .forEach({updateIPAAndPronounce(it)})

        then:
        1 == 1
    }

    void updateIPAAndPronounce(PhoneticQuestion question) {
        println "update: $question.word"

        CompletableFuture<Optional<DictionaryResult>> dictionaryResult = webService.queryDictionary(question.getWord())
        Optional<DictionaryResult> r = dictionaryResult.join()
        if (r.isPresent()) {
            DictionaryResult result = r.get()
            question.setPronouncedLink(result.pronunciationUrl)
            phoneticQuestionDao.persist(question)
        }
    }

}
