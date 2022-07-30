package com.example.foliage.constant;

/**
 * Description：公共静态config
 * Created by liang.qfzc@gmail.com on 2018/6/11.
 */

public class CommonConfig {

    public static String APP_DIR_NAME = "qlch";

    public static String DIR_APP_NAME = android.os.Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + APP_DIR_NAME;

    public static String DIR_APP_CACHE = DIR_APP_NAME + "/cache";

    public static String DIR_APP_PROTOBUF = DIR_APP_NAME + "/liveness";

    /**
     * 接口回调正常code
     */
    public final static String RET_OK = "0000";
    /**
     * 在另一端登录，当前sessionId失效
     */
    public final static String OFFLINE = "7000";

    /**
     * 分页加载数据每页10条
     */
    public static final int PAGENUMER = 10;

    /**
     * 消息发送等待时间
     */
    public static final int SMS_SEC = 60;

    /**
     * intent传递
     */
    public static final String INTENT_GOODSID = "goodsId";
    public static final String INTENT_BUNDLE = "bundle";
    public static final String INTENT_ISTRANSPUSH = "isTransPush";

    /**
     * 微信、QQ注册ID
     */
    public static final String WX_APPID    = "wx874adc7e2f7fef35";
    public static final String WXPAY_APPID = "wx06f223b4b40503e4";
    public static final String QQ_APPID    = "1108109036";
    public static final String FB_APPID    = "23725345";
    public static final String WX_SECRET   = "5e83c43d589a339edbbb17f1ad953232";
    /**
     * 微信支付回调
     */
    public static final String WX_PAY_RESULT_ACTION = "com.weixin.pay.result.action";
    public static final String WX_PAY_RESULT        = "wx_pay_result";

    /**
     * 支付类型
     */
    public static final String TRANTYPE_APP  = "APP";
    public static final String TRANTYPE_H5   = "H5";
    public static final String TRANTYPE_SCAN = "SCAN";

    /**
     * 图片压缩值
     */
    public static final int LUBAN_COMPRESSSIZE = 400;
    public static final int LUBAN_COMPRESSSIZE_PERSONAL = 200;

    /**
     * 支付类型
     */
    public static final String PAYTYPE_BALANCE = "BALANCE";//余额
    public static final String PAYTYPE_UNIONPAY_SCAN = "UNIONPAY_SCAN";//银联扫码
    public static final String PAYTYPE_ALIPAY_APP = "ALIPAY_APP";//支付宝SDK
    public static final String PAYTYPE_WECHAT_APP = "WECHAT_APP";//微信SDK

    /**
     * 分享类型
     */
    public static String SHARETYPE_WXCIRCLE = "WXCIRCLE";
    public static String SHARETYPE_WX       = "WX";
    public static String SHARETYPE_QQ       = "QQ";
    public static String SHARETYPE_QZONE    = "QZONE";

    /**
     * 会员等级
     */
    public static final String MEMBER_NORMAL = "NORMAL";//普通用户
    public static final String MEMBER_VIP = "VIP";//会员
    public static final String MEMBER_AMBASSADOR = "AMBASSADOR";//推广大使
    public static final String MEMBER_SERVICE = "SERVICE";//服务商
    public static final String MEMBER_OPERATOR = "OPERATOR";//总运营

    /**
     * 网络传参公共参数
     */
    public static final String REQDATA       = "reqData";
    public static final String SESSIONID     = "sessionId";
    public static final String CHARSET       = "charset";
    public static final String VERSION       = "version";
    public static final String SIGNTYPE      = "signType";
    public static final String APPTYPE      = "appType";
    public static final String ENCTYPE       = "encType";
    public static final String PUBKEYVERSION = "pubKeyVersion";
    public static final String REQTIME       = "reqTime";
    public static final String KEY           = "key";
    public static final String SIGN          = "sign";

    /**
     * 认证状态
     */
    public static final String AUTHEN_NOTSUB       = "INIT";//未提交0
    public static final String AUTHEN_NOCOMPLETION = "INCOMPLETE_INFO";//资料待补全4
    public static final String AUTHEN_PASS         = "PASS";//已认证2
    public static final String AUTHEN_AUDIT        = "IN_REVIEW";//审核中1
    public static final String AUTHEN_NOPASS       = "REFUSE";//审核未通过3

    /**
     * 图片类型
     */
    public static final String TYPE_IDFROPIC  = "idFroPic";
    public static final String TYPE_IDCONPIC  = "idConPic";
    public static final String TYPE_IDHANDPIC = "idHandPic";
    public static final String TYPE_CRDFROPIC = "crdFroPic";
    public static final String TYPE_CRDCONPIC = "crdConPic";

    /**
     * 支付类型
     */
    public static final String TYPE_ALIPAY = "ALIPAY";
    public static final String TYPE_ALIPAY_APP = "ALIPAY_APP";
    public static final String TYPE_WEICHAT = "WEICHAT";
    public static final String TYPE_QQ = "QQ";
    public static final String TYPE_ALIPAY_WAP = "ALIPAY_WAP";
    public static final String TYPE_QQ_WAP = "QQ_WAP";
    public static final String TYPE_UNIONPAY = "UNIONPAY";//银联
    public static final String TYPE_UNIONPAY_SCAN = "UNIONPAY_SCAN";//银联
    public static final String TYPE_QUICKPAY = "QUICKPAY";
    public static final String TYPE_BPQUICKPAY = "BPQUICKPAY";
    public static final String TYPE_T1QUICKPAY = "T1QUICKPAY";
    public static final String TYPE_YUNSF = "YUNSF";//云闪付
    public static final String TYPE_DEV = "DEV";
    public static final String TYPE_C_QUICKPAY = "C_QUICKPAY";
    public static final String TYPE_BP_C_QUICKPAY = "BP_C_QUICKPAY";
    public static final String TYPE_RESIDUE = "RESIDUE";

    /**
     * 新老用户标识
     */
    public static final String NEW_FLAG = "NEW";
    public static final String OLD_FLAG = "OLD";

    /**
     * 新老用户标识
     */
    public static final String INTERFACETYPE_H5 = "H5";
    public static final String INTERFACETYPE_API = "API";

    /**
     * 是否是会员
     */
    public static final String VIP_USER = "VIP";
    public static final String NORMAL_USER = "NORMAL";

    /**
     * 验证码发送类型
     */
    public static final String SMSTYPE_REGISTER = "REGISTER";//注册
    public static final String SMSTYPE_UPDPWD = "MOFDIFY_LOGIN_PASSWORD";//修改登录密码
    public static final String SMSTYPE_UPDPAYPWD = "MOFDIFY_PAY_PASSWORD";//修改支付密码
    public static final String SMSTYPE_CERTIFICATE = "04";//实名认证
    public static final String SMSTYPE_TRANS = "05";//转账
    public static final String SMSTYPE_ADDBANK = "ADD_BANK";//添加银行卡
    public static final String SMSTYPE_MODACCOUNT = "MOFDIFY_LOGIN_PHONE_NUMBER";//修改登录手机号

    /**
     * APP跳转标识
     */
    public static final String FLAG_NATIVE     = "native";
    public static final String FLAG_HTML       = "html";
    public static final String FLAG_OVERSEAPAY = "overSeaPay";

    /**
     * 上拉加载更多状态
     */
    public static final String LOADMORECOMPLETE = "loadMoreComplete";
    public static final String LOADMOREEND = "loadMoreEnd";
    public static final String LOADMOREFAIL = "loadMoreFail";

    /**
     * 银行卡类型
     */
    public static final String CREDIT_CARD_TYPE = "CREDIT";
    public static final String DEPOSIT_CARD_TYPE = "DEBIT";
    public static final String CREDIT_CARD = "信用卡";
    public static final String DEPOSIT_CARD = "储蓄卡";

    /**
     * requstCode
     */
    public static final int REQUESTCODE_PICKER_QRCODE = 10001;
    public static final int REQUESTCODE_VIEW_QRCODE = REQUESTCODE_PICKER_QRCODE + 1;
    public static final int REQUESTCODE_CERTIFICATE_HELP = REQUESTCODE_VIEW_QRCODE + 1;
    public static final int REQUESTCODE_CERTIFICATE_PICKER = REQUESTCODE_CERTIFICATE_HELP + 1;
    public static final int REQUESTCODE_CERTIFICATE_VIEW_FRONT = REQUESTCODE_CERTIFICATE_PICKER + 1;
    public static final int REQUESTCODE_CERTIFICATE_VIEW_REVERSE = REQUESTCODE_CERTIFICATE_VIEW_FRONT + 1;
    public static final int REQUESTCODE_CERTIFICATE_VIEW_CRDFRO = REQUESTCODE_CERTIFICATE_VIEW_REVERSE + 1;
    public static final int REQUESTCODE_CERTIFICATE_VIEW_CRDCON = REQUESTCODE_CERTIFICATE_VIEW_CRDFRO + 1;
    public static final int REQUESTCODE_CERTIFICATE_VIEW_HAND = REQUESTCODE_CERTIFICATE_VIEW_CRDCON + 1;
    public static final int REQUESTCODE_UPDATE_BANKTEL = REQUESTCODE_CERTIFICATE_VIEW_HAND + 1;
    public static final int REQUESTCODE_DEFBANK_VIEW_CRDFRO = REQUESTCODE_UPDATE_BANKTEL + 1;
    public static final int REQUESTCODE_DEFBANK_VIEW_CRDCON = REQUESTCODE_DEFBANK_VIEW_CRDFRO + 1;
    public static final int REQUESTCODE_DEFBANK_VIEW_IDHAND = REQUESTCODE_DEFBANK_VIEW_CRDCON + 1;
    public static final int REQUESTCODE_PROMOTE_POSTER = REQUESTCODE_DEFBANK_VIEW_IDHAND + 1;
    public static final int REQUESTCODE_GOODS_ADDRESS = REQUESTCODE_PROMOTE_POSTER + 1;
    public static final int REQUESTCODE_GATHERING_ADDBANK = REQUESTCODE_GOODS_ADDRESS + 1;
    public static final int REQUESTCODE_PICK_CONTACT = REQUESTCODE_GATHERING_ADDBANK + 1;
    public static final int REQUESTCODE_SETPAY_PASSWORD = REQUESTCODE_PICK_CONTACT + 1;
    public static final int REQUESTCODE_REPAY_CREDIT = REQUESTCODE_SETPAY_PASSWORD + 1;
    public static final int PERMISSION_REQUEST_CAMERA = REQUESTCODE_REPAY_CREDIT + 1;
    public static final int PERMISSION_REQUEST_SD_WRITE = PERMISSION_REQUEST_CAMERA + 1;
    public static final int KEY_TO_DETECT_REQUEST_CODE = PERMISSION_REQUEST_SD_WRITE + 1;
    public static final int PERMISSION_REQUEST_WRITE = KEY_TO_DETECT_REQUEST_CODE + 1;
    public static final int WEB_REQUESTCODE_FILE_CHOOSE = PERMISSION_REQUEST_WRITE + 1;
    public static final int REQUESTCODE_IMAGE_CAPTURE = WEB_REQUESTCODE_FILE_CHOOSE + 1;
    public static final int REQUESTCODE_IMAGE_PICKER = REQUESTCODE_IMAGE_CAPTURE + 1;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = REQUESTCODE_IMAGE_PICKER + 1;
}
