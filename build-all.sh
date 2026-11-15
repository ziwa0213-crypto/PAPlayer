#!/usr/bin/env bash
# 三个变体用同一个版本号出包。
# 用法: ./build-all.sh            取当前北京时间
#       ./build-all.sh 2611141100 指定版本
set -euo pipefail
cd "$(dirname "$0")"
VN="${1:-$(TZ=Asia/Shanghai date +%y%m%d%H%M)}"
echo "统一版本号: $VN"
for v in debug release vision; do
  PA_VERSION_NAME="$VN" ./build.sh "$v"
done
