/*
package com.esl.command;

import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.entity.VocabImage;
import com.esl.service.practice.PhoneticQuestionService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import java.util.Arrays;
import java.util.List;

@ImportResource("classpath:com/esl/ESL-context.xml")
@EntityScan(basePackages = {"com.esl.model", "com.esl.entity"})
public class SaveVocabImageCommand {

    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(SaveVocabImageCommand.class).web(false).run(args);
        IPhoneticQuestionDAO phoneticQuestionDao = ctx.getBean(IPhoneticQuestionDAO.class);
        PhoneticQuestionService phoneticQuestionService = ctx.getBean(PhoneticQuestionService.class);

        List<String> urls = Arrays.asList(
                "https://rheteffects.files.wordpress.com/2013/01/broken_links.jpg",
                "https://us.123rf.com/450wm/djvstock/djvstock1612/djvstock161214368/67892202-fragile-broken-cup-symbol-icon-vector-illustration-graphic-design.jpg?ver=6",
                "https://us.123rf.com/450wm/madozi/madozi1503/madozi150300142/37727255-broken-bottle-glass-isolated-on-white-background.jpg?ver=6"
        );
        String word = "fragment";

        urls.stream().forEach(url -> {
                    phoneticQuestionService.retrieveImageToString(url)
                            .ifPresent(i -> {
                                phoneticQuestionDao.persist(new VocabImage(word, i));
                                System.out.println("Created image for " + word);
                            });
                });

        System.exit(0);
    }
}
*/
