package com.hwp.commons.util.identity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 生份证工具
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public class IdCardUtils {
    /**
     * 18位身份证正则表达式
     */
    private static final Pattern ID_CARD_18_PATTERN =
            Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");

    /**
     * 15位身份证正则表达式
     */
    private static final Pattern ID_CARD_15_PATTERN =
            Pattern.compile("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$");

    /**
     * 18位权重系数
     */
    private static final int[] WEIGHTS_18 = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 校验码对应表
     */
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 身份证数据
     */
    @Getter
    @Setter
    @ToString
    public static class IdCardInfo {
        private boolean isValid = false; // 是否合法
        private String idCardCode = ""; // 身份证号码
        private LocalDate birthDate = null; // 出生年月日
        private Integer age = 0; // 年龄
        private Byte gender = 1; //性别(0:女|1:男)
        private String genderTxt = ""; // 性别描述(0:F|1:M)
        private String genderDesc = ""; // 性别(0:女|1:男)
        private String regionCode = "";  // 地区代码
        private String province = ""; // 省份
        private String city = ""; // 城市
        private String errorMessage = ""; // 验证错误消息

        // 获取籍贯
        public String getNativePlace() {
            if (this.province.equals(this.city)) {
                return this.province;
            }
            return this.province + "/" + this.city;
        }
    }

    /**
     * 分析身份证号码是否有效
     *
     * @param idCard 身份证号码
     * @return 是否有效
     */
    public static IdCardInfo parse(String idCard) {
        IdCardInfo info = new IdCardInfo();

        if (idCard == null || idCard.trim().isEmpty()) {
            info.setValid(false);
            info.setErrorMessage("身份证号码不能为空");
            return info;
        }

        idCard = idCard.trim().toUpperCase();
        info.setIdCardCode(idCard);

        // 判断是15位还是18位
        boolean is18Bit = idCard.length() == 18;
        boolean is15Bit = idCard.length() == 15;

        if (!is18Bit && !is15Bit) {
            info.setValid(false);
            info.setErrorMessage("身份证号码长度不正确");
            return info;
        }

        // 格式验证
        if (is18Bit && !ID_CARD_18_PATTERN.matcher(idCard).matches()) {
            info.setValid(false);
            info.setErrorMessage("18位身份证号码格式不正确");
            return info;
        }

        if (is15Bit && !ID_CARD_15_PATTERN.matcher(idCard).matches()) {
            info.setValid(false);
            info.setErrorMessage("15位身份证号码格式不正确");
            return info;
        }

        // 校验码验证（仅18位）
        if (is18Bit && !validateCheckCode(idCard)) {
            info.setValid(false);
            info.setErrorMessage("身份证校验码不正确");
            return info;
        }

        // 解析生日, 兼容15位身份证号
        String birthDateStr = is18Bit ? idCard.substring(6, 14) : "19" + idCard.substring(6, 12);
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DATE_FORMATTER);
            info.setBirthDate(birthDate);

            // 验证日期合理性（不能是未来日期）
            if (birthDate.isAfter(LocalDate.now())) {
                info.setValid(false);
                info.setErrorMessage("出生日期不能是未来日期");
                return info;
            }

            // 计算年龄
            info.setAge(calculateAge(birthDate));

        } catch (DateTimeParseException e) {
            info.setValid(false);
            info.setErrorMessage("出生日期无效");
            return info;
        }

        // 解析性别
        int genderCode = Integer.parseInt(idCard.substring(idCard.length() - 2, idCard.length() - 1));
        boolean isMale = genderCode % 2 == 1;
        info.setGender(isMale ? (byte) 1 : (byte) 0);
        info.setGenderTxt(isMale ? "M" : "F");
        info.setGenderDesc(isMale ? "男" : "女");

        // 解析地区
        info.setRegionCode(idCard.substring(0, 6));
        info.setProvince(RegionData.PROVINCE_MAP.getOrDefault(idCard.substring(0, 2), ""));
        info.setCity(RegionData.CITY_MAP.getOrDefault(idCard.substring(0, 4), ""));

        info.setValid(true);
        return info;
    }

    /**
     * 计算身份证号码的校验码
     *
     * @param idCardPrefix 身份证号码的前 17 位
     * @return 校验码字符
     */
    private static char calculateCheckCode(String idCardPrefix) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCardPrefix.charAt(i) - '0') * WEIGHTS_18[i];
        }
        int mod = sum % 11;
        return CHECK_CODES[mod];
    }

    /**
     * 计算年龄
     *
     * @param birthDate 生日日期
     * @return 年龄
     */
    private static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 15位身份证转18位
     *
     * @param idCard15 15位身份证号码
     * @return 18位身份证号码，转换失败返回null
     */
    public static String convert15to18(String idCard15) {
        if (idCard15 == null || idCard15.length() != 15) {
            return null;
        }

        if (!ID_CARD_15_PATTERN.matcher(idCard15).matches()) {
            return null;
        }

        // 在第6位后添加19
        String idCard17 = idCard15.substring(0, 6) + "19" + idCard15.substring(6);

        // 计算校验码
        char checkCode = calculateCheckCode(idCard17);

        return idCard17 + checkCode;
    }

    /**
     * 验证18位身份证校验码
     */
    private static boolean validateCheckCode(String idCard) {
        char expectedCheckCode = calculateCheckCode(idCard.substring(0, 17));
        char actualCheckCode = idCard.charAt(17);
        return expectedCheckCode == actualCheckCode;
    }

    private static final class RegionData {
        private static final Map<String, String> PROVINCE_MAP = new ConcurrentHashMap<>();
        private static final Map<String, String> CITY_MAP = new ConcurrentHashMap<>();

        static {
            // 省份城市开始
            PROVINCE_MAP.put("11", "北京市");
            PROVINCE_MAP.put("12", "天津市");
            PROVINCE_MAP.put("13", "河北省");
            PROVINCE_MAP.put("14", "山西省");
            PROVINCE_MAP.put("15", "内蒙古自治区");
            PROVINCE_MAP.put("21", "辽宁省");
            PROVINCE_MAP.put("22", "吉林省");
            PROVINCE_MAP.put("23", "黑龙江省");
            PROVINCE_MAP.put("31", "上海市");
            PROVINCE_MAP.put("32", "江苏省");
            PROVINCE_MAP.put("33", "浙江省");
            PROVINCE_MAP.put("34", "安徽省");
            PROVINCE_MAP.put("35", "福建省");
            PROVINCE_MAP.put("36", "江西省");
            PROVINCE_MAP.put("37", "山东省");
            PROVINCE_MAP.put("41", "河南省");
            PROVINCE_MAP.put("42", "湖北省");
            PROVINCE_MAP.put("43", "湖南省");
            PROVINCE_MAP.put("44", "广东省");
            PROVINCE_MAP.put("45", "广西壮族自治区");
            PROVINCE_MAP.put("46", "海南省");
            PROVINCE_MAP.put("50", "重庆市");
            PROVINCE_MAP.put("51", "四川省");
            PROVINCE_MAP.put("52", "贵州省");
            PROVINCE_MAP.put("53", "云南省");
            PROVINCE_MAP.put("54", "西藏自治区");
            PROVINCE_MAP.put("61", "陕西省");
            PROVINCE_MAP.put("62", "甘肃省");
            PROVINCE_MAP.put("63", "青海省");
            PROVINCE_MAP.put("64", "宁夏回族自治区");
            PROVINCE_MAP.put("65", "新疆维吾尔自治区");
            PROVINCE_MAP.put("71", "台湾省");
            PROVINCE_MAP.put("81", "香港特别行政区");
            PROVINCE_MAP.put("82", "澳门特别行政区");

            // 城市数据开始
            CITY_MAP.put("1100", "北京市");
            CITY_MAP.put("1101", "北京市");
            CITY_MAP.put("1102", "北京市");
            CITY_MAP.put("1200", "天津市");
            CITY_MAP.put("1201", "天津市");
            CITY_MAP.put("1202", "天津市");
            CITY_MAP.put("1300", "河北省");
            CITY_MAP.put("1301", "石家庄市");
            CITY_MAP.put("1302", "唐山市");
            CITY_MAP.put("1303", "秦皇岛市");
            CITY_MAP.put("1304", "邯郸市");
            CITY_MAP.put("1305", "邢台市");
            CITY_MAP.put("1306", "保定市");
            CITY_MAP.put("1307", "张家口市");
            CITY_MAP.put("1308", "承德市");
            CITY_MAP.put("1309", "沧州市");
            CITY_MAP.put("1310", "廊坊市");
            CITY_MAP.put("1311", "衡水市");
            CITY_MAP.put("1400", "山西省");
            CITY_MAP.put("1401", "太原市");
            CITY_MAP.put("1402", "大同市");
            CITY_MAP.put("1403", "阳泉市");
            CITY_MAP.put("1404", "长治市");
            CITY_MAP.put("1405", "晋城市");
            CITY_MAP.put("1406", "朔州市");
            CITY_MAP.put("1407", "晋中市");
            CITY_MAP.put("1408", "运城市");
            CITY_MAP.put("1409", "忻州市");
            CITY_MAP.put("1410", "临汾市");
            CITY_MAP.put("1411", "吕梁市");
            CITY_MAP.put("1500", "内蒙古自治区");
            CITY_MAP.put("1501", "内蒙古自治区");
            CITY_MAP.put("1502", "包头市");
            CITY_MAP.put("1503", "乌海市");
            CITY_MAP.put("1504", "赤峰市");
            CITY_MAP.put("1505", "通辽市");
            CITY_MAP.put("1506", "鄂尔多斯市");
            CITY_MAP.put("1507", "呼伦贝尔市");
            CITY_MAP.put("1508", "巴彦淖尔市");
            CITY_MAP.put("1509", "乌兰察布市");
            CITY_MAP.put("1522", "兴安盟");
            CITY_MAP.put("1525", "锡林郭勒盟");
            CITY_MAP.put("1529", "阿拉善盟");
            CITY_MAP.put("2100", "辽宁省");
            CITY_MAP.put("2101", "沈阳市");
            CITY_MAP.put("2102", "大连市");
            CITY_MAP.put("2103", "鞍山市");
            CITY_MAP.put("2104", "抚顺市");
            CITY_MAP.put("2105", "本溪市");
            CITY_MAP.put("2106", "丹东市");
            CITY_MAP.put("2107", "锦州市");
            CITY_MAP.put("2108", "营口市");
            CITY_MAP.put("2109", "阜新市");
            CITY_MAP.put("2110", "辽阳市");
            CITY_MAP.put("2111", "盘锦市");
            CITY_MAP.put("2112", "铁岭市");
            CITY_MAP.put("2113", "朝阳市");
            CITY_MAP.put("2114", "葫芦岛市");
            CITY_MAP.put("2200", "吉林省");
            CITY_MAP.put("2201", "长春市");
            CITY_MAP.put("2202", "吉林市");
            CITY_MAP.put("2203", "四平市");
            CITY_MAP.put("2204", "辽源市");
            CITY_MAP.put("2205", "通化市");
            CITY_MAP.put("2206", "白山市");
            CITY_MAP.put("2207", "松原市");
            CITY_MAP.put("2208", "白城市");
            CITY_MAP.put("2224", "延边朝鲜族自治州");
            CITY_MAP.put("2300", "黑龙江省");
            CITY_MAP.put("2301", "哈尔滨市");
            CITY_MAP.put("2302", "齐齐哈尔市");
            CITY_MAP.put("2303", "鸡西市");
            CITY_MAP.put("2304", "鹤岗市");
            CITY_MAP.put("2305", "双鸭山市");
            CITY_MAP.put("2306", "大庆市");
            CITY_MAP.put("2307", "伊春市");
            CITY_MAP.put("2308", "佳木斯市");
            CITY_MAP.put("2309", "七台河市");
            CITY_MAP.put("2310", "牡丹江市");
            CITY_MAP.put("2311", "黑河市");
            CITY_MAP.put("2312", "绥化市");
            CITY_MAP.put("2327", "大兴安岭地区");
            CITY_MAP.put("3100", "上海市");
            CITY_MAP.put("3101", "上海市");
            CITY_MAP.put("3102", "上海市");
            CITY_MAP.put("3200", "江苏省");
            CITY_MAP.put("3201", "南京市");
            CITY_MAP.put("3202", "无锡市");
            CITY_MAP.put("3203", "徐州市");
            CITY_MAP.put("3204", "常州市");
            CITY_MAP.put("3205", "苏州市");
            CITY_MAP.put("3206", "南通市");
            CITY_MAP.put("3207", "连云港市");
            CITY_MAP.put("3208", "淮安市");
            CITY_MAP.put("3209", "盐城市");
            CITY_MAP.put("3210", "扬州市");
            CITY_MAP.put("3211", "镇江市");
            CITY_MAP.put("3212", "泰州市");
            CITY_MAP.put("3213", "宿迁市");
            CITY_MAP.put("3300", "浙江省");
            CITY_MAP.put("3301", "杭州市");
            CITY_MAP.put("3302", "宁波市");
            CITY_MAP.put("3303", "温州市");
            CITY_MAP.put("3304", "嘉兴市");
            CITY_MAP.put("3305", "湖州市");
            CITY_MAP.put("3306", "绍兴市");
            CITY_MAP.put("3307", "金华市");
            CITY_MAP.put("3308", "衢州市");
            CITY_MAP.put("3309", "舟山市");
            CITY_MAP.put("3310", "台州市");
            CITY_MAP.put("3311", "丽水市");
            CITY_MAP.put("3400", "安徽省");
            CITY_MAP.put("3401", "合肥市");
            CITY_MAP.put("3402", "芜湖市");
            CITY_MAP.put("3403", "蚌埠市");
            CITY_MAP.put("3404", "淮南市");
            CITY_MAP.put("3405", "马鞍山市");
            CITY_MAP.put("3406", "淮北市");
            CITY_MAP.put("3407", "铜陵市");
            CITY_MAP.put("3408", "安庆市");
            CITY_MAP.put("3410", "黄山市");
            CITY_MAP.put("3411", "滁州市");
            CITY_MAP.put("3412", "阜阳市");
            CITY_MAP.put("3413", "宿州市");
            CITY_MAP.put("3415", "六安市");
            CITY_MAP.put("3416", "亳州市");
            CITY_MAP.put("3417", "池州市");
            CITY_MAP.put("3418", "宣城市");
            CITY_MAP.put("3500", "福建省");
            CITY_MAP.put("3501", "福州市");
            CITY_MAP.put("3502", "厦门市");
            CITY_MAP.put("3503", "莆田市");
            CITY_MAP.put("3504", "三明市");
            CITY_MAP.put("3505", "泉州市");
            CITY_MAP.put("3506", "漳州市");
            CITY_MAP.put("3507", "南平市");
            CITY_MAP.put("3508", "龙岩市");
            CITY_MAP.put("3509", "宁德市");
            CITY_MAP.put("3600", "江西省");
            CITY_MAP.put("3601", "南昌市");
            CITY_MAP.put("3602", "景德镇市");
            CITY_MAP.put("3603", "萍乡市");
            CITY_MAP.put("3604", "九江市");
            CITY_MAP.put("3605", "新余市");
            CITY_MAP.put("3606", "鹰潭市");
            CITY_MAP.put("3607", "赣州市");
            CITY_MAP.put("3608", "吉安市");
            CITY_MAP.put("3609", "宜春市");
            CITY_MAP.put("3610", "抚州市");
            CITY_MAP.put("3611", "上饶市");
            CITY_MAP.put("3700", "山东省");
            CITY_MAP.put("3701", "济南市");
            CITY_MAP.put("3702", "青岛市");
            CITY_MAP.put("3703", "淄博市");
            CITY_MAP.put("3704", "枣庄市");
            CITY_MAP.put("3705", "东营市");
            CITY_MAP.put("3706", "烟台市");
            CITY_MAP.put("3707", "潍坊市");
            CITY_MAP.put("3708", "济宁市");
            CITY_MAP.put("3709", "泰安市");
            CITY_MAP.put("3710", "威海市");
            CITY_MAP.put("3711", "日照市");
            CITY_MAP.put("3712", "莱芜市");
            CITY_MAP.put("3713", "临沂市");
            CITY_MAP.put("3714", "德州市");
            CITY_MAP.put("3715", "聊城市");
            CITY_MAP.put("3716", "滨州市");
            CITY_MAP.put("3717", "菏泽市");
            CITY_MAP.put("4100", "河南省");
            CITY_MAP.put("4101", "郑州市");
            CITY_MAP.put("4102", "开封市");
            CITY_MAP.put("4103", "洛阳市");
            CITY_MAP.put("4104", "平顶山市");
            CITY_MAP.put("4105", "安阳市");
            CITY_MAP.put("4106", "鹤壁市");
            CITY_MAP.put("4107", "新乡市");
            CITY_MAP.put("4108", "焦作市");
            CITY_MAP.put("4109", "濮阳市");
            CITY_MAP.put("4110", "许昌市");
            CITY_MAP.put("4111", "漯河市");
            CITY_MAP.put("4112", "三门峡市");
            CITY_MAP.put("4113", "南阳市");
            CITY_MAP.put("4114", "商丘市");
            CITY_MAP.put("4115", "信阳市");
            CITY_MAP.put("4116", "周口市");
            CITY_MAP.put("4117", "驻马店市");
            CITY_MAP.put("4190", "省直辖");
            CITY_MAP.put("4200", "湖北省");
            CITY_MAP.put("4201", "武汉市");
            CITY_MAP.put("4202", "黄石市");
            CITY_MAP.put("4203", "十堰市");
            CITY_MAP.put("4205", "宜昌市");
            CITY_MAP.put("4206", "襄阳市");
            CITY_MAP.put("4207", "鄂州市");
            CITY_MAP.put("4208", "荆门市");
            CITY_MAP.put("4209", "孝感市");
            CITY_MAP.put("4210", "荆州市");
            CITY_MAP.put("4211", "黄冈市");
            CITY_MAP.put("4212", "咸宁市");
            CITY_MAP.put("4213", "随州市");
            CITY_MAP.put("4228", "恩施土家族苗族自治州");
            CITY_MAP.put("4290", "省直辖");
            CITY_MAP.put("4300", "湖南省");
            CITY_MAP.put("4301", "长沙市");
            CITY_MAP.put("4302", "株洲市");
            CITY_MAP.put("4303", "湘潭市");
            CITY_MAP.put("4304", "衡阳市");
            CITY_MAP.put("4305", "邵阳市");
            CITY_MAP.put("4306", "岳阳市");
            CITY_MAP.put("4307", "常德市");
            CITY_MAP.put("4308", "张家界市");
            CITY_MAP.put("4309", "益阳市");
            CITY_MAP.put("4310", "郴州市");
            CITY_MAP.put("4311", "永州市");
            CITY_MAP.put("4312", "怀化市");
            CITY_MAP.put("4313", "娄底市");
            CITY_MAP.put("4331", "湘西土家族苗族自治州");
            CITY_MAP.put("4400", "广东省");
            CITY_MAP.put("4401", "广州市");
            CITY_MAP.put("4402", "韶关市");
            CITY_MAP.put("4403", "深圳市");
            CITY_MAP.put("4404", "珠海市");
            CITY_MAP.put("4405", "汕头市");
            CITY_MAP.put("4406", "佛山市");
            CITY_MAP.put("4407", "江门市");
            CITY_MAP.put("4408", "湛江市");
            CITY_MAP.put("4409", "茂名市");
            CITY_MAP.put("4412", "肇庆市");
            CITY_MAP.put("4413", "惠州市");
            CITY_MAP.put("4414", "梅州市");
            CITY_MAP.put("4415", "汕尾市");
            CITY_MAP.put("4416", "河源市");
            CITY_MAP.put("4417", "阳江市");
            CITY_MAP.put("4418", "清远市");
            CITY_MAP.put("4419", "东莞市");
            CITY_MAP.put("4420", "中山市");
            CITY_MAP.put("4451", "潮州市");
            CITY_MAP.put("4452", "揭阳市");
            CITY_MAP.put("4453", "云浮市");
            CITY_MAP.put("4500", "广西壮族自治区");
            CITY_MAP.put("4501", "南宁市");
            CITY_MAP.put("4502", "柳州市");
            CITY_MAP.put("4503", "桂林市");
            CITY_MAP.put("4504", "梧州市");
            CITY_MAP.put("4505", "北海市");
            CITY_MAP.put("4506", "防城港市");
            CITY_MAP.put("4507", "钦州市");
            CITY_MAP.put("4508", "贵港市");
            CITY_MAP.put("4509", "玉林市");
            CITY_MAP.put("4510", "百色市");
            CITY_MAP.put("4511", "贺州市");
            CITY_MAP.put("4512", "河池市");
            CITY_MAP.put("4513", "来宾市");
            CITY_MAP.put("4514", "崇左市");
            CITY_MAP.put("4600", "海南省");
            CITY_MAP.put("4601", "海口市");
            CITY_MAP.put("4602", "三亚市");
            CITY_MAP.put("4603", "三沙市");
            CITY_MAP.put("4690", "省直辖");
            CITY_MAP.put("5000", "重庆市");
            CITY_MAP.put("5001", "重庆市");
            CITY_MAP.put("5002", "重庆市");
            CITY_MAP.put("5100", "四川省");
            CITY_MAP.put("5101", "成都市");
            CITY_MAP.put("5103", "自贡市");
            CITY_MAP.put("5104", "攀枝花市");
            CITY_MAP.put("5105", "泸州市");
            CITY_MAP.put("5106", "德阳市");
            CITY_MAP.put("5107", "绵阳市");
            CITY_MAP.put("5108", "广元市");
            CITY_MAP.put("5109", "遂宁市");
            CITY_MAP.put("5110", "内江市");
            CITY_MAP.put("5111", "乐山市");
            CITY_MAP.put("5113", "南充市");
            CITY_MAP.put("5114", "眉山市");
            CITY_MAP.put("5115", "宜宾市");
            CITY_MAP.put("5116", "广安市");
            CITY_MAP.put("5117", "达州市");
            CITY_MAP.put("5118", "雅安市");
            CITY_MAP.put("5119", "巴中市");
            CITY_MAP.put("5120", "资阳市");
            CITY_MAP.put("5132", "阿坝藏族羌族自治州");
            CITY_MAP.put("5133", "甘孜藏族自治州");
            CITY_MAP.put("5134", "凉山彝族自治州");
            CITY_MAP.put("5200", "贵州省");
            CITY_MAP.put("5201", "贵阳市");
            CITY_MAP.put("5202", "六盘水市");
            CITY_MAP.put("5203", "遵义市");
            CITY_MAP.put("5204", "安顺市");
            CITY_MAP.put("5205", "毕节市");
            CITY_MAP.put("5206", "铜仁市");
            CITY_MAP.put("5223", "黔西南布依族苗族自治州");
            CITY_MAP.put("5226", "黔东南苗族侗族自治州");
            CITY_MAP.put("5227", "黔南布依族苗族自治州");
            CITY_MAP.put("5300", "云南省");
            CITY_MAP.put("5301", "昆明市");
            CITY_MAP.put("5303", "曲靖市");
            CITY_MAP.put("5304", "玉溪市");
            CITY_MAP.put("5305", "保山市");
            CITY_MAP.put("5306", "昭通市");
            CITY_MAP.put("5307", "丽江市");
            CITY_MAP.put("5308", "普洱市");
            CITY_MAP.put("5309", "临沧市");
            CITY_MAP.put("5323", "楚雄彝族自治州");
            CITY_MAP.put("5325", "红河哈尼族彝族自治州");
            CITY_MAP.put("5326", "文山壮族苗族自治州");
            CITY_MAP.put("5328", "西双版纳傣族自治州");
            CITY_MAP.put("5329", "大理白族自治州");
            CITY_MAP.put("5331", "德宏傣族景颇族自治州");
            CITY_MAP.put("5333", "怒江傈僳族自治州");
            CITY_MAP.put("5334", "迪庆藏族自治州");
            CITY_MAP.put("5400", "西藏自治区");
            CITY_MAP.put("5401", "拉萨市");
            CITY_MAP.put("5421", "昌都地区");
            CITY_MAP.put("5422", "山南地区");
            CITY_MAP.put("5423", "日喀则地区");
            CITY_MAP.put("5424", "那曲地区");
            CITY_MAP.put("5425", "阿里地区");
            CITY_MAP.put("5426", "林芝地区");
            CITY_MAP.put("6100", "陕西省");
            CITY_MAP.put("6101", "西安市");
            CITY_MAP.put("6102", "铜川市");
            CITY_MAP.put("6103", "宝鸡市");
            CITY_MAP.put("6104", "咸阳市");
            CITY_MAP.put("6105", "渭南市");
            CITY_MAP.put("6106", "延安市");
            CITY_MAP.put("6107", "汉中市");
            CITY_MAP.put("6108", "榆林市");
            CITY_MAP.put("6109", "安康市");
            CITY_MAP.put("6110", "商洛市");
            CITY_MAP.put("6200", "甘肃省");
            CITY_MAP.put("6201", "兰州市");
            CITY_MAP.put("6202", "嘉峪关市");
            CITY_MAP.put("6203", "金昌市");
            CITY_MAP.put("6204", "白银市");
            CITY_MAP.put("6205", "天水市");
            CITY_MAP.put("6206", "武威市");
            CITY_MAP.put("6207", "张掖市");
            CITY_MAP.put("6208", "平凉市");
            CITY_MAP.put("6209", "酒泉市");
            CITY_MAP.put("6210", "庆阳市");
            CITY_MAP.put("6211", "定西市");
            CITY_MAP.put("6212", "陇南市");
            CITY_MAP.put("6229", "临夏回族自治州");
            CITY_MAP.put("6230", "甘南藏族自治州");
            CITY_MAP.put("6300", "青海省");
            CITY_MAP.put("6301", "西宁市");
            CITY_MAP.put("6321", "海东地区");
            CITY_MAP.put("6322", "海北藏族自治州");
            CITY_MAP.put("6323", "黄南藏族自治州");
            CITY_MAP.put("6325", "海南藏族自治州");
            CITY_MAP.put("6326", "果洛藏族自治州");
            CITY_MAP.put("6327", "玉树藏族自治州");
            CITY_MAP.put("6328", "海西蒙古族藏族自治州");
            CITY_MAP.put("6400", "宁夏回族自治区");
            CITY_MAP.put("6401", "银川市");
            CITY_MAP.put("6402", "石嘴山市");
            CITY_MAP.put("6403", "吴忠市");
            CITY_MAP.put("6404", "固原市");
            CITY_MAP.put("6405", "中卫市");
            CITY_MAP.put("6500", "新疆维吾尔自治区");
            CITY_MAP.put("6501", "乌鲁木齐市");
            CITY_MAP.put("6502", "克拉玛依市");
            CITY_MAP.put("6521", "吐鲁番地区");
            CITY_MAP.put("6522", "哈密地区");
            CITY_MAP.put("6523", "昌吉回族自治州");
            CITY_MAP.put("6527", "博尔塔拉蒙古自治州");
            CITY_MAP.put("6528", "巴音郭楞蒙古自治州");
            CITY_MAP.put("6529", "阿克苏地区");
            CITY_MAP.put("6530", "克孜勒苏柯尔克孜自治州");
            CITY_MAP.put("6531", "喀什地区");
            CITY_MAP.put("6532", "和田地区");
            CITY_MAP.put("6540", "伊犁哈萨克自治州");
            CITY_MAP.put("6542", "塔城地区");
            CITY_MAP.put("6543", "阿勒泰地区");
            CITY_MAP.put("6590", "自治区直辖");
            CITY_MAP.put("7100", "台湾省");
            CITY_MAP.put("7101", "台北市");
            CITY_MAP.put("7102", "高雄市");
            CITY_MAP.put("7103", "基隆市");
            CITY_MAP.put("7104", "台中市");
            CITY_MAP.put("7105", "台南市");
            CITY_MAP.put("7106", "新竹市");
            CITY_MAP.put("7107", "嘉义市");
            CITY_MAP.put("7190", "省直辖");
            CITY_MAP.put("8100", "香港特别行政区");
            CITY_MAP.put("8101", "香港岛");
            CITY_MAP.put("8102", "九龙");
            CITY_MAP.put("8103", "新界");
            CITY_MAP.put("8200", "澳门特别行政区");
            CITY_MAP.put("8201", "澳门半岛");
            CITY_MAP.put("8202", "澳门离岛");
            CITY_MAP.put("8203", "无堂区划分区域");
        }
    }
}
