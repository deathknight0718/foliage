/*
 * Copyright 2024 Foliage Develop Team.
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

pub fn box_and_return_id<T: 'static>(val: T) -> i64 {
    return Box::into_raw(Box::new(val)) as i64;
}

pub fn box_select_by_id<T>(handle: i64) -> &'static mut T {
    assert_ne!(handle, 0, "Invalid handle value");
    let ptr = handle as *mut T;
    return unsafe { &mut *ptr };
}

pub fn box_delete_by_id<T: 'static>(handle: i64) {
    unsafe {
        let _ = Box::from_raw(handle as *mut T);
    }
}
