package com.example.fashionface;

/**
 * Created by QiQi on 2017/7/10.
 */

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetMyImage {

    /*
    * 从Assets中读取图片
    */
    public Bitmap getImageFromAssetsFile(Context context,String fileName)
    {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }
}




