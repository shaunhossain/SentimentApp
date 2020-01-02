package com.shaunhossain.sentimentapp;

public class SaveData {

    String saveToken;
    String Sentence;
    String Sentiment;

    public SaveData(){

    }

    public SaveData(String saveToken, String sentence, String sentiment) {
        this.saveToken = saveToken;
        Sentence = sentence;
        Sentiment = sentiment;
    }

    public String getSaveToken() {
        return saveToken;
    }

    public void setSaveToken( String saveToken) {
        this.saveToken=saveToken;
    }

    public String getSentence() {
        return Sentence;
    }

    public void setSentence( String Sentence) {
        this.Sentence=Sentence;
    }

    public String getSentiment() {
        return Sentiment;
    }
    public void setSentiment( String Sentiment) {
        this.Sentiment=Sentiment;
    }
}
