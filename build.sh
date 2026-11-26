#!/bin/sh
# Builds BarcodeQRGenerator.jar from src/ and the bundled ZXing 3.5.3 sources (no internet needed).
set -e
ZX=lib/zxing-zxing-3.5.3
rm -rf build && mkdir -p build/classes
find "$ZX/core/src/main/java" -name '*.java' > build/sources.txt
echo "$ZX/javase/src/main/java/com/google/zxing/client/j2se/MatrixToImageWriter.java" >> build/sources.txt
echo "$ZX/javase/src/main/java/com/google/zxing/client/j2se/MatrixToImageConfig.java" >> build/sources.txt
find src -name '*.java' >> build/sources.txt
javac --release 11 -nowarn -encoding UTF-8 -d build/classes @build/sources.txt
printf 'Main-Class: com.example.BarcodeQRGenerator\n' > build/manifest.txt
jar cfm BarcodeQRGenerator.jar build/manifest.txt -C build/classes .
echo "Built BarcodeQRGenerator.jar  ->  java -jar BarcodeQRGenerator.jar"
