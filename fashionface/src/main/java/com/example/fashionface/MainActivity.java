package com.example.fashionface;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;


public class MainActivity extends BaseActivity {
    private Bitmap img;
    private ImageView imageView;
    final private int PICTURE_CHOOSE = 2;
    final private int CAPTURE_CHOOSE = 1;
    private Dialog dialog;

    private String age;
    private String gender = "";
    private String range;
    private MyDialog myDialog;
    private Bitmap bm;
    private GetMyImage getMyImage=new GetMyImage();
    private ImageView rlIcon1;
    private ImageView rlIcon2;
    private ImageView rlIcon3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        //设置标题为某个layout
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_style);
        imageView=(ImageView) findViewById(R.id.imageView);

        init();
        //拍照功能
        ImageButton Btn_image=(ImageButton) findViewById(R.id.bt_capture);
        Btn_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(),
                        "tmpe.png");// localTempImgDir和localTempImageFileName是自己定义的名字
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(intent, CAPTURE_CHOOSE);
            }
        });
    }

    public void init(){
        // 定义下方的圆形按钮
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        rlIcon1 = new ImageView(this);
        rlIcon2 = new ImageView(this);
        rlIcon3 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.human));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.pic));
        // 设定标记，用于按钮监听
        rlIcon1.setTag("rlIcon1");
        rlIcon2.setTag("rlIcon2");
        rlIcon3.setTag("rlIcon3");

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 50);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        // 按钮监听
        rlIcon1.setOnClickListener(new MyListener());
        rlIcon2.setOnClickListener(new MyListener());
        rlIcon3.setOnClickListener(new MyListener());

        // 初始化界面
        Resources res=getResources();
        img=BitmapFactory.decodeResource(res, R.drawable.rq);
        imageView.setImageBitmap(img);
    }

    // 拍照后数据回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICTURE_CHOOSE) {
                if (data != null) {
                    // The Android api ~~~
                    // Log.d(TAG, "idButSelPic Photopicker: " +
                    // intent.getDataString());
                    Cursor cursor = getContentResolver().query(data.getData(),
                            null, null, null, null);
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    // Log.d(TAG, "Picture:" + fileSrc);

                    // just read size
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    img = BitmapFactory.decodeFile(fileSrc, options);
                    // scale size to read
                    options.inSampleSize = Math.max(1, (int) Math.ceil(Math
                            .max((double) options.outWidth / 1024f,
                                    (double) options.outHeight / 1024f)));
                    options.inJustDecodeBounds = false;
                    img = BitmapFactory.decodeFile(fileSrc, options);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(FileTools.getExifOrientation(fileSrc));
                    int width = img.getWidth();
                    int height = img.getHeight();
                    img = Bitmap.createBitmap(img, 0, 0, width, height, matrix,
                            true);
                    imageView.setImageBitmap(img);
                } else {

                }
            } else if (requestCode == CAPTURE_CHOOSE) {
                File f = new File(Environment.getExternalStorageDirectory(),
                        "tmpe.png");
                String fileSrc = f.getAbsolutePath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                img = BitmapFactory.decodeFile(fileSrc, options);
                // scale size to read
                options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                        (double) options.outWidth / 1024f,
                        (double) options.outHeight / 1024f)));
                options.inJustDecodeBounds = false;
                img = BitmapFactory.decodeFile(fileSrc, options);
                Matrix matrix = new Matrix();
                matrix.postRotate(FileTools.getExifOrientation(f.getPath()));
                int width = img.getWidth();
                int height = img.getHeight();
                img = Bitmap.createBitmap(img, 0, 0, width, height, matrix,
                        true);

                imageView.setImageBitmap(img);
            }
        }
    }

    //创建一个对话框
    public Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.view_progress_dialog, null);// 得到加载view
        ViewFlipper viewFlipper = (ViewFlipper) v
                .findViewById(R.id.viewFlipper);// 加载布局
        if (!viewFlipper.isFlipping()) {
            viewFlipper.startFlipping();
        }
        TextView tv = (TextView) v.findViewById(R.id.text_message);
        tv.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCanceledOnTouchOutside(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }


    // 人脸检测
    private class FaceppDetect {
        DetectCallback callback = null;

        public void setDetectCallback(DetectCallback detectCallback) {
            callback = detectCallback;
        }

        public void detect(final Bitmap image) {
            new Thread(new Runnable() {
                public void run() {
                    HttpRequests httpRequests = new HttpRequests(
                            "4480afa9b8b364e30ba03819f3e9eff5",
                            "Pz9VFT8AP3g_Pz8_dz84cRY_bz8_Pz8M", true, false);
                    Log.v("TAG1", "image size : " + img.getWidth() + " " +
                            img.getHeight());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    float scale = Math.min(
                            1,
                            Math.min(600f / img.getWidth(),
                                    600f / img.getHeight()));
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0,
                            img.getWidth(), img.getHeight(), matrix, false);
                    // Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " "
                    // + imgSmall.getHeight());

                    imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] array = stream.toByteArray();

                    try {
                        // detect
                        JSONObject result = httpRequests
                                .detectionDetect(new PostParameters()
                                        .setAttribute(
                                                "age,gender,race,smiling,glass,pose")
                                        .setImg(array));
                        // finished , then call the callback function
                        if (callback != null) {
                            callback.detectResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                showToast(getApplicationContext(),"网络连接失败,请重试");
                            }
                        });
                    }
                }
            }).start();
        }
    }

    //返回json
    private interface DetectCallback {
        void detectResult(JSONObject rst);
    }

    // 自定义的按钮监听外部类
    public class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getTag()=="rlIcon1"){
                //Toast.makeText(getApplicationContext(),"按钮一",Toast.LENGTH_SHORT).show();
                Random rand = new Random();
                final int i=rand.nextInt(3);

                // TODO Auto-generated method stub
                dialog = createLoadingDialog(MainActivity.this, "匹配中...");
                dialog.show();
                // textView.setText("Waiting ...");

                FaceppDetect faceppDetect = new FaceppDetect();
                faceppDetect.setDetectCallback(new DetectCallback() {

                    public void detectResult(final JSONObject rst) {
                        // use the red paint
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStrokeWidth(Math.max(img.getWidth(),
                                img.getHeight()) / 100f);

                        // create a new canvas
                        final Bitmap bitmap = Bitmap.createBitmap(
                                img.getWidth(), img.getHeight(),
                                img.getConfig());
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(img, new Matrix(), null);

                        try {
                            // find out all faces
                            final int count = rst.getJSONArray("face").length();
                            for (int i = 0; i < count; ++i) {
                                float x, y, w, h;
                                // get the center point
                                x = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getJSONObject("center").getDouble("x");
                                y = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getJSONObject("center").getDouble("y");

                                // get face size
                                w = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getDouble("width");
                                h = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getDouble("height");
                                age = rst.getJSONArray("face").getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("age")
                                        .getString("value");
                                range = rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("age")
                                        .getString("range");
                                gender = rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("gender")
                                        .getString("value");
                                // change percent value to the real size
                                x = x / 100 * img.getWidth();
                                w = w / 100 * img.getWidth() * 0.7f;
                                y = y / 100 * img.getHeight();
                                h = h / 100 * img.getHeight() * 0.7f;

                                // draw the box to mark it out
                                canvas.drawLine(x - w, y - h, x - w, y + h,
                                        paint);
                                canvas.drawLine(x - w, y - h, x + w, y - h,
                                        paint);
                                canvas.drawLine(x + w, y + h, x - w, y + h,
                                        paint);
                                canvas.drawLine(x + w, y + h, x + w, y - h,
                                        paint);
                            }
                            // save new image
                            // img = bitmap;

                            MainActivity.this.runOnUiThread(new Runnable() {

                                public void run() {
                                    dialog.dismiss();
                                    String msg = null;
                                    try {
                                        if (Integer.valueOf(age) <= 15) {
                                            msg = Integer.valueOf(age)
                                                    + Integer.valueOf(range)
                                                    + "";
                                        } else if (Integer.valueOf(age) <= 18
                                                && Integer.valueOf(age) >= 15) {
                                            msg = age;
                                        } else if (Integer.valueOf(age) > 25
                                                && Integer.valueOf(age) <= 35) {
                                            int i = (Integer.valueOf(age) - 25)
                                                    / Integer.valueOf(range);
                                            msg = Integer.valueOf(age) - i + "";
                                        } else if (Integer.valueOf(age) > 35) {
                                            msg = Integer.valueOf(age)
                                                    - Integer.valueOf(range)
                                                    + "";
                                        } else {
                                            msg = age;
                                        }
                                        age = msg;
                                    } catch (Exception e) {
                                        showToast(getApplicationContext(), "分析失败.");
                                        return;
                                    }
                                    if (gender.equals("Male")) {
                                        gender = "男";
                                    } else if (gender.equals("Female")) {
                                        gender = "女";
                                    }

                                    msg = "匹配的分析结果:" + gender + "   " + msg + "岁";
                                    //showToast(getApplicationContext(),msg);

                                    myDialog = new MyDialog(MainActivity.this);
                                    myDialog.setTextView(Matching.setOtOMessage(age));
                                    if (gender.equals("女")) {
                                        bm = getMyImage.getImageFromAssetsFile(getApplicationContext(), "male" + Matching.sethuman(age, i) + "man.png");
                                    } else {
                                        bm = getMyImage.getImageFromAssetsFile(getApplicationContext(), "female" + Matching.sethuman(age, i) + "woman.png");
                                    }
                                    //bm=getMyImage.getImageFromAssetsFile(getApplicationContext(),"wy.png");
                                    myDialog.setImageView(bm);
                                    myDialog.setCloseOnclickListener(new MyDialog.closeOnclickListener(){

                                        @Override
                                        public void closeClick() {
                                            // TODO Auto-generated method stub
                                            //Toast.makeText(MainActivity.this,"点击了--关闭--按钮",Toast.LENGTH_SHORT).show();
                                            showToast(getApplicationContext(),"点击了--关闭--按钮");
                                            myDialog.dismiss();
                                        }

                                    });
                                    myDialog.show();
                                    // show the image
                                    imageView.setImageBitmap(bitmap);
                                    age = "";
                                }

                                ;

                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showToast(getApplicationContext(), "JSONException");
                                }
                            });
                        }
                    }
                });
                faceppDetect.detect(img);
            }
            else if (v.getTag()=="rlIcon2"){
                //Toast.makeText(getApplicationContext(),"按钮二",Toast.LENGTH_SHORT).show();
                dialog = createLoadingDialog(MainActivity.this, "分析中...");
                dialog.show();
                // textView.setText("Waiting ...");

                FaceppDetect faceppDetect = new FaceppDetect();
                faceppDetect.setDetectCallback(new DetectCallback() {

                    public void detectResult(final JSONObject rst) {
                        // use the red paint
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStrokeWidth(Math.max(img.getWidth(),
                                img.getHeight()) / 100f);

                        // create a new canvas
                        final Bitmap bitmap = Bitmap.createBitmap(
                                img.getWidth(), img.getHeight(),
                                img.getConfig());
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(img, new Matrix(), null);

                        try {
                            // find out all faces
                            final int count = rst.getJSONArray("face").length();
                            for (int i = 0; i < count; ++i) {
                                float x, y, w, h;
                                // get the center point
                                x = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getJSONObject("center").getDouble("x");
                                y = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getJSONObject("center").getDouble("y");

                                // get face size
                                w = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getDouble("width");
                                h = (float) rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("position")
                                        .getDouble("height");
                                age = rst.getJSONArray("face").getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("age")
                                        .getString("value");
                                range = rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("age")
                                        .getString("range");
                                gender = rst.getJSONArray("face")
                                        .getJSONObject(i)
                                        .getJSONObject("attribute")
                                        .getJSONObject("gender")
                                        .getString("value");
                                // change percent value to the real size
                                x = x / 100 * img.getWidth();
                                w = w / 100 * img.getWidth() * 0.7f;
                                y = y / 100 * img.getHeight();
                                h = h / 100 * img.getHeight() * 0.7f;

                                // draw the box to mark it out
                                canvas.drawLine(x - w, y - h, x - w, y + h,
                                        paint);
                                canvas.drawLine(x - w, y - h, x + w, y - h,
                                        paint);
                                canvas.drawLine(x + w, y + h, x - w, y + h,
                                        paint);
                                canvas.drawLine(x + w, y + h, x + w, y - h,
                                        paint);
                            }
                            // save new image
                            // img = bitmap;

                            MainActivity.this.runOnUiThread(new Runnable() {

                                public void run() {
                                    dialog.dismiss();
                                    String msg = null;
                                    try {
                                        if (Integer.valueOf(age) <= 15) {
                                            msg = Integer.valueOf(age)
                                                    + Integer.valueOf(range)
                                                    + "";
                                        } else if (Integer.valueOf(age) <= 18
                                                && Integer.valueOf(age) >= 15) {
                                            msg = age;
                                        } else if (Integer.valueOf(age) > 25
                                                && Integer.valueOf(age) <= 35) {
                                            int i = (Integer.valueOf(age) - 25)
                                                    / Integer.valueOf(range);
                                            msg = Integer.valueOf(age) - i + "";
                                        } else if (Integer.valueOf(age) > 35) {
                                            msg = Integer.valueOf(age)
                                                    - Integer.valueOf(range)
                                                    + "";
                                        } else {
                                            msg = age;
                                        }
                                        age = msg;
                                    } catch (Exception e) {
                                        showToast(getApplicationContext(),"分析失败.");
                                        return;
                                    }
                                    if (gender.equals("Male")) {
                                        gender = "男";
                                    } else if (gender.equals("Female")) {
                                        gender = "女";
                                    }
                                    msg = "分析结果:" + gender + "   " + msg + "岁";
                                    showToast(getApplicationContext(),msg);
                                    // show the image
                                    imageView.setImageBitmap(bitmap);
                                    age="";
                                };

                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showToast(getApplicationContext(),"JSONException");
                                }
                            });
                        }

                    }
                });
                faceppDetect.detect(img);
            }
            else if (v.getTag()=="rlIcon3"){
                //Toast.makeText(getApplicationContext(),"按钮三",Toast.LENGTH_SHORT).show();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
            }
        }
    }
}
