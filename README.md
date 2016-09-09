# XFrag
XFrag使用简便、功能强大的Fragment管理器

#XFrag简介

##版本号：1.0.1 测试版

###XFrag https://github.com/zkbutt
###XFrag 采用函数式链式调用，即可轻松管理应用中的所有Fragment
###XFrag 帮忙我们轻松应对主流中一个Activity多个Fragment情况
###XFrag 与APP完全解藕,轻松集成到任何一个APP中
###XFrag 能够方便的在启动各个Fragment时传blund数据
###XFrag 集成默认动画,能够方便的在定义并应用各种切换动画
###XFrag 支持返回键自动按顺序回退Fragment

#XFrag方法函数定义
###静态链式启动方法-使用XFrag.open 开启链式调用
	例：
	XFrag.close(fragment1).open(fragment2).show(fragment3).lock(false).execute();
1. open(fragment) //打开指定fragment
2. close(fragment) //关闭指定fragment
 
###静态独立方法-不能链式调用只能单独执行
	例：
	XFrag.addListner(new ExecuteListner() {
                    @Override
                    public void onExecutePre(Request request) {
                        //命令有效准备执行时 调用
                    }

                    @Override
                    public void onExecuteComplete(Request request) {
                        //命令执行完成时 调用
                    }
                });
1. initXFrag(activity) //在简单使用时，用于初始化 XFrag
2. removeListner(ExecuteListner listner) //移除侦听
3. addListner(ExecuteListner listner) //添加侦听
4. setAnim(addIn, addOut, quitOut, quitIn,animTime) //动画资源文件 开启动画
6. setAnim(true) //开启默认动画 或关闭动画
7. unlock(fragment) //单独解锁fragment
8. onBackPressed //拦截返回键 实现自动回退 用于activity对应绑定
9. save //保存锁定和隐藏状态 用于activity对应绑定
10. restore //恢复锁定和隐藏状态 用于activity对应绑定
11. onDestroy //销毁时XFrag 用于activity对应绑定

###链式方法
例：
	XFrag.close(fragment1).open(fragment2).show(fragment3).lock(false).execute();
1. layout(fragLayoutId) //指定布局ID，用于指定Fragment显示在哪里
2. hide(fragment) //隐藏
3. bundle(bundle) //为打开的Fragment 传数据
4. mode(stackMode) //与打开activity一样指定Fragment 打开模式 4种STANDARD, SINGLE_TOP, SINGLE_TASK, SINGLE_INSTANCE
5. setAnim(addIn, addOut, quitIn, quitOut) //链式设置一次性请求动画 动画只在当前调用有效
6. addListner()//功能同 静态独立方法 
7. removeListner()//功能同 静态独立方法.
8. open(fragment) //功能同 静态链式启动方法
9. close（fragment）//功能同 静态链式启动方法

###链式结束方法-调用此方法才开始执行
1. execute() //
2. initOpen() //

#XFrag使用

在您的项目的build.gradle中，添加仓库

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

在您的应用的build.gradle中，添加依赖

	dependencies {
	        compile 'com.github.zkbutt:XFrag:v1.0.1'
	}



##使用XFrag通常有两种方式:

**一、智能模式：强烈推荐项目应用这种方法**

XFragActivity 具有以下功能 （当然您也可以复制关键代码到您项目）

	1、自动初始化XFrag并快速设置默认
	2、解决在重建时自动恢复Fragment原有显示状态
	3、解决内存泄漏
	4、智能回退Fragment

继承XFragActivity 并在Fragment的基类的onCreate方法中

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//重建时不销毁Fragment
    }

继承后建议将onCreate内容写到onCreateNow方法中
XFragActivity 三个抽象方法:

	protected abstract int getFrameLayoutResID();//指定默认空间

	protected abstract Class<? extends Fragment> getDefFragment();//定义初始Fragment

	public abstract void onCreateNow(Bundle savedInstanceState);//将你onCreate的内容写在这里

例如:

    @Override
    protected int getFrameLayoutResID() {
        return R.id.main_fl;
    }

    @Override
    protected Class<? extends Fragment> getDefFragment() {
        return MainFragment.class;
    }

如果您需要在onCreate 初始化多个 Fragment，一定要使用initopen
通过初始化打开的 Fragment XFrag会自己将其锁定lock，无法隐藏和关闭，如需关闭初始化的Fragment 必须在链式命令中调用lock(false)解锁!

    XFrag.close(fragment).lock(false).execute();

注意在使用锁定和解锁时，链式中如果出现多个fragment，将全部进行解锁，或锁定如：
	                
	XFrag.close(fragment1).open(fragment2).show(fragment3).lock(false).execute();



**二、简单模式：（不推荐）**
如果您只想简单使用XFrag 帮忙您打开、关闭、显示、隐藏等基本操作，可以在activity中调用initXFrag注册，该模式不能自动智能回退fragment，只能进行fragment等操作，适合于只Fragment比较固定且数据较少的情况.
	
	XFrag.initXFrag(this);

注意XFrag使用的是V4包的FragmentManager，因此activity只支持FragmentActivity。
初始化后，即可在任意地方进行调用。

	XFrag.open(fragment2).layoutID(R.id.fl_1).show(fragment3).lock(false).execute();

注意当XFrag注册后打开和关闭只能注册的activity中，如果应用需要销毁注册的 activity，需要在activity中调用，如下方法，否则会产生activity泄漏

	XFrag.onDestroy(this);


#动画设置注意事项

1. 动画时间超过500，建议在调用设置动画时，填入正确的延时。

	XFrag.setAnim(R.anim.left_in,
                        R.anim.left_out,
                        R.anim.right_in,
                        R.anim.right_out, 2000);

2. 如需使用动画建议在初始化Activity时开启设置。当启用动画后，再去关闭一个添加时没有动画的Fragment会产生异常

        XFrag.setAnim(true);

#关于作者---Fredre

	有任何建议或者使用中遇到问题或是其它什么都可以联系我！
	技术交流，idea分享，欢迎有志之士一起研究，共同创造！
	Email： zkbutt@qq.com
	QQ : 318740003
	QQ群 : 490770868
