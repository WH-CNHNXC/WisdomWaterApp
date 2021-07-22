package com.example.kriston.ai;

public class Identify_Info {
    public class Scores{
        public float score;
        public String spkId;
    }
    public Scores[] scores; // TopN声纹检索结果
    public int ret_code; //错误码
}