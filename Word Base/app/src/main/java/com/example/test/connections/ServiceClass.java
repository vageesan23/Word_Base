package com.example.test.connections;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;

public class ServiceClass { // This class is to call the API methods inside the LanguageSubscription, Translate and TranslateAll

    // Initialize the Language Translating API to translate the words
    public static LanguageTranslator initLanguageTranslatorService() {
        IamAuthenticator authenticator= new IamAuthenticator("qvJxdaOX57amCPV6Vhd9DyhBTvgYEROSHGehI2ZzePa7"); // Defining personal API key
        LanguageTranslator service = new LanguageTranslator("2018-05-01",  authenticator);
        service.setServiceUrl("https://api.us-south.language-translator.watson.cloud.ibm.com"); //Defining the URL for Language translator
        return service;
    }

    // Initialize the Text to speech API to Pronounce the words
    public static TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator("E0CSKqAjBx4nPy0M70g7C8ysasLgTRx5BXtB382gdsHG"); // Defining personal API key
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl("https://api.us-south.text-to-speech.watson.cloud.ibm.com"); //Defining the URL for Text to Speech
        return service;
    }
}
