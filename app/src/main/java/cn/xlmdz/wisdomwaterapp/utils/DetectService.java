package cn.xlmdz.wisdomwaterapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.kriston.ai.Asr_Info;
import com.example.kriston.ai.Feature_Extract_Info;
import com.example.kriston.ai.Identify_Info;
import com.example.kriston.ai.Kvp;
import com.example.kriston.ai.Load_Feature_Info;
import com.example.kriston.ai.Verify_Info;
import com.kriston.ai.Recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @file                DetectService.java
 * @brief               DetectService类是在kvp类接口的基础上封装的高级接口，各个接口功能与kvp类接口一致
 * @details             DetectService接口文件
 * @author              yely@kuaishang.cn
 * @date                2018.12.12
 * @par Copyright (C):  厦门快商通科技股份有限公司
 *
 **/
public class DetectService {
    public DetectService(Context context) {

    }
    private static final String TAG = "DetectService";

    private static final String RD_NODE_NAME = "rd_test";
    private static final String TI_NODE_NAME = "ti_test";
    private static final String TD_NODE_NAME = "td_test";

    private static Context mContext;
    private static final int topN = 1;
    private static Kvp kvp;
    private static Map<String, String> nodeMap = new HashMap<String, String>();

    //声纹库说话人id注册，用于声纹验证或者声纹检索
    public static int registUser(String speaker, String wavPath, String textType) {
        int ret = kvp.Register_Speaker_By_VoiceFile(nodeMap.get(textType), speaker, wavPath);
        if(ret !=0) {
            Toast.makeText(mContext, "声纹库注册用户失败["+ret+"]", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    //声纹验证，需要提前将说话人id注册到声纹库
    public static Verify_Info verifyUser(String speaker, String wavPath, String textType) {
        Verify_Info info = kvp.Verify_Speaker_By_VoiceFile(nodeMap.get(textType), speaker, wavPath, textType);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "声纹库验证用户失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    //声纹验证，使用特征文件和语音文件，无需注册说话人
    public static Verify_Info verifyUser2(String featurePath, String wavPath, String textType) {
        Verify_Info info = kvp.Verify_Speaker_By_FeatureFile_And_VoiceFile(featurePath, wavPath, textType);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "声纹库验证用户失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    //声纹验证，使用两条提取的特征文件，无需注册说话人
    public static Verify_Info verifyUser3(String featurePath1, String featurePath2, String textType) {
        Verify_Info info = kvp.Verify_Speaker_By_FeatureFiles(featurePath1, featurePath2, textType);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "声纹库验证用户失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    //声纹检索，需要提前将说话人id注册到声纹库
    public static Identify_Info identifyUser(String wavPath, String textType) {
        String[] strArray= new String[1];
        strArray[0] = nodeMap.get(textType);
        Identify_Info info = kvp.Identify_Speaker_By_VoiceFile(strArray, wavPath, textType, topN);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "声纹库辨认用户失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    //声纹特征文件提取保存，文件格式[spkid.fea]
    public static Feature_Extract_Info extractFeatureFile(String wavPath, String featureSavePath) {
        Feature_Extract_Info info = kvp.Extract_FeatureFile_By_VoiceFile(wavPath, featureSavePath);
        if (info.ret_code!=0) {
            Toast.makeText(mContext, "特征提取失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    //将特征文件导入声纹库，可同时导入多个特征文件
    public static Load_Feature_Info loadSpeakerByFeatureFile(String[] fileList, String textType) {
        Load_Feature_Info info = kvp.Load_Speaker_By_FeatureFile(nodeMap.get(textType), fileList);
        if(info.ret_codes.length != 0){
            Toast.makeText(mContext, "特征导入失败["+info.ret_codes[0]+"]", Toast.LENGTH_LONG).show();
            return info;
        }
        Toast.makeText(mContext, "注册用户成功", Toast.LENGTH_LONG).show();
        return info;
    }

    public static Asr_Info rdAsrRecognize(String path, String num) {

        Asr_Info info = kvp.Rdasr_Recognize_By_VoiceFile(path, num);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "内容识别失败["+info.ret_code+"]", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    public static boolean rdAsrStreamCreateSession(int SampleRate) {

        int ret = kvp.Rdasr_Create_Session(SampleRate);
        if(ret!=0) {
            Toast.makeText(mContext, "内容识别接口创建失败", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean rdAsrStreamPutDate(short[] wave, int length) {

        int ret = kvp.Rdasr_Put_Data(wave, length);
        if(ret!=0) {
            return false;
        }
        return true;
    }

    public static Asr_Info rdAsrStreamGetResult(String reference) {
        Asr_Info info = kvp.Rdasr_Get_Result(reference);
        if(info.ret_code !=0) {
            Toast.makeText(mContext, "内容识别结果获取失败", Toast.LENGTH_LONG).show();
        }

        if(!info.asr.equals(reference)) {
            Toast.makeText(mContext, "动态数字验证不通过", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    public static boolean rdAsrStreamFreeSession() {

        int ret = kvp.Rdasr_Free_Session();
        if(ret!=0) {
            Toast.makeText(mContext, "内容识别接口释放失败", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static int nodeInsert(String nodeName) {
        int ret = kvp.Node_Insert(nodeName);
        if (ret!=0) {
            Toast.makeText(mContext, "声纹库节点插入失败["+ret+"]", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    public static int nodeDelete(String nodeName) {
        int ret = kvp.Node_Delete(nodeName);
        if (ret!=0) {
            Toast.makeText(mContext, "声纹库节点删除失败["+ret+"]", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    public static int queryNode(String nodeName) {
        int ret = kvp.Query_Node(nodeName);
        return ret;
    }

    public static int querySpk(String spkid, String textType) {
        int ret = kvp.Query_Speaker(nodeMap.get(textType), spkid);
        return ret;
    }

    public static String[] nodeGetList(String textType) {
        String[] list = kvp.Node_Get_List(nodeMap.get(textType));
        return list;
    }

    public static int deleteUser(String speaker, String textType) {
        int ret = kvp.Speaker_Remove(nodeMap.get(textType), speaker);
        if (ret!=0) {
            Toast.makeText(mContext, "声纹库删除用户失败["+ret+"]", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    public static String getRandomNumber() {
        return kvp.Get_Random_Number();
    }

    public static int initKvp(Context context) {
        mContext =context;
        File privateStorageDir = mContext.getFilesDir();
        String libPath = privateStorageDir.getAbsolutePath();
        Log.i(TAG,"files path:" + libPath);

        String fileName = "voiceprint";
        String savePath = libPath + "/" + fileName;
        copyFilesFromAssets(mContext, "voiceprint", savePath);

        kvp = new Kvp();
        int ret = kvp.Init(savePath);
        if (ret!=0) {
            Toast.makeText(mContext, "声纹库初始化失败["+ret+"]", Toast.LENGTH_LONG).show();
            return ret;
        }

        nodeMap.put(Recorder.STRING_RD, RD_NODE_NAME);
        nodeMap.put(Recorder.STRING_TI, TI_NODE_NAME);
        nodeMap.put(Recorder.STRING_TD, TD_NODE_NAME);

        kvp.Node_Insert(RD_NODE_NAME);
        kvp.Node_Insert(TI_NODE_NAME);
        kvp.Node_Insert(TD_NODE_NAME);

        return 0;
    }

    public static void copyFilesFromAssets(Context context, String assetsPath, String savePath){
        try {

            String fileNames[] = context.getAssets().list(assetsPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(savePath);
                
                //判断assets文件夹已存在直接退出，优化初始化速度
//                if (file.exists() && file.isDirectory()) {
//                    Log.i(TAG, "assets file already exist!");
//                    return;
//                }

                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, assetsPath + "/" + fileName,
                            savePath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(assetsPath);
                FileOutputStream fos = new FileOutputStream(new File(savePath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}