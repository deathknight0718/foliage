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
use crate::Error;

use lazy_static::lazy_static;

use std::collections::HashSet;
use std::sync::{Arc, RwLock};

// ----------------------------------------------------------------------------

lazy_static! {
    static ref BOX_ADDRESSES: Arc<RwLock<HashSet<i64>>> = Arc::new(RwLock::new(HashSet::new()));
}

// ----------------------------------------------------------------------------

pub fn box_and_return_id<T: 'static>(val: T) -> Result<i64, Error> {
    let address = Box::into_raw(Box::new(val)) as i64;
    BOX_ADDRESSES.write().map_err(|_| Error::BOX())?.insert(address);
    return Ok(address);
}

pub fn box_select_by_id<T>(handle: i64) -> Result<&'static mut T, Error> {
    if BOX_ADDRESSES.read().map_err(|_| Error::BOX())?.contains(&handle) {
        let ptr = handle as *mut T;
        return Ok(unsafe { &mut *ptr });
    } else {
        return Err(Error::BOX());
    }
}

pub fn box_delete_by_id<T: 'static>(handle: i64) -> Result<(), Error> {
    if BOX_ADDRESSES.write().map_err(|_| Error::BOX())?.remove(&handle) {
        let _ = unsafe { Box::from_raw(handle as *mut T) };
        return Ok(());
    } else {
        return Err(Error::BOX());
    }
}
