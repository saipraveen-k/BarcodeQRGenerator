# BarcodeQRGenerator

A Java Swing desktop app that turns text into a **QR code or barcode** and saves it as a PNG. It is built on the [ZXing](https://github.com/zxing/zxing) library (bundled in `lib/`, Apache 2.0).

## Supported formats

| Format | Allowed input |
|---|---|
| QR Code | Any text or URL |
| Data Matrix | Any text |
| Aztec | Any text |
| PDF417 | Any text |
| Code 128 | Printable ASCII |
| Code 39 | A–Z, 0–9, space and `- . $ / + %` |
| EAN-13 | 12 or 13 digits (check digit added automatically) |
| UPC-A | 11 or 12 digits (check digit added automatically) |

## Build

Needs JDK 11+. No internet is required, because ZXing is compiled from the bundled source.

```bash
./build.sh        # macOS / Linux
build.bat         # Windows
```

This produces `BarcodeQRGenerator.jar`.

## Run

**Desktop app**

```bash
java -jar BarcodeQRGenerator.jar
```

Type your text, pick a format, press **Generate** (or Enter), then **Save PNG…**.

**Command line** (no window, works on servers):

```bash
java -jar BarcodeQRGenerator.jar --cli "https://example.com"                 # QR code -> code.png
java -jar BarcodeQRGenerator.jar --cli "590123412345" EAN_13 product.png
java -jar BarcodeQRGenerator.jar --cli "ABC-123" CODE_128 label.png
```

Format names: `QR_CODE`, `DATA_MATRIX`, `AZTEC`, `PDF_417`, `CODE_128`, `CODE_39`, `EAN_13`, `UPC_A`.

## Project structure

```
src/com/example/BarcodeQRGenerator.java   # the app (GUI + CLI)
lib/zxing-zxing-3.5.3/                    # ZXing 3.5.3 source (core + javase used)
build.sh / build.bat                      # compile into a runnable jar
```
