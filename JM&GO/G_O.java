package softwarereality;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class G_O {
    ArrayList<Double> T = new ArrayList<Double>(); // 存储失效时间
    double D = 0.0; // 参数D
    double f = 0.0; // 函数f的结果
    double xl = 0.0; // 左边界
    double xr = 0.0; // 右边界
    double xm = 0.0; // 中点
    double b = 0.0; // 参数b
    double a = 0.0; // 参数a
    double v = 1.0E-1; // 精度控制

    public G_O() {
    }

    // 从文件中读取时间数据并填充到T列表中
    public void setT(G_O g_o) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("jm.txt"));
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        }

        double temp = 0.0;
        String line = null;

        try {
            line = br.readLine();
        } catch (IOException var12) {
            var12.printStackTrace();
        }

        // 读取第一个时间数据
        String[] T0 = line.split("\\s+");
        temp = Double.valueOf(T0[1]);
        g_o.T.add(temp);

        try {
            line = br.readLine();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        // 读取剩余的数据
        while (line != null) {
            String[] numbers = line.split("\\s+");
            temp += Double.valueOf(numbers[1]); // 累计时间
            g_o.T.add(temp);

            try {
                line = br.readLine();
            } catch (IOException var10) {
                var10.printStackTrace();
            }
        }

        try {
            br.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }
    }

    // 计算D值
    public double getD(G_O g_o) {
        double result = 0.0;
        double sum = 0.0;
        int n = g_o.T.size();

        // 累计失效时间求和
        for (int i = 0; i < n; ++i) {
            sum += g_o.T.get(i);
        }

        result = sum / (n * g_o.T.get(n - 1)); // 计算D
        return result;
    }

    // 计算函数f，用于判断中点xm是否满足条件
    public double getF(G_O g_o, double D) {
        double p1 = (1.0 - D * g_o.xm) * Math.pow(Math.E, g_o.xm);
        double p2 = (D - 1.0) * g_o.xm - 1.0;
        double result = p1 + p2;
        return result;
    }

    // 计算参数b
    public double getb(G_O g_o) {
        int n = g_o.T.size();
        double result = g_o.xm / g_o.T.get(n - 1);
        return result;
    }

    // 计算参数a
    public double geta(G_O g_o) {
        int n = g_o.T.size();
        double temp2 = -this.b * g_o.T.get(n - 1);
        double temp1 = Math.pow(Math.E, temp2);
        double result = n / (1.0 - temp1);
        return result;
    }

    public static void main(String[] args) {
        G_O g_o = new G_O();
        g_o.setT(g_o); // 读取失效时间数据
        g_o.D = g_o.getD(g_o); // 计算D值

        // 如果D不在合适范围内，输出无解
        if (g_o.D >= 0.5) {
            System.out.println("参数估计无解" + g_o.D);
        } else {
            // 确定初始的左边界xl和右边界xr
            if (g_o.D > 0.0 && g_o.D < 0.5) {
                g_o.xl = (1.0 - 2.0 * g_o.D) / 2.0;
                g_o.xr = 1.0 / g_o.D;
            }

            // 二分法迭代逼近xm
            while (Math.abs(g_o.xr - g_o.xl) > g_o.v) {
                g_o.xm = (g_o.xr + g_o.xl) / 2.0;
                g_o.f = g_o.getF(g_o, g_o.D);
                if (g_o.f > g_o.v) {
                    g_o.xl = g_o.xm; // 更新左边界
                } else {
                    if (!(g_o.f < -g_o.v)) {
                        break; // 满足条件，退出迭代
                    }
                    g_o.xr = g_o.xm; // 更新右边界
                }
            }

            // 计算参数b和a
            g_o.b = g_o.getb(g_o);
            g_o.a = g_o.geta(g_o);
            System.out.println("b= " + g_o.b);
            System.out.println("a= " + g_o.a);
        }
    }
}