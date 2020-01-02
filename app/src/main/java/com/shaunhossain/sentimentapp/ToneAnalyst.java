package com.shaunhossain.sentimentapp;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import java.util.ArrayList;


public class ToneAnalyst {
    final String VERSION_DATE = "2016-05-19";
    private ToneAnalyzer service = new ToneAnalyzer(VERSION_DATE);
    private String textToAnalize;

    public ToneAnalyst()
    {
        service.setUsernameAndPassword("e76bc0e2-29bd-4ee1-a5c8-6d11b1f55d2d", "cL6qPy040wj3");
        textToAnalize = "";
    }

    public ToneAnalyst(String text)
    {
        service.setUsernameAndPassword("e76bc0e2-29bd-4ee1-a5c8-6d11b1f55d2d", "cL6qPy040wj3");
        textToAnalize = text;
    }


    public void setText(String text)
    {
        textToAnalize = text;
    }

    public String sendTone()
    {
        ToneOptions tonOptions = new ToneOptions.Builder().text(textToAnalize).build();
        ToneAnalysis tone = service.tone(tonOptions).execute();
        ArrayList<Tone> tones = getTones(tone.toString());

        return getHighestTone(tones).getTone();
    }



    public static ArrayList<Tone> getTones(String tone)
    {
        ArrayList<Tone> tones = new ArrayList<Tone>();

        ArrayList<Integer> index = new ArrayList<Integer>();
        ArrayList<Integer> index2 = new ArrayList<Integer>();
        ArrayList<Integer> index3 = new ArrayList<Integer>();
        ArrayList<Integer> index4 = new ArrayList<Integer>();

        int lookFor = tone.indexOf("score");
        while(lookFor>= 0)
        {
            index.add(lookFor);
            lookFor = tone.indexOf("score", lookFor+1);
        }

        lookFor = tone.indexOf(",", index.get(0));
        for(int i = 1; i < index.size(); i++)
        {
            index2.add(lookFor);
            lookFor = tone.indexOf(",", index.get(i)+1);
        }

        lookFor = tone.indexOf(",", index.get(index.size()-1)+1);
        index2.add(lookFor);

        //-----------------------------------------------------------------------------------------------------------

        lookFor = tone.indexOf("tone_id");
        while(lookFor >= 0)
        {
            index3.add(lookFor);
            lookFor = tone.indexOf("tone_id", lookFor+1);
        }

        lookFor = tone.indexOf(",", index3.get(0));
        for(int i = 1; i < index3.size(); i++)
        {
            index4.add(lookFor);
            lookFor = tone.indexOf(",", index3.get(i)+1);
        }

        lookFor = tone.indexOf(",", index3.get(index3.size()-1)+1);
        index4.add(lookFor);



        for(int i = 0; i < index.size(); i++)
        {
            String score = tone.substring(index.get(i), index2.get(i));
            score = score.substring(score.indexOf(":")+1);
            double scoreNum = Double.parseDouble(score);

            String toneStr = tone.substring(index3.get(i), index4.get(i));
            toneStr = toneStr.replace("\"", "");
            toneStr = toneStr.substring(toneStr.indexOf(":")+1);
            toneStr = toneStr.trim();

            tones.add(new Tone(scoreNum, toneStr));

        }

        return tones;
    }

    public static Tone getHighestTone(ArrayList<Tone> tones)
    {
        double maxScore = 0;
        int indexMax = 0;
        for(int i = 0; i < tones.size(); i++)
        {
            if(tones.get(i).getScore() > maxScore)
            {
                maxScore = tones.get(i).getScore();
                indexMax = i;

            }
        }
        return tones.get(indexMax);
    }

}
