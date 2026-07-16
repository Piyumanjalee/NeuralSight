# NeuralSight 👁️🧠

NeuralSight is an Android application that performs real-time object detection using a smartphone camera. It utilizes TensorFlow Lite and the Android CameraX API to process live camera feeds and draw dynamic bounding boxes around recognized objects directly on the screen.

## Features ✨
* **Real-Time Detection:** Processes live camera frames instantly.
* **Completely Offline:** The AI model runs entirely on the device processor without requiring an internet connection.
* **Dynamic UI Overlay:** Custom Canvas implementation that draws transparent bounding boxes, labels, and confidence scores accurately over detected objects.
* **80 Object Classes:** Capable of identifying everyday items (people, vehicles, electronics, furniture, etc.) using the COCO dataset.

## Tech Stack 🛠️
* **Language:** Kotlin
* **Camera Handling:** Android CameraX API
* **Machine Learning:** TensorFlow Lite (Task Vision & Support Libraries)
* **Pre-trained Model:** SSD MobileNet V1 (`ssd_mobilenet.tflite` with embedded metadata)

## How to Run 🚀
1. Clone the repository to your local machine.
2. Open the project in **Android Studio**.
3. Let Gradle sync and download the required dependencies.
4. Ensure your physical Android device is connected and **USB Debugging** is enabled.
5. Click the **Run** button to build and install the APK on your device.
*(Note: Testing on a physical device is highly recommended over an emulator for optimal camera performance).*

## Future Updates (Version 2) 🔮
Currently, the application uses a pre-trained MobileNet model built for speed. The next phase of development involves **Transfer Learning (Fine-Tuning)**. We plan to replace the base model with a custom-trained deep learning model to significantly increase detection accuracy and introduce custom, specialized object classes.
