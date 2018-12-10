Logtalk plugin for Intellij IDEA
======================================

This plugin allows you to develop [Logtalk](http://logtalk.org/) programs with [Intellij IDEA](http://www.jetbrains.com/idea).
It requires Intellij IDEA Ultimate or Community Edition, versions 2016.x, 2017.x, and 2018.x.

#### Technical Support
Support for this plugin is available through the project's home page: [http://intellij-logtalk.org](http://intellij-logtalk.org), or
the github issues list [http://github.com/LogtalkFoundation/intellij-logtalk/issues](http://github.com/LogtalkFoundation/intellij-logtalk/issues).

#### Past Versions Announcement

Very, very few users are continuing to use this plugin with IDEA versions 14.x and 15.x.  Plugin version 0.11.2 was the last
version published that supported these environments.  (If you and/or your business need these or previous versions supported, then
[contact the project maintainers](http://intellij-logtalk.org/contact) at the project page ([http://intellij-logtalk.org](http://intellij-logtalk.org)) and continuing support
can be arranged.) 

#### We have moved!
As of January 2017, the primary maintained branch of this plugin has moved to the Logtalk Foundation's github repository.  Please update your
links and git clones to point to [http://github.com/LogtalkFoundation/intellij-logtalk](http://github.com/LogtalkFoundation/intellij-logtalk)

Install
-------

*JetBrains' official plugin installation documentation is at [https://www.jetbrains.com/idea/plugins/](https://www.jetbrains.com/idea/plugins/).
The Logtalk plugin page is [https://plugins.jetbrains.com/plugin/6873?pr=idea](https://plugins.jetbrains.com/plugin/6873?pr=idea).*

### To install using IDEA (from Intellij plugin repository):

Install and start IDEA.  It is found at [https://www.jetbrains.com/idea](https://www.jetbrains.com/idea)

If you do not have a project open in IDEA (and after first-time setup):
- On the IDEA welcome screen, select "Configure(dropdown)->Plugins"
- Click on the "Browse Repositories..." button.
- Type 'logtalk' to see the description for the plugin.
- Select 'Install' to install it.
- Allow IDEA to restart and initialize the plugin.

If you already have a project open in IDEA:
- Open the Settings dialog (File->Settings...)
- Highlight "Plugins" in the leftmost column
- Click on the "Browse Repositories..." button.
- Type 'logtalk' to see the description for the plugin.
- Select 'Install' to install it.
- Allow IDEA to restart and initialize the plugin.

### To manually install the latest or a previous Github release

Download the `intellij-logtalk.jar` file from the release you want from [Github releases](https://github.com/LogtalkFoundation/intellij-logtalk/releases).
More recent releases have begun to be named `intellij-logtalk-<release>.jar`, where &lt;release&gt; is the version of Idea for which the Jar is built.  (e.g. `intellij-logtalk-14.1.1.jar`)
Make sure that you pick the proper one for your release.  A message should pop up and warn you if a release is incompatible.

If you do not yet have a project open in IDEA (and after first-time setup):
- On the IDEA welcome screen, select "Configure(dropdown)->Plugins"
- Click “Install plugin from disk...”
- Select the “intellij-logtalk.jar” file you downloaded
- Allow IDEA to restart and initialize the plugin.

If you already have a project open IDEA:
- Open the Settings dialog (File->Settings...)
- Highlight "Plugins" in the leftmost column
- Click “Install plugin from disk...”
- Select the “intellij-logtalk.jar” file you downloaded
- Allow IDEA to restart and initialize the plugin.

Build
-----
Note that installation as described above installs a fully built version of the plugin (the .jar file).  Most users do not have to
build the product for themselves.  This section is for those who like to dig a little deeper.

This describes the command line build on a Linux platform. To build from within Intellij IDEA itself, see the [contributing](CONTRIBUTING.md) document to setup
your development environment.  Much more detail is provided there for command line build options as well.

### Dependencies
- Ant
- Oracle JDK 8 or OpenJDK 8
- Make
- A bash compatible shell

### Build command
```
make
```

This will generate a `intelllij-logtalk-<release>.jar` file at the root of the project that you can then install from disk
(see “Install the latest or a previous Github release).  Note that the default make (see Makefile) will build the plugin for
Idea 2017.3.3.  To override the default, set the IDEA_VERSION environment variable prior to executing make.

```
IDEA_VERSION=2016.3.4 make
```

Note that building via make will download the requested version of IntelliJ Ultimate (to a temporary directory)
every time a build is started.  This can be quite slow at times and prone to failure.  For repeated building and testing,
we recommended that you set up your machine as described in the [contributing document](CONTRIBUTING.md). 

Test
----

### Dependencies
Same as for build.

### Test command
```
make test
```

This will build and run the tests and display the JUnit report.  Again, you can override the Idea version
being tested against by overriding IDEA_VERSION.

```
IDEA_VERSION=2016.3.4 make test
```


Use the hxcpp debugger
----------------------

*NOTE: IDEA Community Edition currently will not start an IDE-based debugging session.  For that,
IDEA Ultimate is required.  Command-line debugging is available because that is a feature
of the Logtalk language itself.  See the hxcpp-debugger project for more information.*

The hxcpp debugger functionality conforms to the
Logtalk v3.0 debugger.  In order to use this, you must:

- Install the *VERSION 1.1* debugger Logtalklib from [https://github.com/LogtalkFoundation/hxcpp-debugger/tree/protocol_v1.1](https://github.com/LogtalkFoundation/hxcpp-debugger/tree/protocol_v1.1).
  The hxcpp-debugger that is installed via 'Logtalklib install' is generally
  not the latest or best working version. (The Logtalk Foundation maintainers
  do not release regular updates for it). Instead, get the current sources
  locally: Install git and clone the repository from
  [http://github.com/LogtalkFoundation/hxcpp-debugger](http://github.com/LogtalkFoundation/hxcpp-debugger)
  and install via
  ```
  Logtalklib git hxcpp-debugger <your_local_clone> protocol_v1.1
  ```
  Then, you'll have the version
  that matches the plugin. Whenever you need to update the debugger to
  the latest sources, do a 'git pull' and then rebuild your app.
- Re-build your project using this newest debugger Logtalklib.
- Configure your logtalk program to start the debugger when the following
  command line option is provided:

  ```
  -start_debugger
  ```
  If you expect to do remote debugging, you'll also have to support:

  ```
  -debugger_host=[host]:[port]
  ```

  Most likely you'll just want to add the following in your main() when
  `-start_debugger` is set:
    
  ```logtalk
  new debugger.LogtalkRemote(true, host, port);
  ```

  Generally speaking, the build/debug configuration (Run->Edit Configurations)
   is set up to use port 6972, so you can probably cheat and use:

  ```logtalk
  new debugger.LogtalkRemote(true, "localhost", 6972);
  ```

  However, the line has to match your debug settings. Fortunately, they are
  passed to your program on the command line. Notice the
  "-start_debugger -debugger_host=localhost:6972" passed to Logtalklib:
  ```
  C:/LogtalkToolkit/logtalk/Logtalklib.exe run lime run C:/temp/issue349/openfl_cpp_debug/openfl_cpp_debug/project.xml
    windows -verbose -Ddebug -debug -args  -start_debugger -debugger_host=localhost:6972
  ```
Your program should now:
1) Look for the '-start_debugger' parameter before doing anything. It won't
 be there if the program is being started via the "Run" command from IDEA.
2) Parse the '-debugger_host=' parameter.  If it exists,
 then a remote controller (e.g. IDEA) will be trying to connect on that port.
 If it doesn't exist, then the user (you) probably want to start the command
 line debugger:
  ```logtalk
  new debugger.Local(true);
  ```

Here's a snippet you can use: (Thanks to @isBatak)
  ```logtalk
  #if (debug && cpp)
    var startDebugger:Bool = false;
    var debuggerHost:String = "";
    var argStartDebugger:String = "-start_debugger";
    var pDebuggerHost:EReg = ~/-debugger_host=(.+)/;

    for (arg in Sys.args()) {
      if(arg == argStartDebugger){
        startDebugger = true;
      }
      else if(pDebuggerHost.match(arg)){
        debuggerHost = pDebuggerHost.matched(1);
      }
    }

    if(startDebugger){
      if(debuggerHost != "") {
        var args:Array<String> = debuggerHost.split(":");
        new debugger.LogtalkRemote(true, args[0], Std.parseInt(args[1]));
      }
      else {
        new debugger.Local(true);
      }
    }
  #end
  ```

Contribute
----------
See the [contributing document](CONTRIBUTING.md).
