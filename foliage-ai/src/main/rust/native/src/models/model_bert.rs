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
use candle_core::utils::cuda_is_available;
use candle_core::{Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::models::bert::{BertModel, Config, DTYPE};

use jni::objects::{JFloatArray, JLongArray, JObject, JObjectArray, JString, ReleaseMode};
use jni::sys::{jboolean, jint, jlong, jsize, JNI_FALSE, JNI_TRUE};
use jni::JNIEnv;

use tokenizers::tokenizer::{EncodeInput, Encoding};
use tokenizers::utils::padding::{PaddingParams, PaddingStrategy};
use tokenizers::utils::truncation::{TruncationParams, TruncationStrategy};
use tokenizers::Tokenizer;

use crate::utils::*;
use crate::Error;

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_isCudaAvailable<'local>(_: JNIEnv, _: JObject) -> jboolean {
    return if cuda_is_available() { JNI_TRUE } else { JNI_FALSE };
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerCreate<'local>(mut env: JNIEnv<'local>, _: JObject, path: JString<'local>) -> jlong {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerDelete<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) {
    let _ = box_delete_by_id::<Tokenizer>(tokenizer_id).inspect_err(|e| {
        env.throw(e.to_string()).unwrap();
    });
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerMaxLength<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> jint {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerStride<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> jint {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerPadToMultipleOf<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> jint {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerPaddingStrategy<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerPaddingUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString<'local>, pad_to_multiple_of: jint) {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerPaddingDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    if let Ok(tokenizer) = box_select_by_id::<Tokenizer>(tokenizer_id) {
        tokenizer.with_padding(None);
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerTruncationStrategy<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerTruncationUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString, truncation_stride: jint) {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenizerTruncationDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    if let Ok(tokenizer) = box_select_by_id::<Tokenizer>(tokenizer_id) {
        let _ = tokenizer.with_truncation(None);
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_encodingCreate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, input: JString, add_special_tokens: jboolean) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let sequence: String = env.get_string(&input)?.into();
        let encode_sequence = EncodeInput::Single(tokenizers::InputSequence::from(sequence));
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let encoding = tokenizer.encode_char_offsets(encode_sequence, add_special_tokens == JNI_TRUE).map_err(|_| Error::MODEL())?;
        return box_and_return_id(encoding);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_encodingsCreate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, inputs: JObjectArray<'local>, add_special_tokens: jboolean) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id)?;
        let len = env.get_array_length(&inputs)?;
        let mut vector: Vec<String> = Vec::new();
        for i in 0..len {
            let item = env.get_object_array_element(&inputs, i)?.into();
            let value: String = env.get_string(&item)?.into();
            vector.push(value);
        }
        let encodings = tokenizer.encode_batch_char_offsets(vector, add_special_tokens == JNI_TRUE).map_err(|_| Error::MODEL())?;
        return Ok(box_and_return_id(encodings)?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_encodingDelete<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) {
    if let Err(e) = box_delete_by_id::<Encoding>(encoding_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_encodingsDelete<'local>(mut env: JNIEnv<'local>, _: JObject, encodings_id: jlong) {
    if let Err(e) = box_delete_by_id::<Vec<Encoding>>(encodings_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokensGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JObjectArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenTypeIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_tokenWordIdsGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_attentionMaskGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_specialTokenMaskGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_decode<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, token_ids: JLongArray<'local>, skip_special_tokens: jboolean) -> JString<'local> {
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
        let decoding: String = tokenizer.decode(&*decode_ids, skip_special_tokens == JNI_TRUE).map_err(|_| Error::MODEL())?;
        return Ok(env.new_string(&decoding)?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return JString::from(JObject::null());
    })
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_modelCreate<'local>(mut env: JNIEnv<'local>, _: JObject, gpu_id: jint, path: JString<'local>, mode: JString<'local>) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let directory: String = env.get_string(&path)?.into();
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let data_config = std::fs::read_to_string(format!("{}/config.json", directory))?;
        let config: Config = serde_json::from_str(&data_config)?;
        let mode: String = env.get_string(&mode)?.into();
        let builder = match mode.as_str() {
            "PT" => Ok(VarBuilder::from_pth(format!("{}/pytorch_model.bin", directory), DTYPE, &device)?),
            "ST" => Ok(unsafe { VarBuilder::from_mmaped_safetensors(&[format!("{}/model.safetensors", directory)], DTYPE, &device)? }),
            _ => Err(Error::MODEL()),
        }?;
        let model = BertModel::load(builder, &config)?;
        Ok(box_and_return_id(model)?)
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_modelDelete<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong) {
    if let Err(e) = box_delete_by_id::<BertModel>(model_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_embeddingsCreate<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong, encoding_id: jlong) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let model = box_select_by_id::<BertModel>(model_id)?;
        let encoding = box_select_by_id::<Encoding>(encoding_id)?;
        let tokens = encoding.get_ids().to_vec();
        let token_ids = Tensor::new(&tokens[..], &model.device)?.unsqueeze(0)?;
        let token_type_ids = token_ids.zeros_like()?;
        return box_and_return_id(model.forward(&token_ids, &token_type_ids)?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_embeddingsCreateInBatch<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong, encodings_id: jlong) -> jlong {
    let result: Result<jlong, Error> = (|| {
        let model = box_select_by_id::<BertModel>(model_id)?;
        let encodings: &mut Vec<Encoding> = box_select_by_id::<Vec<Encoding>>(encodings_id)?;
        let mut token_ids: Vec<Tensor> = Vec::new();
        for (_, encoding) in encodings.iter().enumerate() {
            token_ids.push(Tensor::new(encoding.get_ids().to_vec().as_slice(), &model.device)?)
        }
        let token_ids = Tensor::stack(&token_ids, 0)?;
        let token_type_ids = token_ids.zeros_like()?;
        return box_and_return_id(model.forward(&token_ids, &token_type_ids)?);
    })();
    result.unwrap_or_else(|e| {
        env.throw(e.to_string()).unwrap();
        return 0;
    })
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_embeddingsDelete<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) {
    if let Err(e) = box_delete_by_id::<Tensor>(embeddings_id) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_embeddingsLastHiddenState<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) -> JObjectArray<'local> {
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
pub extern "system" fn Java_page_foliage_ai_bert_BertRustLibrary_embeddings<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) -> JObjectArray<'local> {
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
    use crate::Error;
    use anyhow::Result;
    use candle_core::Device;
    use candle_core::Tensor;
    use candle_nn::VarBuilder;
    use candle_transformers::models::bert::{BertModel, Config, DTYPE};
    use tokenizers::Tokenizer;

    fn model_create(gpu_id: i32, path: String) -> Result<BertModel, Error> {
        let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize)? } else { Device::Cpu };
        let file_config = format!("{}/config.json", path);
        let file_weights = format!("{}/pytorch_model.bin", path);
        let data_config = std::fs::read_to_string(file_config)?;
        let config: Config = serde_json::from_str(&data_config)?;
        let vb = VarBuilder::from_pth(file_weights, DTYPE, &device)?;
        return Ok(BertModel::load(vb, &config)?);
    }

    #[test]
    fn bert_batch() {
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
    fn bert_single() {
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
    fn sam() {
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
}
