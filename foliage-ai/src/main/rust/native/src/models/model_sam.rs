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
use candle_core::DType;
use candle_core::Device;
use candle_nn::VarBuilder;
use candle_transformers::models::segment_anything::sam::{Sam, IMAGE_SIZE};

use jni::objects::{JObject, JString};
use jni::sys::{jboolean, jint, jlong, JNI_FALSE, JNI_TRUE};
use jni::JNIEnv;

use crate::utils::*;

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_sam_SamLibrary_isCudaAvailable<'local>(_: JNIEnv, _: JObject) -> jboolean {
    return if candle_core::utils::cuda_is_available() { JNI_TRUE } else { JNI_FALSE };
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_sam_SamLibrary_modelCreate<'local>(mut env: JNIEnv<'local>, _: JObject, gpu_id: jint, path: JString<'local>, mode: JString<'local>) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let directory: String = env.get_string(&path)?.into();
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let mode: String = env.get_string(&mode)?.into();
        let builder = match mode.as_str() {
            "VIT_H" => Ok(unsafe { VarBuilder::from_mmaped_safetensors(&[format!("{}/model_vit_h.safetensors", directory)], DType::F32, &device)? }),
            "VIT_L" => Ok(unsafe { VarBuilder::from_mmaped_safetensors(&[format!("{}/model_vit_l.safetensors", directory)], DType::F32, &device)? }),
            "VIT_B" => Ok(unsafe { VarBuilder::from_mmaped_safetensors(&[format!("{}/model_vit_b.safetensors", directory)], DType::F32, &device)? }),
            _ => Err(Error::MODEL()),
        }?;
        let model = Sam::new_tiny(builder);
        Ok(box_and_return_id(model)?)
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_sam_SamLibrary_modelDelete<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong) {
    if let Err(e) = box_delete_by_id::<Sam>(model_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_sam_SamLibrary_mask<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong, gpu_id: jint, image_path: JString<'local>) {
    let result: Result<(), Error> = (|| {
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let path: String = env.get_string(&image_path)?.into();
        let image = load_image(&device, &path, Some(IMAGE_SIZE))?;
        let image_data = image.data.to_device(&device)?;
        let model = box_select_by_id::<Sam>(model_id)?;
        let bboxes = model.generate_masks(&image_data, 32, 0, 512. / 1500., 1)?;
        for (idx, bbox) in bboxes.iter().enumerate() {
            let mask = (&bbox.data.to_dtype(DType::U8)? * 255.)?;
            let (h, w) = mask.dims2()?;
            let mask = mask.broadcast_as((3, h, w))?;
            image_save(format!("sam_mask{idx}.png"), Image { data: mask, ih: image.ih, iw: image.iw })?;
        }
        return Ok(());
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
    })
}

// ----------------------------------------------------------------------------

#[cfg(test)]
mod tests {

    use candle_core::DType;
    use candle_core::Device;
    use candle_nn::VarBuilder;
    use candle_transformers::models::segment_anything::sam::{Sam, IMAGE_SIZE};

    use crate::utils::*;

    #[test]
    fn sam() {
        let result: Result<(), Error> = (|| {
            let device = Device::new_cuda(0)?;
            let builder = unsafe { VarBuilder::from_mmaped_safetensors(&["/home/foliage/model/segment-anything/model_vit_b.safetensors"], DType::F32, &device)? };
            let model = Sam::new(768, 12, 12, &[2, 5, 8, 11], builder)?;
            let path = "sample.jpg".to_string();
            let image = load_image(&device, &path, Some(IMAGE_SIZE))?;
            let image_data = image.data.to_device(&device)?;
            let bboxes = model.generate_masks(&image_data, /* points_per_side */ 32, /* crop_n_layer */ 0, /* crop_overlap_ratio */ 512. / 1500., /* crop_n_points_downscale_factor */ 1)?;
            for (idx, bbox) in bboxes.iter().enumerate() {
                let mask = (&bbox.data.to_dtype(DType::U8)? * 255.)?;
                let (h, w) = mask.dims2()?;
                let mask = mask.broadcast_as((3, h, w))?;
                image_save(format!("sam_mask{idx}.png"), Image { data: mask, ih: image.ih, iw: image.iw })?;
            }
            return Ok(());
        })();
        result.unwrap_or_else(|e| {
            println!("failed {}", e.to_string());
        });
    }

    #[test]
    fn sam_points() {
        let result: Result<(), Error> = (|| {
            let device = Device::new_cuda(0)?;
            let builder = unsafe { VarBuilder::from_mmaped_safetensors(&["/home/foliage/model/segment-anything/model_vit_b.safetensors"], DType::F32, &device)? };
            // let model = Sam::new(1024, 24, 16, &[5, 11, 17, 23], builder)?;
            let model = Sam::new(768, 12, 12, &[2, 5, 8, 11], builder)?;
            let path: String = "sample.jpg".to_string();
            let image = load_image(&device, &path, Some(IMAGE_SIZE))?;
            let points = [(0.8, 0.62, true)];
            let points = points.to_vec();
            let (mask, iou_predictions) = model.forward(&image.data, &points, false)?;
            println!("mask:\n{mask}");
            println!("iou_predictions: {iou_predictions}");
            let mask = (mask.ge(0.)? * 255.)?;
            let (_, h, w) = mask.dims3()?;
            let mask = mask.expand((3, h, w))?;
            let mask_pixels = mask.permute((1, 2, 0))?.flatten_all()?.to_vec1::<u8>()?;
            let mut image = image::ImageReader::open(&path)?.decode().map_err(Error::wrap)?;
            let mask_image: image::ImageBuffer<image::Rgb<u8>, Vec<u8>> = image::ImageBuffer::from_raw(w as u32, h as u32, mask_pixels).unwrap();
            let mask_image = image::DynamicImage::from(mask_image).resize_to_fill(image.width(), image.height(), image::imageops::FilterType::CatmullRom);
            for x in 0..image.width() {
                for y in 0..image.height() {
                    let mask_point = imageproc::drawing::Canvas::get_pixel(&mask_image, x, y);
                    if mask_point.0[0] > 100 {
                        let mut image_point = imageproc::drawing::Canvas::get_pixel(&image, x, y);
                        image_point.0[2] = 255 - (255 - image_point.0[2]) / 2;
                        image_point.0[1] /= 2;
                        image_point.0[0] /= 2;
                        imageproc::drawing::Canvas::draw_pixel(&mut image, x, y, image_point)
                    }
                }
            }
            for (x, y, b) in points {
                let x = (x * image.width() as f64) as i32;
                let y = (y * image.height() as f64) as i32;
                let color = if b { image::Rgba([255, 0, 0, 200]) } else { image::Rgba([0, 255, 0, 200]) };
                imageproc::drawing::draw_filled_circle_mut(&mut image, (x, y), 3, color);
            }
            image.save("merged.jpg").map_err(Error::wrap)?;
            return Ok(());
        })();
        result.unwrap_or_else(|e| {
            println!("failed {}", e.to_string());
        });
    }
}
