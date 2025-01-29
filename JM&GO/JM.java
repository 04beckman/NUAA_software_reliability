package softwarereality;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JM {
    // 使用List存储数据T[i]，用于记录失效发生的时间
    ArrayList<Double> T = new ArrayList<Double>();
    // 使用List存储预测结果
    ArrayList<Double> F = new ArrayList<Double>();
    double N = 0; // 估计的故障数
    double Fi = 0; // 失效率参数
    double ex = 0.001; // x的误差精度控制值
    double ey = 0.001; // y的误差精度控制值

    // 从文本文件中读取数据并填充T列表
    public void setT(JM jm) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("jm.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double temp = 0;
        jm.T.add(0.0); // 添加初始值 0
        while (line != null) {
            String[] numbers = line.split("\\s+");
            temp += Double.valueOf(numbers[1]); // 计算累计失效时间
            jm.T.add(temp);
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 计算P的值，用于后续计算N
    public double getP(JM jm) {
        double result = 0;
        double sum = 0;
        int n = jm.T.size() - 1;
        for (int i = 1; i <= n; i++) {
            sum += (i - 1) * (jm.T.get(i) - jm.T.get(i - 1));
        }
        result = sum / jm.T.get(n); // 计算P值
        return result;
    }

    // 计算函数f(N)
    public double fun(JM jm, double k, double P) {
        double result = 0;
        double sum = 0;
        int n = jm.T.size() - 1;
        for (int i = 1; i <= n; i++) {
            sum += (1 / (k - i + 1));
        }
        result = sum - n / (k - P);
        return result;
    }

    // 计算N的值，使用二分法进行迭代求解
    public void setN(JM jm) {
        int n = jm.T.size() - 1;
        double left = 0;
        double right = 0;
        double root = 0;

        double P = jm.getP(jm);
        System.out.println("P 值: " + P);

        // 步骤1：根据P的值来初始化左右边界
        if (P > (n - 1) / 2) {
            left = n - 1;
            right = n;
        } else {
            return; // 如果P值不满足条件，直接结束计算
        }

        // 步骤2：寻找满足条件的初始right值
        double f_right = jm.fun(jm, right, P);
        while (f_right > jm.ey) {
            left = right;
            right = right + 1;
            f_right = jm.fun(jm, right, P);
        }
        if (-jm.ex <= f_right) {
            root = right;
            jm.N = root; // 找到合适的N值
            return;
        } else {
            while (true) {
                // 步骤3：二分查找确定root值
                if (Math.abs(right - left) < jm.ex) {
                    root = (right + left) / 2;
                    break; // 满足精度要求，退出循环
                }
                if (Math.abs(right - left) > jm.ex) {
                    root = (right + left) / 2;
                }

                // 步骤4：更新左右边界
                double f_root = jm.fun(jm, root, P);
                if (f_root > jm.ey) {
                    left = root;
                    continue;
                }
                if (-jm.ey <= f_root && f_root <= jm.ey) {
                    jm.N = root; // 找到合适的N值
                    break;
                }
                if (f_root < -jm.ey) {
                    right = root;
                    continue;
                }
            }
        }
        jm.N = root; // 最终确定的N值
        return;
    }

    // 计算失效率参数Φ
    public void setFi(JM jm) {
        double sum = 0;
        int n = jm.T.size() - 1;
        for (int i = 1; i <= n; i++) {
            sum += (i - 1) * (jm.T.get(i) - jm.T.get(i - 1));
        }
        jm.Fi = n / (jm.N * jm.T.get(n) - sum); // 计算Φ
    }

    // 计算F值并存储在列表中
    public void setF(JM jm) {
        int n = jm.T.size();
        int k = 10; // 计算前10个预测结果
        double index = 0;
        double result = 0;
        int j = n - 1;
        for (int i = 0; i < k; i++) {
            index = -jm.Fi * (jm.N - j) * jm.T.get(j);
            j--;
            result = 1 - Math.exp(index); // 计算预测结果
            jm.F.add(i, result);
        }
        System.out.println("F 数组大小: " + jm.F.size());
        for (int i = 0; i < k; i++) {
            System.out.println(jm.F.get(i));
        }
        return;
    }

    // 主程序
    public static void main(String[] args) {
        JM jm = new JM();
        jm.setT(jm); // 设置失效时间列表
        jm.setN(jm); // 计算N的值
        jm.setFi(jm); // 计算失效率参数Φ
        // jm.setF(jm); // 计算F值（如果需要）
        System.out.println("当 ex = " + jm.ex + ", ey = " + jm.ey);
        System.out.println("N = " + jm.N);
        System.out.println("Φ = " + jm.Fi);
    }
}

// https://www.doc88.com/p-2199648218197.html
