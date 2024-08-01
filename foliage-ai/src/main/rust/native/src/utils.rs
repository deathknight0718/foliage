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
use lazy_static::lazy_static;

use std::collections::HashSet;
use std::path::Path;
use std::sync::{Arc, RwLock};

use std::backtrace::{Backtrace, BacktraceStatus};

use candle_core::{Device, Tensor};

use image::imageops::FilterType;
use image::Rgb;
use image::{DynamicImage, ImageBuffer, ImageReader};

// ----------------------------------------------------------------------------

#[derive(thiserror::Error, Debug)]
pub enum Error {
    #[error("Error! operation failed: {0}")]
    JNI(#[from] jni::errors::Error),

    #[error("Error! operation failed: {0}")]
    CANDLE(#[from] candle_core::error::Error),

    #[error("Error! operation failed: {0}")]
    JSON(#[from] serde_json::Error),

    #[error("Error! operation failed: {0}")]
    IO(#[from] std::io::Error),

    #[error("Error! operation failed: BOX")]
    BOX(),

    #[error("Error! operation failed: ENUM")]
    ENUM(),

    #[error("Error! operation failed: MODEL")]
    MODEL(),

    #[error(transparent)]
    WRAPPED(Box<dyn std::error::Error + Send + Sync>),

    #[error("{inner}\n{backtrace}")]
    BACKTRACE { inner: Box<Self>, backtrace: Box<Backtrace> },
}

impl Error {
    pub fn wrap(err: impl std::error::Error + Send + Sync + 'static) -> Self {
        Self::WRAPPED(Box::new(err)).bt()
    }

    pub fn bt(self) -> Self {
        let backtrace = Backtrace::capture();
        match backtrace.status() {
            BacktraceStatus::Disabled | BacktraceStatus::Unsupported => self,
            _ => Self::BACKTRACE { inner: Box::new(self), backtrace: Box::new(backtrace) },
        }
    }
}

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

// ----------------------------------------------------------------------------
pub struct Image {
    pub data: Tensor,
    pub ih: usize,
    pub iw: usize,
}

pub fn load_image<P: AsRef<Path>>(device: &Device, path: P, resize_longest: Option<usize>) -> Result<Image, Error> {
    let image = ImageReader::open(path)?.decode().map_err(Error::wrap)?;
    let (ih, iw) = (image.height() as usize, image.width() as usize);
    let image = match resize_longest {
        None => image,
        Some(resize_longest) => {
            let (height, width) = (image.height(), image.width());
            let resize_longest = resize_longest as u32;
            let (height, width) = if height < width {
                let h = (resize_longest * height) / width;
                (h, resize_longest)
            } else {
                let w = (resize_longest * width) / height;
                (resize_longest, w)
            };
            image.resize_exact(width, height, image::imageops::FilterType::CatmullRom)
        }
    };
    let (height, width) = (image.height() as usize, image.width() as usize);
    let image = image.to_rgb8();
    let data = image.into_raw();
    let data = Tensor::from_vec(data, (height, width, 3), device)?.permute((2, 0, 1))?;
    Ok(Image { data, ih, iw })
}

pub fn image_save<P: AsRef<Path>>(path: P, image: Image) -> Result<(), Error> {
    let p = path.as_ref();
    let (channel, height, width) = image.data.dims3()?;
    if channel != 3 {
        return Err(Error::MODEL());
    }
    let image_data = image.data.permute((1, 2, 0))?.flatten_all()?;
    let pixels = image_data.to_vec1::<u8>()?;
    let dynamic_image: ImageBuffer<Rgb<u8>, Vec<u8>> = ImageBuffer::from_raw(width as u32, height as u32, pixels).unwrap();
    let dynamic_image = DynamicImage::from(dynamic_image);
    let dynamic_image = dynamic_image.resize_to_fill(image.iw as u32, image.ih as u32, FilterType::CatmullRom);
    dynamic_image.save(p).map_err(Error::wrap)?;
    Ok(())
}
