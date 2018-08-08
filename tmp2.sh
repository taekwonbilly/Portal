find . -iname "*.class" -exec rm {} \;
rm -rf src/com

javac -target 1.7 -source 1.7 -cp "./src:./lib/lwjgl/lwjgl.jar:./lib/lwjgl/lwjgl_util.jar:./lib/lwjgl/lwjgl_util_applet.jar:./lib/lwjgl/jinput.jar:./lib/jl1.0.1.jar:lib/google/guava-r09.jar:./Ardor3D/ardor3d-core/src/main/java:./Ardor3D/ardor3d-awt/src/main/java:./Ardor3D/ardor3d-lwjgl/src/main/java:./Ardor3D/ardor3d-math/src/main/java:./Ardor3D/ardor3d-savable/src/main/java" src/backbone/Main.java || exit 1
cd src && java -cp ".:../lib/lwjgl/lwjgl.jar:../lib/lwjgl/lwjgl_util.jar:../lib/lwjgl/lwjgl_util_applet.jar:../lib/lwjgl/jinput.jar:../lib/jl1.0.1.jar:../lib/google/guava-r09.jar:../Ardor3D/ardor3d-core/src/main/java:../Ardor3D/ardor3d-awt/src/main/java:../Ardor3D/ardor3d-lwjgl/src/main/java:../Ardor3D/ardor3d-math/src/main/java:../Ardor3D/ardor3d-savable/src/main/java" -Djava.library.path="../native_libs" backbone.Main

#javac -cp ".:./src:./lib:./lib/lwjgl/lwjgl.jar:./lib/lwjgl/lwjgl_util.jar:./lib/lwjgl/lwjgl_util_applet.jar:./lib/lwjgl/jinput.jar:./lib/jl1.0.1.jar:lib/google/guava-r09.jar:./ardor-old/ardor3d-core/src/main/java:./ardor-old/ardor3d-awt/src/main/java:./ardor-old/ardor3d-lwjgl/src/main/java:./lib/AWT.jar:./lib/CORE.jar" src/backbone/Main.java || exit 1 
#cd src && java -cp ".:../lib/lwjgl/lwjgl.jar:../lib/lwjgl/lwjgl_util.jar:../lib/lwjgl/lwjgl_util_applet.jar:../lib/lwjgl/jinput.jar:../lib/jl1.0.1.jar:../lib/google/guava-r09.jar:../ardor-old/ardor3d-core/src/main/java:../ardor-old/ardor3d-awt/src/main/java:../ardor-old/ardor3d-lwjgl/src/main/java:../lib/AWT.jar:../lib/CORE.jar" backbone.Main

#javac -cp ".:./src:./lib:./lib/lwjgl/lwjgl.jar:./lib/lwjgl/lwjgl_util.jar:./lib/lwjgl/jinput.jar:./lib/jl1.0.1.jar:lib/google/guava-r09.jar:./ardor-old/ardor3d-core/src/main/java:./ardor-old/ardor3d-awt/src/main/java:./ardor-old/ardor3d-lwjgl/src/main/java" src/backbone/Main.java || exit 1 
#cd src && java -cp ".:../lib/lwjgl/lwjgl.jar:../lib/lwjgl/lwjgl_util.jar:../lib/lwjgl/jinput.jar:../lib/jl1.0.1.jar:../lib/google/guava-r09.jar:../ardor-old/ardor3d-core/src/main/java:../ardor-old/ardor3d-awt/src/main/java:../ardor-old/ardor3d-lwjgl/src/main/java" backbone.Main
