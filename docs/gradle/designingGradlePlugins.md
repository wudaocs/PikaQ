# [设计Gradle插件](https://docs.gradle.org/current/userguide/designing_gradle_plugins.html)
对于初学者来说，实现插件可能是一项艰巨的任务，其中包括许多考虑因素和深层知识：
组织和构建插件逻辑、测试和调试插件代码以及将插件组件发布到存储库以供使用。
在本节中，你将学习如何根据既定实践正确设计Gradle插件，并将其应用于你自己的项目。  
本节假设你具备：  
- 对软件工程实践的基本理解。
- 了解Gradle的基本知识，如项目组织、任务创建和配置以及Gradle构建生命周期。
## 结构
### 可重用逻辑应写成二进制插件  
Gradle用户手册区分了两种类型的插件：[脚本插件和二进制插件](https://docs.gradle.org/current/userguide/plugins.html#sec:types_of_plugins)。脚本插件基本上只是具有不同名称的普通旧版本Gradle的构建脚本。虽然脚本插件在Gradle项目中有组织构建逻辑的位置，但很难保持它们的良好维护，它们很难测试，而且你不能在其中定义新的可重用类型。  
当逻辑需要在独立项目中重用或共享时，应使用[二进制插件](https://docs.gradle.org/current/userguide/plugins.html#sec:binary_plugins)。它们允许将代码正确地结构化为类和包，是可缓存的，可以遵循版本控制方案来实现平稳的升级过程，并且易于测试。  
### 考虑对性能的影响  
作为Gradle插件的开发人员，你可以完全自由地定义和组织代码。任何可以想象的逻辑都可以实现。在设计Gradle插件时，始终要注意对最终用户的影响。看似简单的逻辑会对构建的执行性能产生相当大的影响。当插件的代码在[构建生命周期的配置阶段](https://docs.gradle.org/current/userguide/build_lifecycle.html#sec:build_phases)执行时，例如通过迭代来解决依赖关系、进行HTTP调用或写入文件。关于[优化Gradle构建性能](https://docs.gradle.org/current/userguide/performance.html#performance_gradle)的部分将为你提供额外的代码示例、陷阱和建议。  
在编写插件代码时，问问自己代码是否不应该在执行阶段运行。如果你怀疑插件代码有问题，请尝试创建[构建扫描](https://scans.gradle.com/?_gl=1*1aygqcq*_ga*MTU1MjEwNjg1OC4xNzAwNDY4MTc3*_ga_7W7NC6YNPT*MTcwNjU4MjUzMy40Mi4xLjE3MDY1ODM2ODEuNjAuMC4w&_ga=2.3983607.656167394.1706507796-1552106858.1700468177)以识别瓶颈。[Gradle探查器](https://github.com/gradle/gradle-profiler)可以帮助自动生成构建扫描并收集更多低级信息。  
### 约定高于配置
约定优于配置"是一种软件工程范例，，它允许工具或框架在不失去灵活性的情况下尝试减少用户必须做出的决策的数量。这对Gradle插件意味着什么？Gradle插件可以在特定的上下文中为用户提供合理的默认值和标准（约定）。让我们以[Java插件](https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin)为例。  
- 它将目录`src/main/java`定义为编译的默认源目录。
- 已编译源代码和其他工件（如JAR文件）的输出目录为 `build`。  

只要插件的使用者不喜欢使用其他约定，在使用构建脚本时就不需要额外的配置。简单到可以开箱即用。但是，如果用户更喜欢其他标准，则可以重新配置默认约定。两全其美。  
在实践中，你会发现大多数用户对默认约定感到满意，直到有充分的理由更改它们，例如，如果必须使用遗留项目。在编写自己的插件时，请确保选择合理的默认值。如果你发现大多数插件使用者不需要重新配置它们，你可以了解你是否为插件选择了合理的约定。  
让我们来看一个插件引入的约定示例。该插件通过进行HTTP调用从服务器检索信息。插件使用的默认URL被配置为指向开发插件的组织内的服务器：  `https://www.myorg.com/server. `使默认URL可配置的一个好的方法是采用[扩展](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:getting_input_from_the_build)。扩展公开了一个自定义DSL，用于捕获影响运行时行为的用户输入。以下示例为所讨论的示例显示了这样一个自定义DSL：  
```
build.gradle.kts
plugins {
   id("org.myorg.server")
}

server {
    url = "http://localhost:8080/server"
}

```

正如你所看到的，用户只声明`“what”`-插件应该访问的服务器。实际的内部工作——`“how”`——对最终用户来说是完全隐藏的。

### 功能 vs 约定
插件带来的功能可能非常强大，但也可能非常主观。如果插件预先定义了项目在应用时自动继承的任务和约定，情况尤其如此。有时，作为插件开发人员，你为用户选择的现实可能会与预期不同。正因为如此，你需要尽量使插件灵活和可配置。提供这些质量标准的一种方式是将功能与约定分开。实际上，这意味着将通用功能与预配置的主观功能分开。让我们通过一个例子来解释这个看似抽象的概念。有两个Gradle核心插件完美地展示了这个概念：[Java Base插件](https://docs.gradle.org/current/javadoc/org/gradle/api/plugins/JavaBasePlugin.html)和[Java插件](https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin)。
- Java Base插件只提供了不带主观色彩的功能和通用概念。例如，它规范了SourceSet的概念并引入了依赖管理配置。然而，它实际上并没有创建你作为Java开发人员常用的任务，也没有创建源集的实例。
- Java插件在内部应用了Java Base插件并继承了它的所有功能。除此之外，它还创建了诸如`“main”`和“`test”`之类的源集实例，并创建了对Java开发人员而言非常熟悉的任务，如`“classes”，“jar”或“javadoc”`。它还在这些任务之间建立了对该域有意义的生命周期。 

总之，我们将功能与约定分开了。因为项目结构与其不符，如果用户不喜欢创建的任务，或者不想重新配置许多约定，那么他们可以回到应用Java Base插件并自己处理。  

在设计自己的插件时，你应该考虑使用相同的技术。你可以在同一个项目中开发这两个插件，并将它们的编译类和标识符与相同的二进制构件一起发布。以下代码示例展示了如何从另一个插件中应用插件，即所谓的插件组合：  
`MyBasePlugin.java  `
```
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyBasePlugin implements Plugin<Project> {
    public void apply(Project project) {
        // define capabilities
    }
}
``` 
`MyPlugin.java ` 
```
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPlugins().apply(MyBasePlugin.class);

        // define conventions
    }
}

```  
为了获得灵感，这里有两个应用该概念的开源插件：
- [Docker plugin](https://bmuschko.github.io/gradle-docker-plugin/#provided_plugins)
- [Cargo plugin](https://github.com/bmuschko/gradle-cargo-plugin#provided-plugins)
  
## 技术
更喜欢使用静态类型的语言来实现插件  
Gradle并不对你应该选择哪种编程语言来实现插件持有立场。只要插件的二进制文件能在JVM上执行，这就是开发人员的选择。建议使用Java或Kotlin等静态类型语言来实现插件，以降低二进制不兼容的可能性。如果你决定在插件实现中使用Groovy，那么使用`annotation@Groovy.transform.CompileStatic`是一个不错的选择。  
使用静态类型语言的建议与为插件代码编写测试的语言选择无关。使用动态`Groovy`和[Spock（其非常强大的测试和模拟框架）](https://spockframework.org/)是一个非常可行和常见的选择。
### 将插件实现限制在Gradle的公共API范围内
为了能够构建`Gradle`插件，你需要告诉你的项目使用对`Gradle API`的编译依赖。您的构建脚本通常会包含以下声明：  
```
build.gradle.kts
dependencies {
    implementation(gradleApi())
}
```  
重要的是要理解这种依赖包括了完整的`Gradle`运行时。出于历史原因，公共和内部的`Gradle API`目前尚未分离。

了确保与其他Gradle版本的最佳向前和向后兼容性，你应该只使用公共API。在大多数情况下，它将支持你尝试用插件支持的用例。请记住，内部API可能会发生变化，并且很容易导致你的插件在一个Gradle版本到另一个版本时出现问题。如果你正在寻找目前仅限于内部的公共API，请在`GitHub`上提出问题。  
你如何知道一个类是否属于公共API？如果你可以在`DSL`指南或`Javadocs`中找到对该类的引用，那么你可以安全地假定它是公共的。未来，我们计划明确区分公共API和内部API，这将使最终用户能够在构建脚本中声明相关的依赖项。
### 最大限度地减少外部库的使用
作为应用程序开发人员，我们已经习惯于使用外部库来避免编写基本功能。你可能不想再没有你心爱的`Guava`或`HttpClient`库了。请记住，当通过Gradle的依赖关系管理系统声明时，一些库可能会引入一个巨大的可传递依赖关系图。依赖关系报告不会呈现为构建脚本的`classpath`配置声明的依赖关系，实际上是声明插件的类路径及其可传递依赖关系。但是，您可以调用帮助任务`buildEnvironment`来渲染完整的依赖关系图。为了演示该功能，让我们假设以下构建脚本：  
```
build.gradle.kts
plugins {
    id("org.asciidoctor.jvm.convert") version "3.2.0"
}

```  
任务的输出清楚地指示了`classpath`配置的类路径：
```
$ gradle buildEnvironment

> Task :buildEnvironment

------------------------------------------------------------
Root project 'external-libraries'
------------------------------------------------------------

classpath
\--- org.asciidoctor.jvm.convert:org.asciidoctor.jvm.convert.gradle.plugin:3.2.0
     \--- org.asciidoctor:asciidoctor-gradle-jvm:3.2.0
          +--- org.ysb33r.gradle:grolifant:0.16.1
          |    \--- org.tukaani:xz:1.6
          \--- org.asciidoctor:asciidoctor-gradle-base:3.2.0
               \--- org.ysb33r.gradle:grolifant:0.16.1 (*)

(*) - Indicates repeated occurrences of a transitive dependency subtree. Gradle expands transitive dependency subtrees only once per project; repeat occurrences only display the root of the subtree, followed by this annotation.

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```
重要的是要理解Gradle插件不会在自己的独立类加载器中运行。因此，这些依赖关系可能会与从其他插件解析的同一库的其他版本发生冲突，并可能导致意外的运行时行为。在编写Gradle插件时，请考虑您是否真的需要一个特定的库，或者您是否可以自己实现一个简单的方法。  
对于作为任务执行的一部分执行的逻辑，请使用允许隔离库的[`Worker API`](https://docs.gradle.org/current/userguide/worker_api.html#tasks_parallel_worker)。













































