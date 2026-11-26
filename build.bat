@echo off
rem Builds BarcodeQRGenerator.jar from src\ and the bundled ZXing 3.5.3 sources (no internet needed).
set ZX=lib\zxing-zxing-3.5.3
if exist build rmdir /s /q build
mkdir build\classes
dir /s /b "%ZX%\core\src\main\java\*.java" > build\sources.txt
echo %ZX%\javase\src\main\java\com\google\zxing\client\j2se\MatrixToImageWriter.java>> build\sources.txt
echo %ZX%\javase\src\main\java\com\google\zxing\client\j2se\MatrixToImageConfig.java>> build\sources.txt
dir /s /b "src\*.java" >> build\sources.txt
javac --release 11 -nowarn -encoding UTF-8 -d build\classes @build\sources.txt || exit /b 1
echo Main-Class: com.example.BarcodeQRGenerator> build\manifest.txt
jar cfm BarcodeQRGenerator.jar build\manifest.txt -C build\classes .
echo Built BarcodeQRGenerator.jar  -^>  java -jar BarcodeQRGenerator.jar
