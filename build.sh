#!/bin/bash

# Stitch Launcher Build Script
# This script compiles the Android launcher APK

set -e

PROJECT_DIR="/mnt/project"
OUTPUT_DIR="/mnt/user-data/outputs"
ANDROID_SDK="${ANDROID_HOME:-/usr/local/android-sdk}"
BUILD_TOOLS_VERSION="34.0.0"

echo "🚀 Building Stitch Launcher..."
echo "Project dir: $PROJECT_DIR"

# Install Java if needed
if ! command -v javac &> /dev/null; then
    echo "Installing Java..."
    apt-get update -qq && apt-get install -y openjdk-17-jdk-headless > /dev/null 2>&1
fi

export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))

cd "$PROJECT_DIR"

# Download Gradle if not present
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Downloading Gradle wrapper..."
    mkdir -p gradle/wrapper
    curl -s "https://repo.gradle.org/gradle/distributions/gradle-8.4-bin.zip" -o gradle-8.4.zip
    unzip -q gradle-8.4.zip
    cp gradle-8.4/lib/gradle-core-8.4.jar gradle/wrapper/gradle-wrapper.jar || true
    rm -rf gradle-8.4 gradle-8.4.zip
fi

# Create gradlew if it doesn't exist
if [ ! -f "gradlew" ]; then
    echo "Creating Gradle wrapper script..."
    cat > gradlew << 'EOF'
#!/bin/bash
exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
EOF
    chmod +x gradlew
fi

echo "📦 Compiling APK..."

# Use gradle wrapper or gradle command
if command -v gradle &> /dev/null; then
    gradle assembleDebug -x lint --no-daemon --quiet
elif [ -f gradlew ]; then
    ./gradlew assembleDebug -x lint --no-daemon --quiet
else
    echo "❌ Gradle not found. Installing Gradle..."
    apt-get install -y gradle > /dev/null 2>&1
    gradle assembleDebug -x lint --no-daemon --quiet
fi

# Find and copy the APK
APK_PATH=$(find "$PROJECT_DIR" -name "*.apk" -type f | head -1)

if [ -z "$APK_PATH" ]; then
    echo "❌ APK not found. Build may have failed."
    exit 1
fi

mkdir -p "$OUTPUT_DIR"
cp "$APK_PATH" "$OUTPUT_DIR/stitch-launcher.apk"

echo "✅ Build complete!"
echo "📍 APK location: $OUTPUT_DIR/stitch-launcher.apk"
ls -lh "$OUTPUT_DIR/stitch-launcher.apk"
