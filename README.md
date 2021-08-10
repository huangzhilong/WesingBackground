# TMEFastBackground
在Android 开发中常用xml的shape标签来创建出各种背景形状，如：圆角、填充色、渐变色、边框等。其最终会被解析生成GradientDrawable对象。
App界面中随处可见GradientDrawable，每次app使用都会存在成千上万次的主线程调用getDrawable获取GradientDrawable。

通过编译期解析项目中定义的xml shape标签，生成java代码记录shape的对应属性值，使用时直接代码创建shape背景，避免原本的xml IO读取和解析的耗时操作。
大大提升运行速度。尤其是海外低端机比较普遍，低端机IO操作在内存不足、CPU高负荷会出现瓶颈，导致原本的耗时大大增加！！


# 接入方式
1、根目录build.gradle
	 “classpath "com.tme.wesing:fast_background_plugin:$fast_background_version"”

2、需要开启的module使用fastBackgroudPlugin插件
	apply plugin: 'com.tme.wesing.background’

3、接入FastBackgroundLib库
	implementation"com.tme.wesing:fast_background_lib:$fast_background_version"
	
最新稳定版本：1.0.8_release

# 数据提升
平均加载耗时优化效率： 10倍+

线上低端机数据对比：




