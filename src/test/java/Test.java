import src.EnumData;
import src.Jlog;
import src.RDate;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package PACKAGE_NAME
 * @Description:
 * @date 2017/7/26 14:56
 */
public class Test {

    public static void main(String[] args) {
        String nowTime = RDate.getNowStrTime();
        System.out.println(nowTime);
        System.out.println(RDate.getLastDayBegin());
        System.out.println(RDate.getWeekBeginString());

        String desc = EnumData.ENUM2.desc;
        System.out.println("enum desc:" + desc);
        Jlog.info(EnumData.ENUM2.order);

    }

}
