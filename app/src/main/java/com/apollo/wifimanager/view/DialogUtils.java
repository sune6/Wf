package com.apollo.wifimanager.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apollo.wifimanager.R;

/**
 * Created by Sun on 2016/5/9.
 */
public class DialogUtils {

    /**
     * 创建一个自定义布局的对话框
     * @param context 上下文对象
     * @param contentView 对话框布局
     * @return
     */
    public static Dialog createDialog(Context context, View contentView){

        return createDialog(context, contentView, 0.3f, 0.7f);
    }

    /**
     * 创建一个自定义布局的对话框
     * @param context 上下文对象
     * @param contentView 对话框布局
     * @param h 对话框高度占屏幕的比例，如：0.4，即屏幕高度的40%
     * @param w 对话框宽度占屏幕的比例，如：0.7，即屏幕高度的70%
     * @return
     */
    public static Dialog createDialog(Context context, View contentView, float h, float w){
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(contentView);
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * h); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * w); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialogWindow.setBackgroundDrawable(new BitmapDrawable());//防止圆角周围的四个黑角

        return dialog;
    }

}
