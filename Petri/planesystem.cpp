#include <bits/stdc++.h>

// Petri网库所和变迁的表示
struct Node
{
    std::string name;
    double reliability1; // 节点可靠度
    double reliability2; // 连接件可靠度
    double probability;  // 转移概率
};

// 定义边
struct Edge
{
    std::string from, to;
};
std::map<std::string, Node> dic; // 名称与点的映射
// Petri网类
class PetriNet
{
public:
    std::vector<Node> nodes;                                       // 点集
    std::vector<Edge> edges;                                       // 边集
    std::map<std::string, std::vector<std::string>> adjacencyList; // 邻接矩阵
    std::map<std::string, int> vis;                                // BFS访问记录
    std::vector<std::vector<Node>> paths;                          // 路径
    std::vector<double> proofpath;                                 // 路径转移概率
    std::vector<double> reofpath;                                  // 路径可靠度

    void addNode(std::string name, double reliability1, double reliability2, double probability)
    {
        // name代表node名称 reliability1表示库所或变迁可靠性 reliability1连接件可靠性 probability表示转移概率
        if (name[0] == 'C')
        {
            nodes.push_back({name, reliability1, 1, 1});
            dic[name] = nodes.back();
        }
        else
        {
            nodes.push_back({name, reliability1, reliability2, probability});
            dic[name] = nodes.back();
        }
    }

    void addEdge(std::string from, std::string to)
    {
        adjacencyList[from].push_back(to);
    }

    // BFS算法生成所有路径
    void generatePaths(Node start, Node end)
    {
        std::vector<Node> s = {start};
        std::queue<std::vector<Node>> q;
        q.push(s);
        while (q.size())
        {
            auto t = q.front();
            q.pop();
            auto node = t.back();
            if (node.name == end.name)
            {
                paths.push_back(t);
                continue;
            }
            for (auto &x : adjacencyList[node.name])
            {
                if (x != end.name && vis[x] >= 2)
                    continue;
                vis[x]++;
                std::vector<Node> nn = t;
                nn.push_back(dic[x]);
                q.emplace(nn);
            }
        }
    }
    void showPaths()
    {
        std::cout << "利用BFS算法可计算出从S到EN的路径有:" << std::endl;
        for (int i = 0; i < paths.size(); ++i)
        {
            std::cout << "S->";
            for (int j = 0; j < paths[i].size(); ++j)
            {
                std::cout << paths[i][j].name << "->";
            }
            std::cout << "EN" << std::endl;
        }
        std::cout << std::endl;
    }
    // 计算路径概率
    void CalculatePathProbability()
    {
        std::cout << "总共有" << paths.size() << "条测试路径,其迁移概率分别为:" << std::endl;
        int cnt = 0;
        for (auto &path : paths)
        {
            double pro = 1.0;
            for (auto &x : path)
                pro *= x.probability;
            std::cout << "路径" << cnt++ << "的迁移概率为" << pro << std::endl;
            proofpath.push_back(pro);
        }
        std::cout << std::endl;
    }
    // 计算路径可靠度
    void CalculatePathReliability()
    {
        std::cout << "总共有" << paths.size() << "条测试路径,其可靠度分别为:" << std::endl;
        int cnt = 0;
        for (auto &path : paths)
        {
            double pro = 1.0;
            for (auto &x : path)
                pro *= x.reliability1 * x.reliability2;
            std::cout << "路径" << cnt++ << "的可靠度为" << pro << std::endl;
            reofpath.push_back(pro);
        }
        std::cout << std::endl;
    }
    void CalculateSystemReliability()
    {
        double sum_Pro = 0;
        double sum_weighted_R = 0;
        for (int i = 0; i < paths.size(); ++i)
        {
            sum_Pro += proofpath[i];
            sum_weighted_R += proofpath[i] * reofpath[i];
        }
        std::cout << "计算该系统SA的可靠度为:";
        std::cout << sum_weighted_R / sum_Pro << std::endl;
    }
};

int main(void)
{
    PetriNet Net;
    Net.addNode("C1", 1, 1, 1);
    Net.addNode("C2", 0.99, 1, 1);
    Net.addNode("C3", 0.98, 1, 1);
    Net.addNode("C4", 1, 1, 1);
    Net.addNode("C5", 0.99, 1, 1);
    Net.addNode("C6", 0.99, 1, 1);
    Net.addNode("C7", 1, 1, 1);
    Net.addNode("C8", 0.98, 1, 1);
    Net.addNode("C9", 1, 1, 1);
    Net.addNode("T1", 1, 0.99, 1);
    Net.addNode("T2", 0.99, 1, 0.99);
    Net.addNode("T3", 1, 1, 0.98);
    Net.addNode("T4", 0.98, 0.98, 0.80);
    Net.addNode("T5", 0.99, 1, 1.0);
    Net.addNode("T6", 1, 0.99, 1.0);
    Net.addNode("T7", 0.98, 0.99, 0.30);
    Net.addNode("T8", 0.98, 1, 0.98);
    Net.addNode("T9", 0.99, 0.98, 0.98);
    Net.addNode("T10", 1, 1, 0.20);
    Net.addEdge("C1", "T1");
    Net.addEdge("T1", "C2");
    Net.addEdge("C2", "T2");
    Net.addEdge("T2", "C3");
    Net.addEdge("C3", "T3");
    Net.addEdge("T3", "C4");
    Net.addEdge("C4", "T4");
    Net.addEdge("C4", "T10");
    Net.addEdge("T10", "C9");
    Net.addEdge("T4", "C5");
    Net.addEdge("C5", "T5");
    Net.addEdge("T5", "C6");
    Net.addEdge("C6", "T6");
    Net.addEdge("T6", "C7");
    Net.addEdge("C7", "T9");
    Net.addEdge("T9", "C9");
    Net.addEdge("C7", "T7");
    Net.addEdge("T7", "C8");
    Net.addEdge("C8", "T8");
    Net.addEdge("T8", "C2");
    Net.generatePaths(dic["C1"], dic["C9"]);
    Net.showPaths();
    Net.CalculatePathProbability();
    Net.CalculatePathReliability();
    Net.CalculateSystemReliability();
    system("pause");
    return 0;
}
