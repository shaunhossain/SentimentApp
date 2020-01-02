package com.shaunhossain.sentimentapp;



public class Tone {
    private double score;
    private String tone;

    public Tone()
    {
        score = 0;
        tone = "none";
    }

    public Tone(double score, String tone)
    {
        this.score = score;
        this.tone = tone;
    }

    public double getScore()
    {
        return score;
    }

    public String getTone()
    {
        return tone;
    }

    public String toString()
    {
        return "score: " + score + " tone: " + tone;
    }
}
