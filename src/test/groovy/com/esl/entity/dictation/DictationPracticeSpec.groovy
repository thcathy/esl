package com.esl

import com.esl.entity.dictation.Dictation
import com.esl.entity.dictation.DictationPractice
import spock.lang.Specification

class DictationPracticeSpec extends Specification {

    def "Separate article by newline"() {
        def article = '''
            Lorem ipsum dolor sit amet,
            consectetur adipiscing elit,
            sed do eiusmod tempor incididunt
            ut labore et dolore magna aliqua.
            '''

        when: "create a dictation practice"
        Dictation dictation = new Dictation()
        dictation.article = article
        DictationPractice dicPractice = new DictationPractice(dictation)

        then: "the sentences are separated by newline"
        dicPractice.sentences.size() == 4
        dicPractice.sentences[0] == "Lorem ipsum dolor sit amet,"
        dicPractice.sentences[1] == "consectetur adipiscing elit,"
        dicPractice.sentences[2] == "sed do eiusmod tempor incididunt"
        dicPractice.sentences[3] == "ut labore et dolore magna aliqua."
    }

    def "Separate article with long sentences"() {
        def article = '''Every animal on earth must eat something in order to stay alive. Many animals eat some parts of plants. Giraffes munch leaves from the tall trees on the African plain. Koala bears in Australia only eat eucalyptus leaves in the forest. Even the mighty buffalo in America only eat grass.
                            Some animals are predators, which means they eat some other creature. Lots of predators will catch and eat small animals like mice and rabbits. Larger fish eat smaller fish. Big predators in Africa like lions and leopards will attack all kinds of big animals, even elephants. Animals that predators eat are called prey.
                            With predators all around them, animals need some way to protect themselves. One form of protection is camouflage. This means that the animal’s outside appearance allows it to blend into its surroundings so it is harder for a predator to find it.
                            Some predators use camouflage too.
                            Predators need camouflage so that prey animals cannot see the predator nearby.'''

        when: "create a dictation practice"
        Dictation dictation = new Dictation()
        dictation.article = article
        DictationPractice dicPractice = new DictationPractice(dictation)

        then: "long sentences are separated"
        dicPractice.sentences.size() == 15
        dicPractice.sentences[0] == "Every animal on earth must eat something in order to stay alive."
        dicPractice.sentences[1] == "Many animals eat some parts of plants."
        dicPractice.sentences[13] == "Some predators use camouflage too."
        dicPractice.sentences[14] == "Predators need camouflage so that prey animals cannot see the predator nearby."

    }

    def "Separate article with number by newline"() {
        def article = '''
            1.  I hate to complain, but this hamburger tastes bad.
            
            2.  Were you following the directions when you made it?
            
            3.  Because you had a coupon for free snails doesn't mean you should put the creatures in my food!
            
            '''

        when: "create a dictation practice"
        Dictation dictation = new Dictation()
        dictation.article = article
        DictationPractice dicPractice = new DictationPractice(dictation)

        then: "the sentences are separated by newline"
        dicPractice.sentences.size() == 3
        dicPractice.sentences[0] == "1.  I hate to complain, but this hamburger tastes bad."
        dicPractice.sentences[1] == "2.  Were you following the directions when you made it?"
        dicPractice.sentences[2] == "3.  Because you had a coupon for free snails doesn't mean you should put the creatures in my food!"

    }


}
