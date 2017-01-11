package com.esl.web.jsf.controller.practice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

import javax.faces.component.UIViewRoot
import javax.faces.context.FacesContext

//@ContextConfiguration(locations = "/com/esl/ESL-context.xml")
@SpringBootTest
class SelfDictationSpec extends Specification {
    @Autowired
    SelfDictationController selfDictationController

    @Ignore
    def "Test spring setup"() {
        when:
        FacesContext.getCurrentInstance().setViewRoot(new UIViewRoot(""))
        selfDictationController.inputVocab = ["test"]
        selfDictationController.start()

        then:
        selfDictationController.practice.answers.size()==1
    }
}