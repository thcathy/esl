package com.esl.service.practice;

import com.esl.dao.IVocabImageDAO;
import com.esl.entity.VocabImage;
import com.esl.entity.rest.WebItem;
import com.esl.model.PhoneticQuestion;
import com.esl.service.rest.WebParserRestService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhoneticQuestionService {
    private static Logger log = LoggerFactory.getLogger("ESL");

    @Resource private IVocabImageDAO vocabImageDao;
    @Resource private WebParserRestService webService;

    public void enrichIfNeeded(List<PhoneticQuestion> questions) {
        log.debug("enrich: {} questions", questions.size());

        questions.stream()
                .filter(q -> !getVocabImagesFromDB(q))
                .forEach(this::getAndStoreImagesFromWeb);
    }

    private PhoneticQuestion getAndStoreImagesFromWeb(PhoneticQuestion question) {
        log.debug("getAndStoreImagesFromWeb for {}", question.getWord());

        try {
            WebItem[] items = webService.searchGoogleImage(question.getWord() + " clipart").get().getBody();
            List<String> images = Arrays.stream(items)
                    .map(i -> i.url)
                    .filter(url -> !url.endsWith("svg"))
                    .map(this::retrieveImageToString)
                    .filter(Optional::isPresent)
                    .limit(5)
                    .map(Optional::get)
                    .map(imageStr -> persistImage(imageStr, question.getWord()))
                    .collect(Collectors.toList());

            question.setPicsFullPaths(images.toArray(new String[1]));
        } catch (Exception e) {
            log.error("Cannot get images from web for {}", question.getWord(), e);
        }
        return question;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String persistImage(String imageAsStr, String word) {
        vocabImageDao.persist(new VocabImage(word, imageAsStr));
        vocabImageDao.flush();
        return imageAsStr;
    }


    private Optional<String> retrieveImageToString(String url) {
        log.debug("retrieveImageToString from url: {}", url);
        try {
            String extension = url.substring(url.lastIndexOf('.') + 1);
            HttpResponse<InputStream> response = Unirest.get(url).asBinary();
            log.debug("Response status code: {}", response.getStatus());
            byte[] byteArray = IOUtils.toByteArray(response.getBody());
            return Optional.of("data:image/" + extension + ";base64," + StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.encodeBase64(byteArray, false)));
        } catch (Exception e) {
            log.error("Cannot retrieve image from url: {}", url, e);
            return Optional.empty();
        }

    }

    @Transactional(readOnly = true)
    private boolean getVocabImagesFromDB(PhoneticQuestion question) {
        List<VocabImage> images = vocabImageDao.listByWord(question.getWord());
        if (images.size() < 1) return false;

        String[] imagesArr = images.stream()
                                .map(VocabImage::getBase64Image)
                                .collect(Collectors.toList())
                                .toArray(new String[0]);
        log.debug("get {} images for {} ", images.size(), question.getWord());
        return true;
    }
}
