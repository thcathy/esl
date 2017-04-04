/*
package com.esl.batch;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.entity.rest.DictionaryResult;
import com.esl.model.PhoneticQuestion;
import com.esl.service.practice.PhoneticQuestionService;
import com.esl.service.rest.WebParserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
//@EnableBatchProcessing
@ImportResource("classpath:com/esl/ESL-context.xml")
@EntityScan(basePackages = {"com.esl.model", "com.esl.entity"})
public class SpringBatchApplication {

    @Autowired IPhoneticQuestionDAO phoneticQuestionDao;
    @Autowired WebParserRestService webService;
    @Autowired PhoneticQuestionService phoneticQuestionService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void run(String... strings) throws Exception {
        jdbcTemplate.queryForList(
                "select q.word from phonetic_question q\n" +
                        "WHERE NOT EXISTS (SELECT 1\n" +
                        "                  FROM vocab_image i\n" +
                        "                  WHERE q.WORD = i.WORD)", String.class)
                .stream()
                .map(word -> new PhoneticQuestion(word, null))
                .map(Arrays::asList)
                .forEach(phoneticQuestionService::enrichIfNeeded);
    }

    */
/*@Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        HibernateJpaSessionFactoryBean sessionFactory = new HibernateJpaSessionFactoryBean();
        return sessionFactory;
    }

    @Bean
    public FlatFileItemReader<PhoneticQuestion> reader() {
        FlatFileItemReader<PhoneticQuestion> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("csv/full.csv"));
        reader.setLineMapper(new DefaultLineMapper<PhoneticQuestion>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(fieldSet -> {
                PhoneticQuestion q = new PhoneticQuestion();
                q.setWord(fieldSet.readString(0));
                q.setFrequency(fieldSet.readDouble(4));
                q.setRank(fieldSet.readInt(7));
                return q;
            });
        }});
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public ItemReader<PhoneticQuestion> notEnrichedQuestionReader() {
        return new ItemReader<PhoneticQuestion>() {
            List<PhoneticQuestion> items = phoneticQuestionDao.getNotEnrichedQuestions();

            public PhoneticQuestion read() throws Exception {
                if (!items.isEmpty()) {
                    return items.remove(0);
                }
                return null;
            }
        };
    }

    @Bean
    public ItemReader<PhoneticQuestion> readPhoneticQuestion() {
        return new ItemReader<PhoneticQuestion>() {
            List<PhoneticQuestion> items = phoneticQuestionDao.getAll();

            @Override
            public PhoneticQuestion read() throws Exception {
                if (!items.isEmpty())
                    return items.remove(0);
                return null;
            }
        };
    }*//*



    */
/*public PhoneticQuestion storePhoneticQuestion(PhoneticQuestion question) {
        PhoneticQuestion questionInDB = phoneticQuestionDao.getPhoneticQuestionByWord(question.getWord());
        if (questionInDB != null) {
            questionInDB.setRank(question.getRank());
            questionInDB.setFrequency(question.getFrequency());
            phoneticQuestionDao.persist(questionInDB);
            return questionInDB;
        } else {
            phoneticQuestionDao.persist(question);
            return question;
        }
    }

    public PhoneticQuestion updateExisting(PhoneticQuestion question) {
        PhoneticQuestion questionInDB = phoneticQuestionDao.getPhoneticQuestionByWord(question.getWord());
        if (questionInDB != null) {
            questionInDB.setRank(question.getRank());
            questionInDB.setFrequency(question.getFrequency());
            phoneticQuestionDao.persist(questionInDB);
            return questionInDB;
        }
        return null;
    }

    public PhoneticQuestion enrichImages(PhoneticQuestion question) {
        phoneticQuestionService.enrichIfNeeded(Arrays.asList(question));
        return null;
    }*//*

*/
/*
    @Bean
    protected Step step1() throws Exception {
        return this.steps.get("step1")
                .<PhoneticQuestion, PhoneticQuestion> chunk(10)
                .reader(reader())
                .processor(this::updateExisting)
                .build();
    }

    @Bean
    protected Step enrichIPAAndPronounce() throws Exception {
        return this.steps.get("enrichIPAAndPronounce")
                .<PhoneticQuestion, PhoneticQuestion> chunk(10)
                .reader(notEnrichedQuestionReader())
                .processor(this::updateIPAAndPronounce)
                .build();
    }

    @Bean
    protected Step enrichImages() throws Exception {
        return this.steps.get("enrichImages")
                .<PhoneticQuestion, PhoneticQuestion> chunk(10)
                .reader(readPhoneticQuestion())
                .processor(this::enrichImages)
                .build();
    }*//*


    private PhoneticQuestion updateIPAAndPronounce(PhoneticQuestion question) {
        CompletableFuture<Optional<DictionaryResult>> dictionaryResult = webService.queryDictionary(question.getWord());
        Optional<DictionaryResult> r = dictionaryResult.join();
        if (!r.isPresent()) {
            question.setIPAUnavailable(true);
        } else {
            DictionaryResult result = r.get();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(result.IPA))
                question.setIPA(result.IPA);
            else
                question.setIPAUnavailable(true);
            question.setPronouncedLink(result.pronunciationUrl);
        }
        phoneticQuestionDao.persist(question);
        return question;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBatchApplication.class).web(false).run(args);
        System.exit(0);
    }
}
*/
