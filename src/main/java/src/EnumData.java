package src;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package src
 * @Description:
 * @date 2017/7/26 15:58
 */
public enum EnumData {

    ENUM1("ENUM1", "第一个enum", 1),
    ENUM2("ENUM2", "第二个enum", 2),;

    public String val;
    public String desc;
    public int order;

    EnumData(String val, String desc, int order) {
        this.val = val;
        this.desc = desc;
        this.order = order;
    }



}
