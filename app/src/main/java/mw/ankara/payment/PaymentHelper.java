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

    public static String charge = "{\n" +
        "  \"id\": \"ch_zfLCyT5CWDaHfzfH88PW18qH\",\n" +
        "  \"object\": \"charge\",\n" +
        "  \"created\": 1440839200,\n" +
        "  \"livemode\": false,\n" +
        "  \"paid\": false,\n" +
        "  \"refunded\": false,\n" +
        "  \"app\": \"app_TuvLSG9e5848jvzz\",\n" +
        "  \"channel\": \"upacp\",\n" +
        "  \"order_no\": \"123456789\",\n" +
        "  \"client_ip\": \"127.0.0.1\",\n" +
        "  \"amount\": 100,\n" +
        "  \"amount_settle\": 0,\n" +
        "  \"currency\": \"cny\",\n" +
        "  \"subject\": \"Your Subject\",\n" +
        "  \"body\": \"Your Body\",\n" +
        "  \"time_paid\": null,\n" +
        "  \"time_expire\": 1440842800,\n" +
        "  \"time_settle\": null,\n" +
        "  \"transaction_no\": null,\n" +
        "  \"refunds\": {\n" +
        "    \"object\": \"list\",\n" +
        "    \"url\": \"1arges_zfLCyT5CWDaHfzfH88PW18qH/refunds\",\n" +
        "    \"has_more\": false,\n" +
        "    \"data\": []\n" +
        "  },\n" +
        "  \"amount_refunded\": 0,\n" +
        "  \"failure_code\": null,\n" +
        "  \"failure_msg\": null,\n" +
        "  \"metadata\": {},\n" +
        "  \"credential\": {\n" +
        "    \"object\": \"credential\",\n" +
        "    \"upacp\": {\n" +
        "      \"tn\": \"201508291706406828345\",\n" +
        "      \"mode\": \"00\"\n" +
        "    }\n" +
        "  },\n" +
        "  \"extra\": {},\n" +
        "  \"description\": null\n" +
        "}";

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
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, PaymentHelper.charge);
        activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * 处理返回值
     * "success" - payment succeed
     * "fail"    - payment failed
     * "cancel"  - user canceld
     * "invalid" - payment plugin not installed
     * <p/>
     * 如果是银联渠道返回 invalid，调用 UPPayAssistEx.installUPPayPlugin(this); 安装银联安全支付控件。
     */
    public static boolean onActivityResult(Activity activity, int requestCode, Intent data) {
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            String result = data.getExtras().getString("pay_result");
            if (result == null || result.equals("fail")) {
                Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
            } else if (result.equals("cancel")) {
                Toast.makeText(activity, "支付取消", Toast.LENGTH_SHORT).show();
            } else if (result.equals("success")) {
                Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                return true;
            } else if (result.equals("invalid")) {
                final Context appContext = activity.getApplicationContext();
                new AlertDialog.Builder(activity)
                    .setMessage("需要安装银联安全支付控件")
                    .setPositiveButton("安装", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UPPayAssistEx.installUPPayPlugin(appContext);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
