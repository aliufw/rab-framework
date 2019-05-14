jar -cvf testapplet.jar com/cssnet/platform/baseframe/comm/codecache/browsercache/*.class

jarsigner -keystore com/cssnet/platform/baseframe/comm/codecache/browsercache/keystore -storepass 111111 -keypass 111111 -signedjar signtestapplet.jar testapplet.jar myself

copy com\cssnet\platform\baseframe\comm\codecache\browsercache\testapplet.html D:\aj-studio\jakarta-tomcat-5.0.28\webapp\cachedata\applet\testapplet.html
copy signtestapplet.jar D:\aj-studio\jakarta-tomcat-5.0.28\webapp\cachedata\applet\signtestapplet.jar

del *.jar 

