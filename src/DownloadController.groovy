import javax.servlet.http.HttpServletResponse

public class DownloadController {
    private static String path = "c:/cokeFile/"

    public void readProperty() {
        try {
            HttpServletResponse response
            Properties props = new Properties()
            InputStream inputStream = this.class.classLoader.getResourceAsStream("./properties/download.properties")

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")

            // 加载配置文件
            props.load(inputStreamReader)
            inputStream.close()

            String name = "fileName"
            String fileName = props.getProperty(name)

            if (fileName == null) throw new Exception("Cannot find corresponding file !")

            // 读到流中
            InputStream inStream = new FileInputStream(path + fileName) // 文件的存放路径

            // 循环取出流中的数据
            byte[] b = new byte[100]
            int len
            try {
                while ((len = inStream.read(b)) > 0)
                    response.getOutputStream().write(b, 0, len)
                inStream.close()
            } catch (IOException e) {
                e.printStackTrace()
            }


        } catch (Exception e) {

        }

    }

}
