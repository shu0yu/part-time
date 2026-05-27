package com.example.bishe_demo.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 模拟数据生成器工具类
 * 用于生成符合业务场景的高质量模拟数据
 */
public class MockDataGenerator {

    private static final Random random = new Random();

    // 常用中文姓氏
    private static final String[] SURNAMES = {
        "张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴",
        "徐", "孙", "马", "朱", "胡", "郭", "何", "林", "高", "罗",
        "郑", "梁", "谢", "宋", "唐", "许", "韩", "冯", "邓", "曹"
    };

    // 常用中文名字
    private static final String[] NAMES = {
        "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
        "勇", "艳", "杰", "涛", "明", "超", "秀英", "华", "平", "刚",
        "桂英", "文", "辉", "玲", "斌", "波", "宇", "浩", "凯", "健"
    };

    // 学校名称
    private static final String[] SCHOOLS = {
        "北京理工大学", "清华大学", "北京大学", "中国人民大学", "北京航空航天大学",
        "复旦大学", "上海交通大学", "同济大学", "华东师范大学", "上海大学",
        "浙江大学", "南京大学", "东南大学", "武汉大学", "华中科技大学",
        "中山大学", "华南理工大学", "暨南大学", "深圳大学", "四川大学"
    };

    // 专业名称
    private static final String[] MAJORS = {
        "计算机科学与技术", "软件工程", "电子信息工程", "通信工程", "机械工程",
        "土木工程", "建筑学", "市场营销", "会计学", "金融学",
        "国际经济与贸易", "法学", "新闻学", "广告学", "汉语言文学",
        "英语", "数学与应用数学", "物理学", "化学工程", "生物科学"
    };

    // 年级
    private static final String[] GRADES = {
        "大一", "大二", "大三", "大四", "研一", "研二", "研三"
    };

    // 企业名称
    private static final String[] COMPANIES = {
        "华为技术有限公司", "阿里巴巴集团", "腾讯科技有限公司", "百度在线网络技术有限公司",
        "字节跳动科技有限公司", "美团点评", "京东集团", "小米科技有限责任公司",
        "网易公司", "新浪公司", "搜狐公司", "滴滴出行科技有限公司",
        "携程计算机技术有限公司", "中国平安保险集团", "招商银行股份有限公司",
        "中国银行股份有限公司", "中国工商银行股份有限公司", "中国移动通信集团",
        "中国电信集团", "中国联通集团", "国家电网有限公司", "中国南方电网有限责任公司"
    };

    // 企业地址
    private static final String[] ADDRESSES = {
        "北京市海淀区中关村软件园", "北京市朝阳区望京SOHO", "深圳市南山区科技园",
        "上海市浦东新区张江高科技园区", "杭州市余杭区文一西路", "广州市天河区珠江新城",
        "南京市建邺区河西新城", "武汉市东湖新技术开发区", "成都市高新区天府大道",
        "西安市雁塔区科技路", "苏州市工业园区金鸡湖大道", "天津市滨海新区泰达开发区"
    };

    // 岗位名称
    private static final String[] JOB_NAMES = {
        "Java开发实习生", "Python开发工程师", "前端开发实习生", "后端开发实习生",
        "UI设计师", "产品经理实习生", "运营专员", "市场营销实习生",
        "数据分析师实习生", "测试工程师实习生", "算法实习生", "客服专员",
        "行政助理", "人力资源实习生", "财务助理", "文案策划",
        "家教老师（数学）", "家教老师（英语）", "家教老师（物理）", "促销专员",
        "快递分拣员", "餐厅服务员", "超市理货员", "图书馆管理员"
    };

    // 岗位类型
    private static final String[] JOB_TYPES = {
        "技术开发", "产品运营", "设计创意", "市场销售",
        "行政人事", "财务金融", "教育培训", "实习兼职"
    };

    // 工作时间
    private static final String[] WORK_TIMES = {
        "每周一至周五 9:00-18:00", "每周一至周五 10:00-19:00", "每周二至周四 14:00-18:00",
        "每周六至周日 9:00-18:00", "每周一、三、五 18:00-21:00", "每周六 10:00-17:00",
        "弹性工作时间", "每周二至周五 9:00-17:00", "每周一至周四 13:00-19:00"
    };

    // 工作地点
    private static final String[] WORK_ADDRESSES = {
        "北京市海淀区中关村大街1号", "北京市朝阳区建国路88号", "深圳市南山区深南大道9999号",
        "上海市浦东新区陆家嘴环路1000号", "杭州市余杭区文一西路969号", "广州市天河区天河路385号",
        "南京市建邺区河西大街1号", "武汉市武昌区中北路1号", "成都市武侯区天府大道北段1号",
        "西安市雁塔区高新路2号", "苏州市工业园区星海街1号", "天津市和平区南京路1号"
    };

    // 岗位描述
    private static final String[] JOB_DESCS = {
        "参与公司核心产品的开发工作，使用Java和Spring Boot框架，与优秀的工程师团队一起成长。",
        "负责产品前端页面的开发和维护，使用Vue.js或React框架，注重用户体验和代码质量。",
        "参与数据分析和处理工作，使用Python进行数据清洗、可视化和模型构建。",
        "协助产品经理进行需求分析、原型设计和产品规划，深入理解用户需求。",
        "负责公司产品的运营推广工作，包括内容运营、用户活动策划等。",
        "参与UI/UX设计工作，负责产品界面设计和用户体验优化。",
        "负责市场调研和竞品分析，协助制定市场营销策略。",
        "参与软件测试工作，编写测试用例，进行功能测试和回归测试。",
        "提供优质的客户服务，解答用户咨询，处理用户反馈和投诉。",
        "协助行政人事部门进行日常工作，包括文件整理、会议安排等。"
    };

    // 岗位要求
    private static final String[] JOB_REQUIRES = {
        "1. 本科及以上学历，计算机相关专业；2. 熟悉Java编程语言，了解Spring Boot框架；3. 有良好的团队协作能力和沟通能力。",
        "1. 熟悉HTML、CSS、JavaScript；2. 了解Vue.js或React框架；3. 有前端项目经验者优先。",
        "1. 熟悉Python编程语言；2. 了解数据分析相关工具（Pandas、NumPy等）；3. 数学基础扎实者优先。",
        "1. 对产品工作有热情；2. 具备良好的逻辑思维和沟通能力；3. 有产品相关经验者优先。",
        "1. 具备良好的文字功底和创意能力；2. 熟悉社交媒体平台运营；3. 有运营经验者优先。",
        "1. 熟练使用Photoshop、Sketch等设计工具；2. 有良好的审美能力；3. 设计相关专业优先。",
        "1. 具备市场分析能力；2. 良好的沟通协调能力；3. 市场营销相关专业优先。",
        "1. 了解软件测试流程；2. 熟悉测试用例编写；3. 计算机相关专业优先。",
        "1. 具备良好的服务意识和沟通能力；2. 普通话标准；3. 有客服经验者优先。",
        "1. 工作认真细致；2. 熟练使用Office办公软件；3. 有行政人事相关经验者优先。"
    };

    // 申请备注
    private static final String[] APPLY_REMARKS = {
        "对该岗位非常感兴趣，希望能有机会学习和成长！",
        "有相关实习经验，相信能够胜任这份工作。",
        "学习能力强，能够快速适应新环境。",
        "非常期待加入贵公司，贡献自己的力量。",
        "课余时间充裕，可以保证充足的工作时间。",
        "专业对口，对该领域有浓厚的兴趣。",
        "有良好的团队协作精神，善于沟通。",
        "希望通过这次实习积累实战经验。",
        "会认真对待每一项工作任务。",
        "期待与您进一步沟通！"
    };

    // 聊天消息内容
    private static final String[] CHAT_MESSAGES = {
        "您好，我对这个岗位很感兴趣，想了解更多信息。",
        "请问这个岗位的工作时间是怎样安排的？",
        "这个岗位的薪资待遇如何？",
        "我有相关的实习经验，能申请这个岗位吗？",
        "请问什么时候可以面试？",
        "感谢您的回复，我会准备好相关材料。",
        "这个岗位的主要工作职责是什么？",
        "请问需要具备哪些技能要求？",
        "我已经提交了申请，请问什么时候能有回复？",
        "好的，我会准时参加面试。",
        "请问工作地点具体在哪里？",
        "这个岗位是长期的还是短期的？",
        "我可以调整工作时间，请问有什么要求吗？",
        "感谢您给我这个机会！",
        "我对这个行业非常感兴趣，希望能深入学习。"
    };

    /**
     * 生成随机中文姓名
     */
    public static String generateChineseName() {
        String surname = SURNAMES[random.nextInt(SURNAMES.length)];
        String name = NAMES[random.nextInt(NAMES.length)];
        return surname + name;
    }

    /**
     * 生成随机手机号
     */
    public static String generatePhone() {
        String[] prefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                            "150", "151", "152", "153", "155", "156", "157", "158", "159",
                            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            suffix.append(random.nextInt(10));
        }
        return prefix + suffix.toString();
    }

    /**
     * 生成随机用户名
     */
    public static String generateUsername() {
        String[] prefixes = {"user", "student", "company", "admin", "test"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        int number = random.nextInt(10000);
        return prefix + String.format("%04d", number);
    }

    /**
     * 生成随机学校
     */
    public static String generateSchool() {
        return SCHOOLS[random.nextInt(SCHOOLS.length)];
    }

    /**
     * 生成随机专业
     */
    public static String generateMajor() {
        return MAJORS[random.nextInt(MAJORS.length)];
    }

    /**
     * 生成随机年级
     */
    public static String generateGrade() {
        return GRADES[random.nextInt(GRADES.length)];
    }

    /**
     * 生成随机企业名称
     */
    public static String generateCompanyName() {
        return COMPANIES[random.nextInt(COMPANIES.length)];
    }

    /**
     * 生成随机地址
     */
    public static String generateAddress() {
        return ADDRESSES[random.nextInt(ADDRESSES.length)];
    }

    /**
     * 生成随机岗位名称
     */
    public static String generateJobName() {
        return JOB_NAMES[random.nextInt(JOB_NAMES.length)];
    }

    /**
     * 生成随机岗位类型
     */
    public static String generateJobType() {
        return JOB_TYPES[random.nextInt(JOB_TYPES.length)];
    }

    /**
     * 生成随机薪资范围
     */
    public static BigDecimal[] generateSalaryRange() {
        int min = 100 + random.nextInt(400);
        int max = min + 50 + random.nextInt(200);
        return new BigDecimal[]{new BigDecimal(min), new BigDecimal(max)};
    }

    /**
     * 生成随机工作时间
     */
    public static String generateWorkTime() {
        return WORK_TIMES[random.nextInt(WORK_TIMES.length)];
    }

    /**
     * 生成随机工作地点
     */
    public static String generateWorkAddress() {
        return WORK_ADDRESSES[random.nextInt(WORK_ADDRESSES.length)];
    }

    /**
     * 生成随机岗位描述
     */
    public static String generateJobDesc() {
        return JOB_DESCS[random.nextInt(JOB_DESCS.length)];
    }

    /**
     * 生成随机岗位要求
     */
    public static String generateJobRequire() {
        return JOB_REQUIRES[random.nextInt(JOB_REQUIRES.length)];
    }

    /**
     * 生成随机申请备注
     */
    public static String generateApplyRemark() {
        return APPLY_REMARKS[random.nextInt(APPLY_REMARKS.length)];
    }

    /**
     * 生成随机聊天消息
     */
    public static String generateChatMessage() {
        return CHAT_MESSAGES[random.nextInt(CHAT_MESSAGES.length)];
    }

    /**
     * 生成随机状态
     */
    public static byte generateStatus() {
        return random.nextDouble() < 0.9 ? (byte) 1 : (byte) 0;
    }

    /**
     * 生成随机已读状态
     */
    public static byte generateIsRead() {
        return random.nextDouble() < 0.7 ? (byte) 1 : (byte) 0;
    }

    /**
     * 生成随机删除状态
     */
    public static byte generateIsDelete() {
        return random.nextDouble() < 0.95 ? (byte) 0 : (byte) 1;
    }

    /**
     * 从列表中随机选择一个元素
     */
    public static <T> T randomFromList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * 从数组中随机选择一个元素
     */
    public static <T> T randomFromArray(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[random.nextInt(array.length)];
    }

    /**
     * 生成指定范围内的随机整数
     */
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 生成指定范围内的随机长整数
     */
    public static long randomLong(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }
}
