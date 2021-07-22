package com.example.kriston.ai;
import android.util.Log;

public class Kvp {
    static {
        try{
            System.loadLibrary("kvp-JNI");
            Log.i("匿名内部类","kvp-JNI库加载成功");

            System.loadLibrary("kvp-asv");
            Log.i("匿名内部类","kvp-asv.so库加载成功");
        }catch (Throwable ex){
            Log.e("匿名内部类","load external Native library fail.");
            ex.printStackTrace();
        }
    }

    public Kvp(){}

    public native int Init(String confPath);

    public native int Node_Insert(String nodeName);

    public native int Speaker_Remove(String nodeName, String spkName);

    public native int Node_Delete(String nodeName);

    public native int Query_Node(String nodeName);

    public native int Query_Speaker(String nodeName, String spkName);

    public native String[] Node_Get_List(String nodeName);

    //声纹特征文件提取保存，文件格式[spkid.fea]
    public native Feature_Extract_Info Extract_FeatureFile_By_VoiceFile(String wavPath, String featureSavePath);

    //将特征文件导入声纹库，可同时导入多个特征文件
    public native Load_Feature_Info Load_Speaker_By_FeatureFile(String nodeName, String[] fileList);

    //声纹库说话人id注册，用于声纹验证或者声纹检索
    public native int Register_Speaker_By_VoiceFile(String nodeName, String spkName, String wavPath);

    //声纹验证，需要提前将说话人id注册到声纹库
    public native Verify_Info Verify_Speaker_By_VoiceFile(String nodeName, String spkName, String wavPath, String text_type);

    //声纹验证，使用两条提取的特征文件，无需注册说话人
    public native Verify_Info Verify_Speaker_By_FeatureFiles(String featurePath1, String featurePath2, String text_type);

    //声纹验证，使用特征文件和语音文件，无需注册说话人
    public native Verify_Info Verify_Speaker_By_FeatureFile_And_VoiceFile(String featurePath, String wavPath, String text_type);

    //声纹检索，需要提前将说话人id注册到声纹库
    public native Identify_Info Identify_Speaker_By_VoiceFile(String[] nodeList, String wavPath, String text_type, int topN);

    //随机数字串接口
    public native Asr_Info Rdasr_Recognize_By_VoiceFile(String wavPath, String reference);

    //随机数字串实时流接口
    public native int Rdasr_Create_Session(int SampleRate);
    public native int Rdasr_Free_Session();
    public native int Rdasr_Put_Data(short[] wave, int length);
    public native Asr_Info Rdasr_Get_Result(String reference);

    public native String Get_Random_Number();

}
