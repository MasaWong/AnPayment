package mw.ankara.payment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.pingplusplus.android.PaymentActivity;
import com.unionpay.UPPayAssistEx;

/**
 * 支付类
 *
 * @author masawong
 * @since 8/28/15
 */
public class PaymentHelper {

    public static final int REQUEST_CODE_PAYMENT = 0xae00;

    /**
     * 调起支付
     *
     * @param activity
     * @param charge
     */
    public static void pay(Activity activity, String charge) {
        Intent intent = new Intent();
        String packageName = activity.getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * 处理返回值
     * "success" - payment succeed
     * "fail"    - payment failed
     * "cancel"  - user canceld
     * "invalid" - payment plugin not installed
     * <p>
     * 如果是银联渠道返回 invalid，调用 UPPayAssistEx.installUPPayPlugin(this); 安装银联安全支付控件。
     */
    public static boolean onActivityResult(Activity activity, int requestCode, Intent data) {
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            String result = data.getExtras().getString("pay_result");
            if (result == null || result.equals("fail")) {
                Toast.makeText(activity, R.string.payment_failed, Toast.LENGTH_SHORT).show();
            } else if (result.equals("cancel")) {
                Toast.makeText(activity, R.string.payment_canceled, Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                Toast.makeText(activity, R.string.payment_success, Toast.LENGTH_SHORT).show();
                return true;
            } else if (result.equals("invalid")) {
                final Context appContext = activity.getApplicationContext();
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.payment_need_uppay)
                        .setPositiveButton(R.string.payment_install, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UPPayAssistEx.installUPPayPlugin(appContext);
                            }
                        })
                        .setNegativeButton(R.string.payment_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }

        return false;
    }
}
