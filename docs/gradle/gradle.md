# 开发自定义Gradle插件


Gradle 插件包建立可重用的构建逻辑片段，它可以用于许多不同的项目和构建。Gradle允许你实现自己的插件，这样你就可以重用你的构建逻辑，并与他人共享。
你可以用任何你喜欢的语言实现Gradle插件，前提是实现最终被编译为JVM字节码。在我们的示例中，我们将使用Java作为独立插件项目的实现语言，并在buildscript插件示例中使用Groovy或Kotlin。通常，使用 Java 或 Kotlin 实现的静态类型的插件比使用 Groovy 实现的同一个插件性能更好。

---
### 打包一个插件  
有几个地方可以放置插件的源代码。

**Build script**  
您可以将插件的源代码直接包含在构建脚本中。这样做的好处是，插件可以自动编译并包含在构建脚本的类路径中，而无需执行任何操作。但是，该插件在构建脚本之外是不可见的，因此您不能在其中定义的构建脚本之外重用该插件。

**`buildSrc` project**  
您可以将插件的源代码放在  
`rootProjectDir/buildSrc/src/main/java` 文件夹 (或者  
`rootProjectDir/buildSrc/src/main/groovy`  或者  
`rootProjectDir/buildSrc/src/main/kotlin`  取决于您喜欢哪种语言）。

Gradle将负责编译和测试插件，并使其在构建脚本的类路径上可用。该插件对构建所使用的每个构建脚本都可见。但是，它在构建之外是不可见的，因此您不能在定义它的构建之外重用插件。
有关`buildSrc`项目的更多详细信息，请参见 [Organizing Gradle Projects](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#organizing_gradle_projects)。  

**Standalone project**  
你可以为你的插件创建一个单独的项目。这个项目生成并发布一个JAR，然后您可以在多个构建中使用它并与其他人共享。通常，这个JAR可能包括一些插件，或者是将几个相关的任务类捆绑到一个库中。或者是两者的某种组合。  
在我们的例子中，我们将从构建脚本中的插件开始，以保持简单。然后我们将着眼于创建一个独立的项目。  

### 编写一个简单的插件  
要创建Gradle插件，您需要编写一个实现`Plugin`接口的类。当插件应用于项目时，Gradle会创建插件类的一个实例，并调用该实例的plugin.apply()方法。项目对象作为参数传递，插件可以根据需要使用该参数来配置项目。以下示例包含一个greeting插件，它向项目添加了名称为hello的任务。

```
class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello") {
            doLast {
                println("Hello from the GreetingPlugin")
            }
        }
    }
}

// Apply the plugin
apply<GreetingPlugin>()
```  

**gradle -q hello** 的输出结果为  
```
> gradle -q hello
Hello from the GreetingPlugin
```
需要注意的一点是，每个应用插件的项目都会创建一个新的插件实例。还要注意，`Plugin`类是一个泛型类型。在本例中，它接收`Project`类型作为类型参数。插件可以接收`Settings`类型的参数，在这种情况下，插件可以在`Settings`脚本中应用，也可以是`Gradle`类型的参数，在这种情况下，可以在初始化脚本中应用插件。  

使插件可配置  
大多数插件为构建脚本和其他插件提供了一些配置选项，用于自定义插件的工作方式。插件通过使用扩展对象来实现这一点。`Gradle`项目有一个关联的`ExtensionContainer`对象，该对象包含已应用于该项目的插件的所有设置和属性。您可以通过向该容器添加扩展对象来提供插件的配置。扩展对象只是一个具有表示配置的Java Bean属性的对象。  
让我们为项目添加一个简单的扩展对象。在这里，我们向项目添加了一个`greeting`扩展对象，它允许您配置问候语。 


```
Kotlin方式  -> build.gradle.kts    

interface GreetingPluginExtension {
    val message: Property<String>
}

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'greeting' extension object
        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
        extension.message.convention("Hello from GreetingPlugin")
        // Add a task that uses configuration from the extension object
        project.task("hello") {
            doLast {
                println(extension.message.get())
            }
        }
    }
}

apply<GreetingPlugin>()

// Configure the extension
the<GreetingPluginExtension>().message = "Hi from Gradle"
```

**gradle -q hello** 的输出结果为  
```
> gradle -q hello
Hi from Gradle
```   
在示例中，`GreetingPluginExtension`是一个具有名为`message`属性的对象。扩展对象将添加到带有名称`greeting`的项目中。该对象作为项目属性可用，其名称与扩展对象相同。  
通常，您需要在单个插件上指定几个相关属性。Gradle为每个扩展对象添加一个配置块，这样您就可以将设置分组在一起。下面的示例向您展示了这是如何工作的。     

```  
Kotlin方式  -> build.gradle.kts    

interface GreetingPluginExtension {
    val message: Property<String>
    val greeter: Property<String>
}

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
        project.task("hello") {
            doLast {
                println("${extension.message.get()} from ${extension.greeter.get()}")
            }
        }
    }
}

apply<GreetingPlugin>()

// Configure the extension using a DSL block
configure<GreetingPluginExtension> {
    message = "Hi"
    greeter = "Gradle"
}
```  

**gradle -q hello** 的输出结果为  

```
> gradle -q hello
Hi from Gradle
```  
在本例中，可以在c`onfigure＜GreetingPluginExtension＞`块中将多个设置分组在一起。在构建脚本`configure`中的配置函数上使用的类型（`GreetingPluginExtension`）需要与扩展类型匹配。然后，当执行该块时，该块的接收器就是扩展对象。  
通过这种方式，使用扩展对象扩展Gradle DSL，为插件添加项目属性和DSL块。因为扩展对象只是一个常规对象，所以可以通过向扩展对象添加属性和方法来提供嵌套在插件块中的DSL。  

开发项目插件
您可以在中找到有关实现项目扩展的更多信息在 [Developing Custom Gradle Types.](https://docs.gradle.org/current/userguide/custom_gradle_types.html#custom_gradle_types)
  
### 使用自定义任务和插件中的文件
  
在开发自定义任务和插件时，最好在接受文件位置的输入配置时非常灵活。您应该使用Gradle的`Managed properties`（托管属性）和`project.layout`来选择文件或目录位置。因此，只有在需要文件时才能解析实际位置，并且可以在构建配置期间随时重新配置。
  
```
abstract class GreetingToFileTask : DefaultTask() {

    @get:OutputFile
    abstract val destination: RegularFileProperty

    @TaskAction
    fun greet() {
        val file = destination.get().asFile
        file.parentFile.mkdirs()
        file.writeText("Hello!")
    }
}

val greetingFile = objects.fileProperty()

tasks.register<GreetingToFileTask>("greet") {
    destination = greetingFile
}

tasks.register("sayGreeting") {
    dependsOn("greet")
    val greetingFile = greetingFile
    doLast {
        val file = greetingFile.get().asFile
        println("${file.readText()} (file: ${file.name})")
    }
}

greetingFile = layout.buildDirectory.file("hello.txt")
```

**gradle -q sayGreeting** 的输出结果为  

```
> gradle -q sayGreeting
Hello! (file: hello.txt)
```
在本例中，我们将`greet`任务`destination`属性配置为闭包/提供对象，该属性使用`Project.file（java.lang.Object）`方法进行评估，以在最后一刻将闭包/提供器的返回值转换为file对象。您会注意到，在上面的示例中，我们在配置为将`greetingFile`属性值用于任务后指定了该属性值。这种惰性求值是在设置文件属性时接受任何值，然后在读取属性时解析该值的一个关键好处。  

将扩展属性映射到任务属性   
通过扩展从构建脚本捕获用户输入并将其映射到自定义任务的输入/输出属性是一种有用的模式。构建脚本编写只与扩展定义的DSL进行交互。命令式逻辑隐藏在插件实现中。Gradle提供了一些类型，您可以在任务实现和扩展中使用这些类型来帮助您做到这一点。有关更多信息，请参阅[Lazy Configuration](https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_configuration)。  

**一个独立项目**  
现在我们将把我们的插件移到一个独立的项目中，这样我们就可以发布它并与其他人共享。这个项目只是一个Java项目，它生成一个包含插件类的JAR。打包和发布插件最简单也是推荐的方法是使用[Java Gradle插件开发插件](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)。该插件将自动应用Java插件，将`gradleApi()`依赖项添加到api配置中，在生成的JAR文件中生成所需的插件描述符，并配置发布时使用的`Plugin Marker Artifact` ([发布插件](https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers))。以下是项目的一个简单构建脚本。  
```
plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "org.example.greeting"
            implementationClass = "org.example.GreetingPlugin"
        }
    }
}
```

**创建插件id**   
插件id以类似于Java包（即反向域名）的方式进行完全限定。这有助于避免冲突，并提供了一种将具有类似所有权的插件分组的方法。您的插件id应该是反映命名空间（指向您或您的组织的合理标志）及其提供的插件名称的组件的组合。例如，如果你有一个名为“`foo”`的Github帐户，而你的插件名为`“bar”`，那么合适的插件id可能是`com.Github.foo.bar`。同样，如果插件是在`baz`组织开发的，那么插件id可能是`org.baz.bar`。  
插件ID应符合以下要求：
- 可以包含任何字母数字字符“.”，和“-”。
- 必须至少包含一个“.”将命名空间与插件名称分隔开的字符。
- 按照惯例，命名空间使用小写的反向域名约定。
- 按惯例，名称中只使用小写字符。
- 不能使用org.gradle和com.gradleware命名空间
- 不能以“.”开头或结尾性格
- 不能包含连续的“.”字符（即'..'）。  

尽管插件id和包名称之间有着惯例的相似之处，但包名称通常比插件id所需的更详细。例如，添加`“gradle”`作为插件id的一个组成部分似乎是合理的，但由于插件id仅用于gradle插件，这将是多余的。一般来说，一个好的插件id只需要一个标识所有权的命名空间和一个名称。

**发布插件**
如果您在内部发布插件以供组织内部使用，则可以像发布任何其他代码工件一样发布它。请参阅[Ivy](https://docs.gradle.org/current/userguide/publishing_ivy.html#publishing_ivy)和[Maven](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven)关于发布工件的章节。
如果你有兴趣发布你的插件供更广泛的Gradle社区使用，你可以将其发布到[Gradle插件门户网站](https://plugins.gradle.org/?_gl=1*1qdu8uk*_ga*MTU1MjEwNjg1OC4xNzAwNDY4MTc3*_ga_7W7NC6YNPT*MTcwNTk3NjU3Ny4zNC4xLjE3MDU5NzY1OTEuNDYuMC4w)。该网站提供了搜索和收集Gradle社区提供的插件信息的能力。请参阅相应的[部分](https://docs.gradle.org/current/userguide/publishing_gradle_plugins.html#publishing_portal)，了解如何使您的插件在此网站上可用。


**在另一个项目中使用插件**  
要在构建脚本中使用插件，您需要在项目设置文件的`pluginManagement｛｝`块中配置仓库。以下示例显示了当插件已发布到本地存储库时如何执行此操作：  
```
settings.gradle.kts  

pluginManagement {
    repositories {
        maven {
            url = uri(repoLocation)
        }
    }
}

build.gradle.kts

plugins {
    id("org.example.greeting") version "1.0-SNAPSHOT"
}

```

**没有`java-gradle-plugin`发布的插件注意事项**
如果你的插件是在没有使用[Java Gradle Plugin Development Plugin](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)的情况下发布的，那么该发布将缺少[Plugin Marker Artifact(插件标记工件)](https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers)，这是插件DSL定位插件所需要的。在这种情况下，建议在另一个项目中解析插件的方法是将`resolutionStrategy`部分添加到项目设置文件的`pluginManagement｛｝`块中，如下所示。

```
settings.gradle.kts

resolutionStrategy {
    eachPlugin {
        if (requested.id.namespace == "org.example") {
            useModule("org.example:custom-plugin:${requested.version}")
        }
    }
}
```

**预编译的脚本插件**  
除了作为独立项目编写的插件外，Gradle还允许您提供用Groovy或Kotlin DSL编写的构建逻辑作为预编译脚本插件。您可以将它们写成`src/main/groovy`目录中的`*.gradle`文件或`src/main/kotlin`目录中的`*.gradle.kts`文件。  
预编译脚本插件名称有两个重要限制
- 他们不能以`org.gradle`作为开始。
- 它们不能与内置插件id同名。  

这确保了预编译的脚本插件不会被静默地忽略。  
预编译的脚本插件被编译成类文件并打包到一个jar中。总而言之，它们是二进制插件，可以通过插件 ID 应用，测试并以二进制插件的形式发布。事实上，它们的插件元数据是使用`Gradle Plugin Development Plugin`生成的。
使用Gradle6.0构建的Kotlin-DSL预编译脚本插件不能与早期版本的Gradle一起使用。这一限制将在未来版本的Gradle中取消。
Groovy-DSL预编译的脚本插件从Gradle6.4开始提供。Groovy-DSL预编译的脚本插件可以应用于使用Gradle5.0及更高版本的项目。

要应用预编译的脚本插件，您需要知道其ID，该ID源自插件脚本的文件名（减去`.gradle.kts`扩展名）及其（可选）包声明。  
例如，脚本`src/main/kotlin/java-library-convention.gradle.kts`将具有`java-library-convention`的插件ID（假设它没有包声明）。同样，`src/main/kotlin/my/java-library-convention.gradle.kts`将产生`my.java-library-invention`的插件ID，只要它有一个`my`的包声明。为了演示如何实现和使用预编译的脚本插件，让我们浏览一个基于`buildSrc`项目的示例。  
首先，您需要一个应用`kotlin-dsl`插件的`buildSrc/build.gradle.kts`文件：  
Example 8. Enabling precompiled script plugins
```
buildSrc/build.gradle.kts  

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
```
我们建议您还创建一个`buildSrc/settings.gradle.kts`文件，该文件可能为空。
接下来，在`buildSrc/src/main/kotlin`目录中创建一个新的`java-library-convention.gradle.kts`文件，并将其内容设置为以下内容：  
Example 9. Creating a simple script plugin
```
buildSrc/src/main/kotlin/java-library-convention.gradle.kts

plugins {
    `java-library`
    checkstyle
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

checkstyle {
    maxWarnings = 0
    // ...
}

tasks.withType<JavaCompile> {
    options.isWarnings = true
    // ...
}

dependencies {
    testImplementation("junit:junit:4.13")
    // ...
}
```

这个脚本插件只需应用Java库和Checkstyle插件并对它们进行配置。请注意，这实际上会将插件应用于主项目，即应用预编译脚本插件的项目。
最后，将脚本插件应用于根项目，如下所示  
Example 10. Applying the precompiled script plugin to the main project
```
build.gradle.kts

plugins {
    `java-library-convention`
}

```

**在预编译脚本插件中应用外部插件**
为了在预编译的脚本插件中应用外部插件，必须将其添加到插件的构建文件中的插件项目的实现类路径中。

```
buildSrc/build.gradle.kts

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.bmuschko:gradle-docker-plugin:6.4.0")
}

```
然后可以将其应用于预编译的脚本插件中

```
buildSrc/src/main/kotlin/my-plugin.gradle.kts

plugins {
    id("com.bmuschko.docker-remote-api")
}

```
这种情况下的插件版本是在依赖声明中定义的

## 为插件编写测试
您可以使用`ProjectBuilder`类创建项目实例，以便在测试插件实现时使用。
Example: Testing a custom plugin  
`src/test/java/org/example/GreetingPluginTest.java`
```
public class GreetingPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("org.example.greeting");

        assertTrue(project.getTasks().getByName("hello") instanceof GreetingTask);
    }
}

```
**更多详细信息**
插件通常还提供自定义任务类型。有关更多详细信息，请参阅[Developing Custom Gradle Task Types](https://docs.gradle.org/current/userguide/custom_tasks.html#custom_tasks)。

`Gradle`提供了许多有助于开发`Gradle`类型（包括插件）的功能。有关更多详细信息，请参阅[Developing Custom Gradle Types](https://docs.gradle.org/current/userguide/custom_gradle_types.html#custom_gradle_types)。

请注意：在开发Gradle插件时，在将信息记录到构建日志时要小心，这一点很重要。记录敏感信息（如凭据、令牌、某些环境变量）被视为安全漏洞。公共连续集成服务的生成日志可在公开可查看，并可能公开此敏感信息。

**幕后花絮**
那么`Gradle`是如何找到`Plugin`实现的呢？答案是-您需要在JAR的`META-INF/gradle-plugins`目录中提供一个属性文件，该文件与您的插件的id相匹配，该插件由`Java gradle-PluginDevelopmentPlugin`处理。
Example: Wiring for a custom plugin  
给定一个ID为`org.example.hettinging`的插件和实现类`org.example.HettingPlugin`  
```
src/main/resources/META-INF/gradle-plugins/org.example.greeting.properties
```
请注意，属性`filename`与插件id匹配，并放置在`resources`文件夹中，实现类属性标识插件实现类.
































<details>  
<summary> 语法 </summary>     
<font face="黑体" color=Blue size=2>我是黑体字</font>  
<table><tr><td bgcolor=yellow>背景色yellow</td></tr></table>
</details>    




