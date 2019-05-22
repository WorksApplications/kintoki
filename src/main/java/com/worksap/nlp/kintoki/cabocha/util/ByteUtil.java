/*
 * Copyright 2019 Works Applications Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worksap.nlp.kintoki.cabocha.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ByteUtil
{
    private static final ByteOrder ORDER = ByteOrder.LITTLE_ENDIAN;

    public static ByteBuffer readAsByteBuffer(String path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(Paths.get(path))) {
            int size = (int)channel.size();
            ByteBuffer bytes = ByteBuffer.allocate(size);
            channel.read(bytes);
            bytes.order(ORDER);
            bytes.flip();
            return bytes;
        }
    }

    public static String getString(ByteBuffer bytes, int byteSize, Charset charset) {
        byte[] array = new byte[byteSize];
        bytes.get(array);
        return new String(array, charset);
    }

    public static IntBuffer getIntBuffer(ByteBuffer bytes, int byteSize) {
        ByteBuffer newBytes = bytes.slice();
        newBytes.order(bytes.order());
        bytes.position(bytes.position() + byteSize);
        return newBytes.asIntBuffer();
    }

}
