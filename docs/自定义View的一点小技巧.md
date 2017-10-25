自定义View的一些小知识



本文主要介绍自定义View中我们比较容易忽视的一些小细节，下面内容我结合一个简单的自定义View的例子解释下我们可以如何让自定义View做得更好。这里的例子是类似即刻应用中点赞效果，这个效果主要实现了两个数字的变换效果，代码地址：[NumberTextView](https://github.com/hujiaweibujidao/NumberTextView/)



### 1.自定义View的基本知识

**(1.1) 自定义View的一般步骤**

一般来说，自定义View往往有以下两个步骤：

1.创建一个继承自View或者某个具体View的类，在其中完成自定义View的主要功能；

2.创建一个 `attrs.xml` 文件，在其中声明这个自定义View支持的配置属性。



**(1.2) 自定义View的构造方法**

在自定义View类中一般会声明三个构造方法，构造方法的写法依据个人喜好往往有下面两种风格👇：一种是先调用super方法，然后去调用初始化方法；另一种是调用参数更多的那个方法，只在参数最全的那个方法中调用初始化方法。(注：Android源码中使用的是类似右边这种形式👉)

![img](customview_constructors.png)

如果是调用 `new NumberTextView(context)` 创建View的话，那么调用的是第一个构造方法；

如果是在XML中配置而创建View的话，那么调用的是第二个构造方法；

那第三个构造方法是什么情况下被调用呢？也许你猜想如果在XML配置的时候设置了style属性是不是就是调用第三个构造方法呢？其实不然，这种情况下调用的还是第二个构造方法，此时相当于将style中定义的属性和属性值拆出来注入到当前的View的属性键值对中。实际上第三个构造方法一般是不会被系统调用的，而是要我们来显式调用，例如上面右边的风格中第二个构造方法就是直接去调用第三个构造方法，并且传入的第三个参数是0，后面我们会详细介绍这个参数。



**(1.3) 获取自定义View的属性值**

一般构造方法中要做的事情就是获取配置给自定义View的属性值，然后初始化自定义View中的某些成员变量，同时创建一些和绘制有关的对象，例如Paint等。下面是获取属性值的常用方法，得到的属性值就保存在 `TypedArray attributes` 中，`attributes.length()` 的值就是得到的属性值个数，`attributes.getString(index)` 就是第 `index` 个属性的属性值。

`TypedArray attributes = context.obtainStyledAttributes(AttributeSet set, int[] attrs);`

第一个参数 `set` 就是在XML中给View设置的属性，attrs中属性的顺序和XML中设置属性时的顺序可能不同，`set.getAttributeCount()` 的值就是设置的属性的个数，例如V1.0版本中(下图左下角)就包含了8个属性；

第二个参数`attrs` 就是我们自定义View声明的一些属性，一般传入的是 `R.styleable.xxx`，内容写在 `attrs.xml` 文件中，标签为`declare-styleable`，例如V1.0版本中(下图左上角)就声明了4个属性。

利用上面👆的知识我们很快可以写出即刻应用中点赞时数字变换的效果，参考代码：[v1.0](https://github.com/hujiaweibujidao/NumberTextView/tree/v1.0)，主要代码如下

![img](customview_v1.png)



**(1.4) 关于属性的声明**

NumberTextView有四个属性，其中 `textSize` 和 `textColor` 两个属性很类似TextView的那两个属性，那我们可不可以直接用 `android:textSize` 和 `android:textColor` 呢？答案是可以的！

此外，有些时候我们希望我们自定义View的某个属性的属性值是个数量有限易于穷举的范围，已这里的动画时长属性 `duration` 为例，可能使用我们这个自定义View的开发者并不知道设置多大的时长比较合适，怎么办呢？这里我们可以考虑只给开发者提供几个值，例如Android系统原生配置的短动画时长 200ms、中等动画时长 400ms 以及长动画时长 500ms。写法如下：

![img](customview_v2.png)

老实说这个改动其实无伤大雅，个人喜好而已，我在一个Google开源项目中看到的前面提到的第一点的改动，也许可能是个最佳实践吧，这样我们就得到[V2.0版本](https://github.com/hujiaweibujidao/NumberTextView/tree/v2.0)，下面我们在这个版本上再做些改进。



### 2.自定义View的知识进阶

前面我们回顾了自定义View的基本知识，并创建好了基础版本的NumberTextView。但是如果现在我有这么个需求，我不想每次使用这个组件的时候都要去设置它的字体颜色、字体大小还有动画时长等参数，我希望它在我不设置这些属性值的时候就能够很好的work呢？这很简单，之前的版本其实已经实现了这个功能，V1.0版本中右侧图片自定义View的实现中已经定义好了各个属性的默认值，只要修改这个默认值就可以了。

但是，这样真的好吗？改了这个Java类文件就要重新编译哟！此外，如果我们不能直接修改自定义View的实现文件怎么办？这里你肯定想到了用style！在XML文件中



 **如果我们希望自定义View在不同的主题下面会有不同的显示效果该怎么办？** 对于这样的需求，我们就不得不深入了解下自定义View中的一些细节。

**(3.1) 参数最全的获取View属性值的方法**

`public TypedArray obtainStyledAttributes (AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes)`

第三个参数`defStyleAttr`：这个是当前Theme中的一个attribute，是指向style的一个引用，当在布局xml和style中都没有为View指定属性时，则会从Theme中这个attribute指向的Style中查找相应的属性值，这就是defStyle的意思，如果没有指定属性值，就用这个值，所以是默认值，但这个attribute要在Theme中指定，且是指向一个Style的引用，如果这个参数传入0表示不向Theme中搜索默认值。

第四个参数`defStyleRes`：这个也是指向一个Style的资源ID，但是仅在defStyleAttr为0或defStyleAttr不为0但Theme中没有为defStyleAttr属性赋值时起作用。

前面两个描述是不是看起来云里雾里的，具体详情可以参考[Android中自定义样式与View的构造函数中的第三个参数defStyle的意义](http://www.cnblogs.com/angeldevil/p/3479431.html)这篇文章。



**(3.2) 属性值的优先级高低**

**布局XML文件 > XML中指定的style > Theme中指定的style > 默认指定的style**











1.自定义View可以使用android系统原生提供的属性名

2.自定义View的属性值如果是枚举类型，那么需要在属性声明的地方列举出来

3.自定义View的默认属性值可以在style中声明出来 (以便在不同的theme中使用)

4.自定义View尽可能对编辑模式友好

5.自定义View的数据保存和恢复





参考文档：

1.[Android中自定义样式与View的构造函数中的第三个参数defStyle的意义](http://www.cnblogs.com/angeldevil/p/3479431.html)

2.

