# MyQQ

> 作者：赵文亮
>
> 学号：2016011452
>
> E-main: zhaowl16@mails.tsinghua.edu.cn

## 程序运行方式

- 运行环境：Android 5.0-Android 9.0

- 运行方式：下载 app/release/MyQQ.apk 到手机即可

  也可以从www.johnwilliams.online/MyQQ.apk 下载

## 目录结构

- app/ ：
  - build/ 构建生成的文件
  - release/ 发布文件
  - libs/ 第三方库
  - src/ 
    - androidTest/, test/ 测试文件
    - main/ 用户文件
      - AndroidManifest.xml：清单文件，包括Activity声明、权限等等
      - java/
        - com.johnwilliams.qq/
          - Activities/
            - ChatActivity.java：聊天界面
            - CreateGroupActivity.java：新建群聊界面
            - LoginActivity.java：登录界面
            - MainActivity.java：主界面
          - fragments/ 
            - ChatFragment.java：最近消息标签页
            - ContactFragment.java：联系人标签页
            - SettingFragment.java：设置标签页
            - MyFragment.java：上述三个Fragment的基类
          - Adapters/：各种ListView、RecyclerView中的Adapter
          - lib/：界面相关的参考程序
          - tools/:
            - Chat/：最近消息相关类
            - Contact/：联系人相关类
            - Connection/：
              - ConnectionTool：建立连接的基类，与中央服务器通信
              - MessageSender.java：发送消息类，继承ConnectionTool
              - MessageReceiver.java：接收消息类
              - ServerWorkerRunnable.java：用于处理每次连接，在MessageReceiver中作为子线程调用
            - Listeners/：列表中Click和LongClick的Listener
            - Message/：聊天消息类
            - Utils/：一些通用工具类
      - res/ 资源文件
- gradle/、gradle*：gradle的相关文件
- report.pdf：实验报告
- README.md：说明文档
- demo/MyQQ_Demo.mp4：demo视频


