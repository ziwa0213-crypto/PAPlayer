#!/usr/bin/env python3
"""读 APK 里的二进制 AndroidManifest.xml，打印包名/版本/minSdk/label。
沙盒里 aapt2 dump 会 SIGILL，所以自己解析。只依赖标准库。
用法: apk-info.py <apk> [...]
"""
import struct, sys, zipfile

CHUNK_STRING_POOL = 0x0001
CHUNK_START_ELEMENT = 0x0102


def read_string_pool(data, off):
    _, header_size, _size = struct.unpack_from("<HHI", data, off)
    count, _style_count, flags, strings_start, _styles_start = struct.unpack_from("<IIIII", data, off + 8)
    utf8 = bool(flags & 0x100)
    offsets = struct.unpack_from("<%dI" % count, data, off + header_size)
    base = off + strings_start
    out = []
    for o in offsets:
        p = base + o
        if utf8:
            n = data[p]
            p += 2 if n & 0x80 else 1
            n = data[p]
            if n & 0x80:
                n = ((n & 0x7F) << 8) | data[p + 1]
                p += 2
            else:
                p += 1
            out.append(data[p:p + n].decode("utf-8", "replace"))
        else:
            n = struct.unpack_from("<H", data, p)[0]
            p += 2
            if n & 0x8000:
                n = ((n & 0x7FFF) << 16) | struct.unpack_from("<H", data, p)[0]
                p += 2
            out.append(data[p:p + n * 2].decode("utf-16-le", "replace"))
    return out


def attr_value(data, strings, dtype, data_val, raw):
    if dtype == 0x03:
        return strings[data_val] if data_val < len(strings) else ""
    if dtype == 0x01:
        return "@0x%08x" % data_val
    if dtype == 0x12:
        return "0x%x" % data_val
    if dtype in (0x10, 0x11):
        return str(struct.unpack("<i", struct.pack("<I", data_val))[0])
    if raw != 0xFFFFFFFF and raw < len(strings):
        return strings[raw]
    return str(data_val)


def parse_manifest(data):
    strings = []
    result = {}
    off = 8  # 跳过 RES_XML header
    total = len(data)
    while off + 8 <= total:
        ctype, header_size, csize = struct.unpack_from("<HHI", data, off)
        if csize == 0:
            break
        if ctype == CHUNK_STRING_POOL:
            strings = read_string_pool(data, off)
        elif ctype == CHUNK_START_ELEMENT:
            name_idx = struct.unpack_from("<I", data, off + 20)[0]
            attr_count = struct.unpack_from("<H", data, off + 28)[0]
            attr_off = off + header_size
            tag = strings[name_idx] if name_idx < len(strings) else ""
            attrs = {}
            for i in range(attr_count):
                ao = attr_off + i * 20
                a_name = struct.unpack_from("<I", data, ao + 4)[0]
                a_raw = struct.unpack_from("<I", data, ao + 8)[0]
                a_dtype = data[ao + 15]
                a_data = struct.unpack_from("<I", data, ao + 16)[0]
                aname = strings[a_name] if a_name < len(strings) else ""
                attrs[aname] = attr_value(data, strings, a_dtype, a_data, a_raw)
            if tag == "manifest" and "package" not in result:
                result["package"] = attrs.get("package", "")
                result["versionCode"] = attrs.get("versionCode", "")
                result["versionName"] = attrs.get("versionName", "")
            elif tag == "uses-sdk":
                result.setdefault("minSdk", attrs.get("minSdkVersion", ""))
                result.setdefault("targetSdk", attrs.get("targetSdkVersion", ""))
            elif tag == "application":
                result.setdefault("label", attrs.get("label", ""))
        off += csize
    return result


def main(paths):
    for p in paths:
        with zipfile.ZipFile(p) as z:
            info = parse_manifest(z.read("AndroidManifest.xml"))
        print(p)
        for k in ("package", "versionName", "versionCode", "minSdk", "targetSdk", "label"):
            if info.get(k):
                print("  %-12s %s" % (k, info[k]))
        print()


if __name__ == "__main__":
    main(sys.argv[1:])
