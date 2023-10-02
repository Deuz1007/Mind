package com.example.mind.utilities;

import com.example.mind.models.Question;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParseXML {
    public static List<Question> parse(Question.QuestionType type, String rawXML) throws ParserConfigurationException, IOException, SAXException {
        // Create questions list
        List<Question> questions = new ArrayList<>();

        // Setup XML parsers
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(rawXML.getBytes()));

        // Get root element of the parsed XML
        Element rootElement = doc.getDocumentElement();

        // Get list of items
        NodeList items = rootElement.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            // Get the item element
            Element itemElement = (Element) items.item(i);

            // Create new question
            Question question = new Question();

            // Get question
            Element questionElement = (Element) itemElement.getElementsByTagName("question").item(0);
            question.question = questionElement.getTextContent();

            // Get answer
            Element answerElement = (Element) itemElement.getElementsByTagName("answer").item(0);
            String answerContent = answerElement.getTextContent();

            // Get questions
            switch (type) {
                case MULTIPLE_CHOICE:
                    NodeList choices = itemElement.getElementsByTagName("choice");
                    for (int j = 0; j < choices.getLength(); j++) {
                        Element choiceElement = (Element) choices.item(j);
                        question.choices.add(choiceElement.getTextContent());
                    }

                    // Convert CHOICE INDEX to number
                    int choiceIndex = Integer.parseInt(answerContent);
                    // Get the choice from choices by index and assign it as answer
                    question.answer = question.choices.get(choiceIndex);

                    break;
                case TRUE_OR_FALSE:
                    // Lowercase the answer
                    question.answer = answerContent.toLowerCase();

                    // Assign choices
                    question.choices.add("true");
                    question.choices.add("false");
                    break;
                case IDENTIFICATION:
                    // Assign the answer
                    question.answer = answerContent;
                    break;
            }

            // Add the question to the quiz
            questions.add(question);
        }

        // Return all parsed questions
        return questions;
    }
}
