/*
 * Copyright 2023 Foliage Develop Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package page.foliage.func;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Flowable;

/**
 * 
 * @author deathknight0718@qq.com
 */
public interface Event<T extends Event<?>> extends Callable<T> {

    default <E extends Event<?>> Flowable<E> next() {
        return Flowable.empty();
    }

    default Flowable<T> callable() {
        return Flowable.fromCallable(this);
    }

}
