本项目代码围绕**云边端混合架构中的工作负载任务协同调度**展开。随着云计算、边缘计算和终端设备的快速发展，云边端协同架构成为提高计算效率、降低延迟、优化资源利用的重要手段。在这种架构下，工作负载任务的调度需要在云端、边缘节点和终端设备之间高效分配，以平衡计算能力、网络延迟和数据传输成本。本项目通过计算 Power Diagram，结合网络模拟与延迟测量，提供了一种优化云边端架构下任务分配和调度的解决方案。

项目主要包括两部分：

1. **Java 项目代码**：用于计算 Power Diagram，确定每个数据项的索引节点，以优化数据分布与任务调度。
2. **mininet + P4 模拟代码**：基于 Java 计算结果，模拟网络通信，测量任务调度过程中不同节点间的延迟，为协同调度提供优化依据。

------

## Java 项目代码

此部分是一个基于 Maven 的项目，导入后即可运行。

Java 代码负责从 Python 生成的雾节点坐标中计算 Power Diagram，进而确定每个数据项的任务应分配到哪个节点进行处理。代码中的 `powerDiagram.java` 类通过 `main()` 方法实现索引节点计算，`src` 和 `dst` 变量分别表示处理前后的数据集地址。

------

## mininet + P4 模拟

模拟环境搭建参考 [P4Tutorial](https://github.com/davidcawork/P4Tutorial)，建议先阅读官方教程的前两节以掌握基础内容。

为便捷起见，建议使用官方提供的虚拟机环境。项目运行时，以项目目录为根目录进行操作，具体步骤如下：

1. 将 `paper` 文件夹复制到 `./exercises/` 目录下。
2. 将 `utils` 文件夹中的 `Makefile` 和 `run_paper.py` 文件复制到 `./utils/` 目录下。
3. 进入 `paper` 目录，使用 `make` 命令编译并运行实验，实验结束后可使用 `make clean` 清理生成文件。

### `paper` 目录文件说明

- **dataset**：包含实验使用的数据集。
- **out**：存储实验结果，包含 mininet 中各主机的命令行输出。
- **advanced_tunnel.p4**：P4 程序文件，控制数据包依据目标节点 id (dst_id) 进行路由。
- **my_controller.py**：控制平面程序，用于定义交换机的转发规则。
- **server.py**：多线程 socket 服务端脚本，运行在雾节点上，用于记录数据索引。运行时需指定监听的 IP 地址（-i）和端口（-p）。
- **client.py**：多线程 socket 客户端脚本，用于发起任务请求。运行时需指定客户端 IP 地址（-i）、端口（-p）、操作类型（-a，visit/generate），以及索引坐标（-c）和目标节点（-host）。
- **Node.py**：Chord 协议的简单实现，用于模拟分布式系统中的节点查找与索引任务分配。
- **generate.py**：用于生成数据索引的实验脚本，通过调用 `client.py` 向各节点的服务端发出索引写入请求。
- **Makefile**：调用 `./utils/Makefile` 对程序进行编译。
- **topology.json**：定义 mininet 网络拓扑，包括主机、交换机、链路及相关参数。

### 运行脚本 `run_paper.py`

`./utils/` 目录下的 `run_paper.py` 运行脚本是项目的核心之一，在 `./utils/Makefile` 中可以看到该脚本作为主运行脚本调用。可以通过修改此脚本来进行个性化实验配置，结合 mininet API 实现复杂的网络拓扑与任务调度模拟。在函数 `ExerciseRunner.run_exercise()` 中，可以调用 `my_controller.py` 写入交换机的转发规则，并通过 `self.generate_data()` 和 `self.visit_data()` 模拟数据生成与访问任务的协同调度过程。