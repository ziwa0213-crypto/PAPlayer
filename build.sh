#!/usr/bin/env bash
# 用法: ./build.sh debug|release|vision [--info]
set -uo pipefail
VAR="${1:-debug}"
EXTRA="${2:-}"

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-arm64}"
export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"

cd "$(dirname "$0")"

# 优先用装好的 gradle（本机免下载），没有才用 wrapper
if [ -x /opt/gradle/gradle-9.8.0/bin/gradle ]; then
  GRADLE=/opt/gradle/gradle-9.8.0/bin/gradle
elif command -v gradle >/dev/null 2>&1; then
  GRADLE=gradle
elif [ -x ./gradlew ]; then
  GRADLE=./gradlew
else
  echo "找不到 gradle，也找不到 ./gradlew" >&2
  exit 1
fi

case "$VAR" in
  debug)   TASK=assembleDebug ;;
  release) TASK=assembleRelease ;;
  vision)  TASK=assembleVision ;;
  *) echo "变体只能是 debug / release / vision" >&2; exit 2 ;;
esac

BAR="════════════════════════════════════════════════════════════════"
START=$(date +%s)

printf '\n%s\n  构建  %s    开始 %s\n%s\n\n' "$BAR" "$VAR" "$(date +%H:%M:%S)" "$BAR"

set +e
"$GRADLE" "$TASK" --console=plain $EXTRA 2>&1 | while IFS= read -r line; do
  printf '%s  %s\n' "$(date +%H:%M:%S)" "$line"
done
STATUS=${PIPESTATUS[0]}
set -e

ELAPSED=$(( $(date +%s) - START ))
DUR="$((ELAPSED / 60))m$(printf '%02d' $((ELAPSED % 60)))s"

if [ "$STATUS" -ne 0 ]; then
  printf '\n%s\n  构建失败  %s    耗时 %s\n  上面最后几行就是原因\n%s\n\n' "$BAR" "$VAR" "$DUR" "$BAR"
  exit "$STATUS"
fi

APK=$(find "app/build/outputs/apk/$VAR" -name "*.apk" | head -1)
INFO=$(python3 tools/apk-info.py "$APK")
VN=$(echo "$INFO" | awk '/versionName/{print $2}')
VC=$(echo "$INFO" | awk '/versionCode/{print $2}')
PKG=$(echo "$INFO" | awk '/package/{print $2}')
SIZE=$(du -h "$APK" | cut -f1)

APKSIGNER="$ANDROID_HOME/build-tools/37.0.0/apksigner"
CERT=$( [ -x "$APKSIGNER" ] && "$APKSIGNER" verify --print-certs "$APK" 2>/dev/null \
        | grep -m1 -oE 'SHA-256 digest: [0-9a-f]+' | awk '{print substr($3,1,8)}' || echo "—" )

mkdir -p dist
OUT="dist/PA-Player-$VAR-$VN.apk"
cp "$APK" "$OUT"

printf '\n%s\n' "$BAR"
printf '  构建成功 · %s\n' "$VAR"
printf '  版本      %s   (versionCode %s)\n' "$VN" "$VC"
printf '  包名      %s\n' "$PKG"
printf '  大小      %s\n' "$SIZE"
printf '  耗时      %s\n' "$DUR"
printf '  签名      %s\n' "$CERT"
printf '  产物      %s\n' "$(cd dist && pwd)/$(basename "$OUT")"
printf '%s\n\n' "$BAR"
