# PA Player

**Personalized × Animation** — 本地优先的 Android 音乐播放器。

Kotlin · Jetpack Compose · minSdk 33

---

## 状态

开发中。目前只有工程骨架：三变体构建、设计令牌、空壳首页。

## 构建

需要 JDK 21 与 Android SDK（platform `android-37.0`、build-tools `37.0.0`）。

```bash
./build.sh debug      # 调试包
./build.sh release    # 正式包
./build.sh vision     # 实验包（固定签名，可覆盖安装）
./build-all.sh        # 三变体共用同一个版本号
```

版本号在构建时取北京时间，格式 `YYMMDDHHmm`。

## 签名

三个变体各自使用独立密钥，放在 `keystore/` 下，不进版本库：

| 变体 | 包名 | 密钥 |
|---|---|---|
| release | `me.cwre.player` | `pa-release.jks` |
| debug | `me.cwre.player.debug` | `pa-debug.jks` |
| vision | `me.cwre.player.vision` | `pa-vision.jks` |

密码写在 `keystore/keystore.properties`。密钥丢了就无法覆盖安装，需要自行备份。

## License

Licensed under the GNU Affero General Public License v3.0 or later
([AGPL-3.0-or-later](https://spdx.org/licenses/AGPL-3.0-or-later.html)).
See [LICENSE](LICENSE) for the full text.
