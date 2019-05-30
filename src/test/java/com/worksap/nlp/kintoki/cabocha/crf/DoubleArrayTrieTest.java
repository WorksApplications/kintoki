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

package com.worksap.nlp.kintoki.cabocha.crf;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DoubleArrayTrieTest {

    // double-array trie includes "a", "ab", and "c" made by mkdarts
    static byte[] daBytes = new byte[0xe48];
    static {
        daBytes[0x000] = (byte) 0x01;

        daBytes[0x318] = (byte) 0x64;
        daBytes[0x31c] = (byte) 0x01;

        daBytes[0x320] = (byte) 0xff;
        daBytes[0x321] = (byte) 0xff;
        daBytes[0x322] = (byte) 0xff;
        daBytes[0x323] = (byte) 0xff;
        daBytes[0x324] = (byte) 0x64;
        daBytes[0x328] = (byte) 0x67;
        daBytes[0x32c] = (byte) 0x01;

        daBytes[0x330] = (byte) 0xfe;
        daBytes[0x331] = (byte) 0xff;
        daBytes[0x332] = (byte) 0xff;
        daBytes[0x333] = (byte) 0xff;
        daBytes[0x334] = (byte) 0x66;
        daBytes[0x338] = (byte) 0xfd;
        daBytes[0x339] = (byte) 0xff;
        daBytes[0x33a] = (byte) 0xff;
        daBytes[0x33b] = (byte) 0xff;
        daBytes[0x33c] = (byte) 0x67;

        daBytes[0x638] = (byte) 0x66;
        daBytes[0x63c] = (byte) 0x64;
    }

    DoubleArrayTrie dat;

    @Before
    public void setUp() {
        dat = new DoubleArrayTrie();
        ByteBuffer array = ByteBuffer.wrap(daBytes);
        array.order(ByteOrder.LITTLE_ENDIAN);
        dat.setArray(array, daBytes.length);
    }

    @Test
    public void exactMatchSearch() {
        assertThat(dat.exactMatchSearch("a"), is(0));
        assertThat(dat.exactMatchSearch("ab"), is(1));
        assertThat(dat.exactMatchSearch("c"), is(2));
        assertThat(dat.exactMatchSearch("b"), is(-1));
    }
}
