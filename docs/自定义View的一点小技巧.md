自定义View的一些小知识

### 1.自定义View的基本知识

**(1.1) 自定义View的一般步骤**

一般来说，自定义View往往有以下两个步骤：

1.创建一个继承自View或者某个具体View组件的类，在其中完成自定义View的主要功能；

2.创建一个 `attrs.xml` 文件，在其中声明这个自定义View支持的一些配置属性。



**(1.2) 自定义View的构造方法**

在自定义View类中一般会声明三个构造方法，构造方法的写法依据个人喜好往往有下面两种风格👇 (注：Android源码中使用的是类似右边👉这种形式)

![img](customview_constructors.png)

如果是调用 `new NumberTextView(context)` 那么调用第一个构造方法；

如果是在XML中配置View的话，那么调用第二个构造方法，将配置的属性值作为第二个参数attrs传进去。

那第三个构造方法呢？你可能会猜想如果在XML配置的时候设置了style属性是不是就是调用第三个构造方法呢？其实不然，第三个构造方法不会被系统调用的，要由我们来显式调用，例如上面右边的风格中第二个构造方法就是直接去调用第三个构造方法，并且传入的第三个参数是0，后面我们会详细介绍这个参数。对于在XML中配置了style属性的情况，实际调用的还是第二个构造方法，此时相当于将style中定义的属性和属性值注入到当前的View的属性键值对中。



**(1.3) 获取自定义View的属性值**

构造方法中一般要做的事情就是获取配置给自定义View的属性值，然后初始化自定义View中的某些成员变量，同时创建一些和绘制有关的对象，例如Paint等。下面是获取属性值的常用方法：

`TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.xxx);`

调用 `obtainStyledAttribute` 方法得到的属性值就保存在 `TypedArray` 中，`attributes.length()` 的值就是得到的属性值个数，`attributes.getString(index)` 得到的就是第index个属性的属性值。

第一个参数 `attrs` 就是在XML中给View设置的属性，attrs中属性的顺序和XML中设置属性时的顺序可能不同，`attrs.getAttributeCount()` 的值就是设置的属性的个数，例如下面的例子就定义了5个属性：

```
<me.javayhu.lib.NumberTextView
    android:id="@+id/numberTextView"
    style="@style/NumberTextViewStyle"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="100dp" />
```

第二个参数`R.styleable.xxx` 就是我们自定义View声明的一些属性，一般写在 `attrs.xml` 文件中，标签为`declare-styleable`，例如下面的例子声明了3个属性：

```
<declare-styleable name="NumberTextView">
    <attr name="count" format="integer" />
    <attr name="duration" format="integer" />
    <attr name="textColor" format="color" />
    <attr name="textSize" format="dimension" />
</declare-styleable>
```



### 2.自定义View知识进阶

前面我们回顾了自定义View的基本知识，但是**如果我们希望自定义View在不同的主题下面会有不同的显示效果该怎么办？**对于这样的需求，我们就不得不深入了解下自定义View中的一些细节。

**(2.1) 参数最全的获取View属性值的方法**

`public TypedArray obtainStyledAttributes (AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes)`

第三个参数`defStyleAttr`：这个是当前Theme中的一个attribute，是指向style的一个引用，当在布局xml和style中都没有为View指定属性时，则会从Theme中这个attribute指向的Style中查找相应的属性值，这就是defStyle的意思，如果没有指定属性值，就用这个值，所以是默认值，但这个attribute要在Theme中指定，且是指向一个Style的引用，如果这个参数传入0表示不向Theme中搜索默认值。

第四个参数`defStyleRes`：这个也是指向一个Style的资源ID，但是仅在defStyleAttr为0或defStyleAttr不为0但Theme中没有为defStyleAttr属性赋值时起作用。

前面两个描述是不是看起来云里雾里的，具体详情可以参考[Android中自定义样式与View的构造函数中的第三个参数defStyle的意义](http://www.cnblogs.com/angeldevil/p/3479431.html)这篇文章。



**(2.2) 属性值的优先级高低**

**布局XML文件 > XML中指定的style > Theme中指定的style > 默认指定的style**











1.自定义View可以使用android系统原生提供的属性名

2.自定义View的属性值如果是枚举类型，那么需要在属性声明的地方列举出来

3.自定义View的默认属性值可以在style中声明出来 (以便在不同的theme中使用)

4.自定义View尽可能对编辑模式友好

5.自定义View的数据保存和恢复



第一版本

https://github.com/hujiaweibujidao/NumberTextView/tree/v1.0



第一版本实现了NumberTextView的基本功能，它支持配置其中的4个属性并对每个属性提供了默认值，同时对外提供了加一和减一的便捷操作方法。



第二版本



从使用者的角度思考，如何改进自定义View的使用，如何更加方便使用



第三版本



提供默认的样式和属性值，支持不同的theme











参考文档：

1.[Android中自定义样式与View的构造函数中的第三个参数defStyle的意义](http://www.cnblogs.com/angeldevil/p/3479431.html)

2.

