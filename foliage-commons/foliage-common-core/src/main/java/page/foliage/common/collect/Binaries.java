/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package page.foliage.common.collect;
import page.foliage.guava.common.primitives.Longs;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class Binaries {

    public static String formatBinary(long input) {
        byte[] bytes = Longs.toByteArray(input);
        StringBuilder builder = new StringBuilder(Longs.BYTES * 9);
        for (byte buf : bytes) {
            String str = Integer.toBinaryString(buf);
            str = String.format("%8s", str);
            str = str.replaceAll(" ", "0");
            builder.append(str).append(" ");
        }
        return builder.toString();
    }
    
    public static String formatHexadecimal(long input) {
        byte[] bytes = Longs.toByteArray(input);
        StringBuilder builder = new StringBuilder(Longs.BYTES * 2 + 2);
        builder.append("0x");
        for (byte buf : bytes) {
            String str = Long.toHexString(buf);
            str = String.format("%2s", str);
            str = str.replaceAll(" ", "0");
            str = str.toUpperCase();
            builder.append(str);
        }
        return builder.toString();
    }

}
