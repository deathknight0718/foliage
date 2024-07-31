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
mod utils;

use candle::utils::cuda_is_available;
use candle::{Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::models::bert::{BertModel, Config, DTYPE};

use jni::objects::{JFloatArray, JLongArray, JObject, JObjectArray, JString, ReleaseMode};
use jni::sys::{jboolean, jint, jlong, jsize, JNI_FALSE, JNI_TRUE};
use jni::JNIEnv;

use tokenizers::tokenizer::{EncodeInput, Encoding};
use tokenizers::utils::padding::{PaddingParams, PaddingStrategy};
use tokenizers::utils::truncation::{TruncationParams, TruncationStrategy};
use tokenizers::Tokenizer;
use utils::*;

// ----------------------------------------------------------------------------

#[derive(thiserror::Error, Debug)]
pub enum Error {
    #[error("Error! operation failed: {0}")]
    JNI(#[from] jni::errors::Error),

    #[error("Error! operation failed: {0}")]
    CANDLE(#[from] candle::error::Error),

    #[error("Error! operation failed: {0}")]
    JSON(#[from] serde_json::Error),

    #[error("Error! operation failed: {0}")]
    IO(#[from] std::io::Error),

    #[error("Error! operation failed: BOX")]
    BOX(),

    #[error("Error! operation failed: ENUM")]
    ENUM(),
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_isCudaAvailable<'local>(_: JNIEnv, _: JObject) -> jboolean {
    return if cuda_is_available() { JNI_TRUE } else { JNI_FALSE };
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerCreate<'local>(mut env: JNIEnv, _: JObject, path: JString) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let file: String = env.get_string(&path)?.into();
        return box_and_return_id(Tokenizer::from_file(&file));
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerDelete(mut env: JNIEnv, _: JObject, tokenizer_id: jlong) {
    let _ = box_delete_by_id::<Tokenizer>(tokenizer_id).inspect_err(|e| {
        env.throw(e.to_string()).unwrap();
    });
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerMaxLength(mut env: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let result: Result<jint, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let max_length = tokenizer.get_truncation().map(|v| v.max_length as jint).unwrap_or(-1);
        if max_length != -1 {
            return Ok(max_length);
        }
        return Ok(tokenizer.get_padding().map_or(-1, |param| if let PaddingStrategy::Fixed(i) = param.strategy { i as jint } else { -1 }));
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return -1;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerStride(mut env: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let result: Result<jint, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        return Ok(tokenizer.get_truncation().map(|v| v.stride as jint).unwrap_or(0));
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPadToMultipleOf(mut env: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let result: Result<jint, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        return Ok(tokenizer.get_padding().map(|v| v.pad_to_multiple_of.unwrap_or(0) as jint).unwrap_or(0));
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingStrategy<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
    let result: Result<JString, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let padding = tokenizer.get_padding();
        let strategy = match padding {
            Some(param) => match param.strategy {
                PaddingStrategy::BatchLongest => "LONGEST",
                _ => "MAX_LENGTH",
            },
            None => "DO_NOT_PAD",
        };
        return Ok(env.new_string(strategy.to_string())?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JString::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString, pad_to_multiple_of: jint) {
    let result: Result<(), Error> = (|| {
        let strategy: String = env.get_string(&strategy)?.into();
        let len = max_length as usize;
        let ref_strategy = match strategy.as_ref() {
            "LONGEST" => Ok(PaddingStrategy::BatchLongest),
            "MAX_LENGTH" => Ok(PaddingStrategy::Fixed(len)),
            _ => Err(Error::ENUM()),
        };
        let ref_pad_to_multiple_of = match pad_to_multiple_of as usize {
            0 => None,
            val => Some(val),
        };
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        if let Some(padding_params) = tokenizer.get_padding_mut() {
            padding_params.strategy = ref_strategy?;
            padding_params.pad_to_multiple_of = ref_pad_to_multiple_of;
        } else {
            let padding_params = PaddingParams { strategy: ref_strategy?, pad_to_multiple_of: ref_pad_to_multiple_of, ..Default::default() };
            tokenizer.with_padding(Some(padding_params));
        }
        return Ok(());
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
    });
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    if let Ok(tokenizer) = box_select_by_id::<Tokenizer>(tokenizer_id) {
        tokenizer.with_padding(None);
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationStrategy<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
    let result: Result<JString, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let strategy = tokenizer.get_truncation().map(|v| v.strategy.as_ref()).unwrap_or("DO_NOT_TRUNCATE");
        return Ok(env.new_string(strategy.to_string())?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JString::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString, truncation_stride: jint) {
    let result: Result<(), Error> = (|| {
        let strategy: String = env.get_string(&strategy)?.into();
        let ref_strategy = match strategy.as_ref() {
            "LONGEST_FIRST" => Ok(TruncationStrategy::LongestFirst),
            "ONLY_FIRST" => Ok(TruncationStrategy::OnlyFirst),
            "ONLY_SECOND" => Ok(TruncationStrategy::OnlySecond),
            _ => Err(Error::ENUM()),
        };
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        if let Some(truncation_params) = tokenizer.get_truncation_mut() {
            truncation_params.strategy = ref_strategy?;
            truncation_params.stride = truncation_stride as usize;
            truncation_params.max_length = max_length as usize;
        } else {
            let truncation_params = TruncationParams {
                strategy: ref_strategy?,
                stride: truncation_stride as usize,
                max_length: max_length as usize,
                ..Default::default()
            };
            let _ = tokenizer.with_truncation(Some(truncation_params));
        }
        return Ok(());
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
    });
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    if let Ok(tokenizer) = box_select_by_id::<Tokenizer>(tokenizer_id) {
        let _ = tokenizer.with_truncation(None);
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_encodingCreate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, input: JString, add_special_tokens: jboolean) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let sequence: String = env.get_string(&input)?.into();
        let encode_sequence = EncodeInput::Single(tokenizers::InputSequence::from(sequence));
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let encoding = tokenizer.encode_char_offsets(encode_sequence, add_special_tokens == JNI_TRUE).unwrap();
        return box_and_return_id(encoding);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_encodingDelete(mut env: JNIEnv, _: JObject, encoding_id: jlong) {
    if let Err(e) = box_delete_by_id::<Encoding>(encoding_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokensGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JObjectArray<'local> {
    let result: Result<JObjectArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let tokens = encoding.get_tokens();
        let len = tokens.len() as jsize;
        let array = env.new_object_array(len, "java/lang/String", JObject::null())?;
        for (i, token) in tokens.iter().enumerate() {
            let item: JString = env.new_string(&token)?;
            env.set_object_array_element(&array, i as jsize, item)?;
        }
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JObjectArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let result: Result<JLongArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let ids = encoding.get_ids();
        let len = ids.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        long_ids.reserve(len as usize);
        for i in ids {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len)?;
        env.set_long_array_region(&array, 0, &long_ids)?;
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JLongArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenTypeIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let result: Result<JLongArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let type_ids = encoding.get_type_ids();
        let len = type_ids.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in type_ids {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len)?;
        env.set_long_array_region(&array, 0, &long_ids)?;
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JLongArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenWordIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let result: Result<JLongArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let word_ids = encoding.get_word_ids();
        let len = word_ids.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in word_ids {
            if let Some(word_id) = i {
                long_ids.push(*word_id as jlong)
            } else {
                long_ids.push(-1)
            }
        }
        let array = env.new_long_array(len)?;
        env.set_long_array_region(&array, 0, &long_ids)?;
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JLongArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_attentionMaskGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let result: Result<JLongArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let attention_masks = encoding.get_attention_mask();
        let len = attention_masks.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in attention_masks {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len)?;
        env.set_long_array_region(&array, 0, &long_ids)?;
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JLongArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_specialTokenMaskGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let result: Result<JLongArray, Error> = (|| {
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let special_token_masks = encoding.get_special_tokens_mask();
        let len = special_token_masks.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in special_token_masks {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len)?;
        env.set_long_array_region(&array, 0, &long_ids)?;
        return Ok(array);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JLongArray::from(JObject::null());
    })
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_decode<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, token_ids: JLongArray<'local>, skip_special_tokens: jboolean) -> JString<'local> {
    let result: Result<JString, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let long_ids = unsafe { env.get_array_elements(&token_ids, ReleaseMode::NoCopyBack) }?;
        let long_ids_ptr = long_ids.as_ptr();
        let len = long_ids.len();
        let mut decode_ids: Vec<u32> = Vec::new();
        for i in 0..len {
            unsafe {
                let val = long_ids_ptr.add(i);
                decode_ids.push(*val as u32);
            }
        }
        let decoding: String = tokenizer.decode(&*decode_ids, skip_special_tokens == JNI_TRUE).unwrap();
        return Ok(env.new_string(&decoding)?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JString::from(JObject::null());
    })
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_modelCreate<'local>(mut env: JNIEnv<'local>, _: JObject, gpu_id: jint, path: JString<'local>) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let directory: String = env.get_string(&path)?.into();
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let data_config = std::fs::read_to_string(format!("{}/config.json", directory))?;
        let config: Config = serde_json::from_str(&data_config)?;
        let vb = VarBuilder::from_pth(format!("{}/pytorch_model.bin", directory), DTYPE, &device)?;
        let model = BertModel::load(vb, &config)?;
        Ok(box_and_return_id(model)?)
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_modelDelete(mut env: JNIEnv, _: JObject, model_id: jlong) {
    if let Err(e) = box_delete_by_id::<BertModel>(model_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddingsCreate<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong, encoding_id: jlong) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let model = box_select_by_id::<BertModel>(model_id)?;
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let tokens = encoding.get_ids().to_vec();
        let token_ids = Tensor::new(&tokens[..], &model.device)?.unsqueeze(0)?;
        let token_type_ids = token_ids.zeros_like()?;
        let embeddings = model.forward(&token_ids, &token_type_ids)?;
        return box_and_return_id(embeddings);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddingsDelete(mut env: JNIEnv, _: JObject, embeddings_id: jlong) {
    if let Err(e) = box_delete_by_id::<Tensor>(embeddings_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddingsLastHiddenState<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) -> JObjectArray<'local> {
    let result: Result<JObjectArray, Error> = (|| {
        let embeddings = box_select_by_id::<Tensor>(embeddings_id)?;
        let vec_3d = embeddings.to_vec3()?;
        let float_2d_class = env.find_class("[F")?;
        let float_3d_class = env.find_class("[[F")?;
        let array_3d: JObjectArray = env.new_object_array(vec_3d.len() as jsize, float_3d_class, JObject::null())?;
        for (i_2d, vec_2d) in vec_3d.iter().enumerate() {
            let array_2d: JObjectArray = env.new_object_array(vec_2d.len() as jsize, &float_2d_class, JObject::null())?;
            for (i_1d, vec_1d) in vec_2d.iter().enumerate() {
                let array_1d: JFloatArray = env.new_float_array(vec_1d.len() as jsize)?;
                env.set_float_array_region(&array_1d, 0, &vec_1d)?;
                env.set_object_array_element(&array_2d, i_1d as jsize, array_1d)?;
            }
            env.set_object_array_element(&array_3d, i_2d as jsize, array_2d)?;
        }
        return Ok(array_3d);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JObjectArray::from(JObject::null());
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddings<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) -> JObjectArray<'local> {
    let result: Result<JObjectArray, Error> = (|| {
        let embeddings = box_select_by_id::<Tensor>(embeddings_id)?;
        let (_n_sentence, n_tokens, _hidden_size) = embeddings.dims3()?;
        let mean_pool = (embeddings.sum(1)? / (n_tokens as f64))?;
        let vec_2d: Vec<Vec<f32>> = mean_pool.to_vec2::<f32>()?;
        let len_2d = vec_2d.len() as jsize;
        let float_2d_class = env.find_class("[F")?;
        let array_2d: JObjectArray = env.new_object_array(len_2d, float_2d_class, JObject::null())?;
        for (i_id, vec_1d) in vec_2d.iter().enumerate() {
            let array_1d: JFloatArray = env.new_float_array(vec_1d.len() as jsize)?;
            env.set_float_array_region(&array_1d, 0, &vec_1d)?;
            env.set_object_array_element(&array_2d, i_id as jsize, array_1d)?;
        }
        return Ok(array_2d);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JObjectArray::from(JObject::null());
    })
}

// ----------------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use anyhow::Result;
    use candle::Tensor;
    use candle::{Device, Error};
    use candle_nn::VarBuilder;
    use candle_transformers::models::bert::{BertModel, Config, DTYPE};
    use tokenizers::Tokenizer;

    use crate::{box_and_return_id, box_delete_by_id};

    fn model_create(gpu_id: i32, path: String) -> Result<BertModel, Error> {
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let file_config = format!("{}/config.json", path);
        let file_weights = format!("{}/pytorch_model.bin", path);
        let data_config = std::fs::read_to_string(file_config).unwrap();
        let config: Config = serde_json::from_str(&data_config).unwrap();
        let vb = VarBuilder::from_pth(file_weights, DTYPE, &device).unwrap();
        return BertModel::load(vb, &config);
    }

    #[test]
    fn it_works_batch() {
        let result = model_create(0, "/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2".to_string());
        assert!(result.is_ok());
        let model = result.expect("Couldn't create model!");
        let tokenizer = Tokenizer::from_file("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2/tokenizer.json").unwrap();
        let sentences = ["床前明月光", "举头望明月"];
        let tokens = tokenizer.encode_batch(sentences.to_vec(), true).unwrap();
        let token_ids = tokens.iter().map(|tokens| Ok(Tensor::new(tokens.get_ids().to_vec().as_slice(), &model.device)?)).collect::<Result<Vec<_>>>().unwrap();
        let token_ids = Tensor::stack(&token_ids, 0).unwrap();
        let token_type_ids = token_ids.zeros_like().unwrap();
        let embeddings = model.forward(&token_ids, &token_type_ids).unwrap();
        let (_n_sentence, n_tokens, _hidden_size) = embeddings.dims3().unwrap();
        let embeddings = (embeddings.sum(1).unwrap() / (n_tokens as f64)).unwrap();
        println!("pooled embeddings {:?}", embeddings.to_vec2::<f32>());
    }

    #[test]
    fn it_works_single() {
        let result = model_create(0, "/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2".to_string());
        assert!(result.is_ok());
        let model = result.expect("Couldn't create model!");
        let tokenizer = Tokenizer::from_file("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2/tokenizer.json").unwrap();
        let tokens = tokenizer.encode("床前明月光", true).unwrap().get_ids().to_vec();
        let token_ids = Tensor::new(&tokens[..], &model.device).unwrap().unsqueeze(0).unwrap();
        let token_type_ids = token_ids.zeros_like().unwrap();
        let embeddings = model.forward(&token_ids, &token_type_ids).unwrap();
        let (_n_sentence, n_tokens, _hidden_size) = embeddings.dims3().unwrap();
        let embeddings = (embeddings.sum(1).unwrap() / (n_tokens as f64)).unwrap();
        println!("embeddings {:?}", embeddings.to_vec2::<f32>());
    }

    #[test]
    fn it_works() {
        let result = model_create(0, "/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2".to_string());
        assert!(result.is_ok());
        let model = result.expect("Couldn't create model!");
        let tokenizer = Tokenizer::from_file("/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2/tokenizer.json").unwrap();
        let tokens = tokenizer.encode("床前明月光", true).unwrap().get_ids().to_vec();
        let token_ids = Tensor::new(&tokens[..], &model.device).unwrap().unsqueeze(0).unwrap();
        let token_type_ids = token_ids.zeros_like().unwrap();
        let embeddings = model.forward(&token_ids, &token_type_ids).unwrap();
        println!("embeddings {:?}", embeddings.to_vec3::<f32>());
    }

    #[test]
    fn it_works_point() {
        let result: BertModel = model_create(0, "/home/foliage/model/paraphrase-multilingual-MiniLM-L12-v2".to_string()).unwrap();
        let pointer = box_and_return_id(result).unwrap();
        box_delete_by_id::<BertModel>(pointer).unwrap();
        box_delete_by_id::<BertModel>(pointer).expect("test");
    }
}
