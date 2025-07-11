/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package page.foliage.common.collect;

import page.foliage.guava.common.base.Preconditions;

/**
 * Twitter Snow Flake ID Generater.
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class Jsonflake {

    // ------------------------------------------------------------------------

    private final static long START_MILLIS = 170406720000L;

    private final static long BIT_SEQUENCE = 3;

    private final static long BIT_MACHINE = 0;

    private final static long BIT_DATACENTER = 0;

    private final static long MAX_SEQUENCE = -1L ^ (-1L << BIT_SEQUENCE);

    private final static long LEFT_MACHINE = BIT_SEQUENCE;

    private final static long LEFT_DATACENTER = BIT_SEQUENCE + BIT_MACHINE;

    private final static long LEFT_TIMESTMP = LEFT_DATACENTER + BIT_DATACENTER;

    private long did = 0L, mid = 0L, sequence = 0L, lastMillis = -1L;

    // ------------------------------------------------------------------------

    public Jsonflake() {}

    public synchronized long next() {
        long millis = millis();
        Preconditions.checkArgument(millis >= lastMillis, "Error! clock moved backwards");
        if (millis == lastMillis) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) millis = nextMillis(lastMillis);
        } else sequence = 0L;
        lastMillis = millis;
        return (millis - START_MILLIS) << LEFT_TIMESTMP | did << LEFT_DATACENTER | mid << LEFT_MACHINE | sequence;
    }

    private static long nextMillis(long currentMillis) {
        long millis = millis();
        while (millis <= currentMillis) {
            millis = millis();
        }
        return millis;
    }

    private static long millis() {
        return System.currentTimeMillis();
    }

}
