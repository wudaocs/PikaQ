## [Android studio Not support JCEF(markdown预览功能)](https://zhuanlan.zhihu.com/p/427137793)
**For Android Studio 2021.1+:**

- Open menu item HelpFind action… and search for “Choose Boot Java runtime for the IDE…”
- Choose a runtime “11.0.x…” labeled “JetBrains Runtime with JCEF” and install it. The IDE will restart.
- Go to the settings of the AsciiDoc plugin and check if the preview is set to “JCEF Browser”. Change it to “JCEF Browser” if this is not the case.

**For Android Studio 4.2 and 2020.3+:**

- Install the plugin “Choose Runtime”.
- Open menu item HelpFind action… and search for “Choose Runtime…”
- Choose a runtime "jbrsdk-11_0_…tar.gz" or later and install it. The IDE will restart.
- Go to the settings of the AsciiDoc plugin and check if the preview is set to “JCEF Browser”. Change it to “JCEF Browser” if this is not the case.Markdown Preview