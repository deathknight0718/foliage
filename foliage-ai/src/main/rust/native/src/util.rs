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
use jni::errors::Error;

use std::collections::HashSet;
use std::sync::Mutex;
use std::sync::Once;

static mut BOX_ADDRESSES: Option<Mutex<HashSet<i64>>> = None;
static INIT: Once = Once::new();

fn init() {
    unsafe {
        INIT.call_once(|| BOX_ADDRESSES = Some(Mutex::new(HashSet::new())));
    }
}

pub fn box_and_return_id<T: 'static>(val: T) -> Result<i64, Error> {
    init();
    let address = Box::into_raw(Box::new(val)) as i64;
    unsafe {
        if let Some(ref mut addresses) = BOX_ADDRESSES {
            addresses.lock().unwrap().insert(address);
            return Ok(address);
        }
    }
    return Err(Error::NullPtr("BOX_ADDRESS"));
}

pub fn box_select_by_id<T>(handle: i64) -> Result<&'static mut T, Error> {
    unsafe {
        if let Some(ref mut addresses) = BOX_ADDRESSES {
            if addresses.lock().unwrap().contains(&handle) {
                let ptr = handle as *mut T;
                return Ok(&mut *ptr);
            }
        }
    }
    return Err(Error::NullPtr("BOX_ADDRESS"));
}

pub fn box_delete_by_id<T: 'static>(handle: i64) -> Result<(), Error> {
    unsafe {
        if let Some(ref mut addresses) = BOX_ADDRESSES {
            if addresses.lock().unwrap().contains(&handle) {
                addresses.lock().unwrap().remove(&handle);
                let _ = Box::from_raw(handle as *mut T);
                return Ok(());
            }
        }
    }
    return Err(Error::NullPtr("BOX_ADDRESS"));
}
