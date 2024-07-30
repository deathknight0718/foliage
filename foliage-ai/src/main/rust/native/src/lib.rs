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

use std::str::FromStr;

use candle::utils::cuda_is_available;
use candle::{Device, Tensor};
use candle_nn::VarBuilder;
use candle_transformers::models::bert::{BertModel, Config, DTYPE};

use jni::objects::{JFloatArray, JLongArray, JObject, JObjectArray, JString, JValue, ReleaseMode};
use jni::sys::{jboolean, jint, jlong, jobject, jsize, JNI_FALSE, JNI_TRUE};
use jni::JNIEnv;

use tokenizers::tokenizer::{EncodeInput, Encoding};
use tokenizers::utils::padding::{PaddingParams, PaddingStrategy};
use tokenizers::utils::truncation::{TruncationParams, TruncationStrategy};
use tokenizers::Tokenizer;

use utils::*;

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_isCudaAvailable<'local>(_: JNIEnv, _: JObject) -> jboolean {
    return if cuda_is_available() { JNI_TRUE } else { JNI_FALSE };
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerCreate<'local>(mut env: JNIEnv, _: JObject, json: JString) -> jlong {
    let data: String = env.get_string(&json).expect("Couldn't get java json data!").into();
    let tokenizer = Tokenizer::from_str(&data);
    match tokenizer {
        Ok(out) => box_and_return_id(out),
        Err(err) => {
            env.throw(err.to_string()).unwrap();
            return 0;
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerDelete(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    box_delete_by_id::<Tokenizer>(tokenizer_id);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerMaxLength(_: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let truncation = tokenizer.get_truncation();
    let mut max_length = match truncation {
        Some(val) => val.max_length as jint,
        None => -1,
    };
    if max_length == -1 {
        let padding = tokenizer.get_padding();
        max_length = match padding {
            Some(param) => match param.strategy {
                PaddingStrategy::Fixed(i) => i as jint,
                _ => -1,
            },
            _ => -1,
        };
    }
    return max_length;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerStride(_: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let truncation = tokenizer.get_truncation();
    let ret = match truncation {
        Some(val) => val.stride,
        None => 0,
    };
    return ret as jint;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPadToMultipleOf(_: JNIEnv, _: JObject, tokenizer_id: jlong) -> jint {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let padding = tokenizer.get_padding();
    let ret = match padding {
        Some(val) => val.pad_to_multiple_of.unwrap_or(0),
        None => 0,
    };
    return ret as jint;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingStrategy<'local>(env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let padding = tokenizer.get_padding();
    let strategy = match padding {
        Some(val) => match val.strategy {
            PaddingStrategy::BatchLongest => "LONGEST",
            _ => "MAX_LENGTH",
        },
        None => "DO_NOT_PAD",
    };
    let ret = env.new_string(strategy).expect("Couldn't create java string!");
    ret
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString, pad_to_multiple_of: jint) {
    let strategy: String = env.get_string(&strategy).expect("Couldn't get java string!").into();
    let len = max_length as usize;
    let ref_strategy = match strategy.as_ref() {
        "LONGEST" => Ok(PaddingStrategy::BatchLongest),
        "MAX_LENGTH" => Ok(PaddingStrategy::Fixed(len)),
        _ => Err("strategy must be one of [longest, max_length]"),
    };
    let ref_pad_to_multiple_of = match pad_to_multiple_of as usize {
        0 => None,
        val => Some(val),
    };
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    if let Some(padding_params) = tokenizer.get_padding_mut() {
        padding_params.strategy = ref_strategy.unwrap();
        padding_params.pad_to_multiple_of = ref_pad_to_multiple_of;
    } else {
        let padding_params = PaddingParams {
            strategy: ref_strategy.unwrap(),
            pad_to_multiple_of: ref_pad_to_multiple_of,
            ..Default::default()
        };
        tokenizer.with_padding(Some(padding_params));
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerPaddingDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    tokenizer.with_padding(None);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationStrategy<'local>(env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong) -> JString<'local> {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let truncation = tokenizer.get_truncation();
    let strategy = match truncation {
        Some(val) => val.strategy.as_ref(),
        None => "DO_NOT_TRUNCATE",
    };
    let ret = env.new_string(strategy.to_string()).expect("Couldn't create java string!");
    return ret;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationUpdate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, max_length: jint, strategy: JString, truncation_stride: jint) {
    let strategy: String = env.get_string(&strategy).expect("Couldn't get java string!").into();
    let ref_strategy = match strategy.as_ref() {
        "LONGEST_FIRST" => Ok(TruncationStrategy::LongestFirst),
        "ONLY_FIRST" => Ok(TruncationStrategy::OnlyFirst),
        "ONLY_SECOND" => Ok(TruncationStrategy::OnlySecond),
        _ => Err("strategy must be one of [longest_first, only_first, only_second]"),
    };
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    if let Some(truncation_params) = tokenizer.get_truncation_mut() {
        truncation_params.strategy = ref_strategy.unwrap();
        truncation_params.stride = truncation_stride as usize;
        truncation_params.max_length = max_length as usize;
    } else {
        let truncation_params = TruncationParams {
            strategy: ref_strategy.unwrap(),
            stride: truncation_stride as usize,
            max_length: max_length as usize,
            ..Default::default()
        };
        let _ = tokenizer.with_truncation(Some(truncation_params));
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenizerTruncationDisable(_: JNIEnv, _: JObject, tokenizer_id: jlong) {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let _ = tokenizer.with_truncation(None);
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_encode<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, input: JString, add_special_tokens: jboolean) -> jobject {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let sequence: String = env.get_string(&input).expect("Couldn't get java string!").into();
    let input_sequence = tokenizers::InputSequence::from(sequence);
    let encoded_input = EncodeInput::Single(input_sequence);
    let encoding = tokenizer.encode_char_offsets(encoded_input, add_special_tokens == JNI_TRUE).unwrap();
    let hash_map_class = env.find_class("java/util/HashMap").expect("Couldn't find HashMap class");
    let hash_map = env.new_object(hash_map_class, "()V", &[]).expect("Couldn't init HashMap instance");
    {
        let tokens = encoding.get_tokens();
        let len = tokens.len() as jsize;
        let array = env.new_object_array(len, "java/lang/String", JObject::null()).unwrap();
        for (i, token) in tokens.iter().enumerate() {
            let item: JString = env.new_string(&token).unwrap();
            env.set_object_array_element(&array, i as jsize, item).unwrap();
        }
        let name = env.new_string("tokens").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    {
        let ids = encoding.get_ids();
        let len = ids.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        long_ids.reserve(len as usize);
        for i in ids {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len).unwrap();
        env.set_long_array_region(&array, 0, &long_ids).unwrap();
        let name = env.new_string("token_ids").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    {
        let type_ids = encoding.get_type_ids();
        let len = type_ids.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in type_ids {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len).unwrap();
        env.set_long_array_region(&array, 0, &long_ids).unwrap();
        let name = env.new_string("token_type_ids").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    {
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
        let array = env.new_long_array(len).unwrap();
        env.set_long_array_region(&array, 0, &long_ids).unwrap();
        let name = env.new_string("token_word_ids").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    {
        let attention_masks = encoding.get_attention_mask();
        let len = attention_masks.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in attention_masks {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len).unwrap();
        env.set_long_array_region(&array, 0, &long_ids).unwrap();
        let name = env.new_string("attention_masks").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    {
        let special_token_masks = encoding.get_special_tokens_mask();
        let len = special_token_masks.len() as jsize;
        let mut long_ids: Vec<jlong> = Vec::new();
        for i in special_token_masks {
            long_ids.push(*i as jlong)
        }
        let array = env.new_long_array(len).unwrap();
        env.set_long_array_region(&array, 0, &long_ids).unwrap();
        let name = env.new_string("special_token_masks").unwrap();
        env.call_method(&hash_map, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", &[JValue::Object(&name), JValue::from(&array)]).unwrap();
    }
    return hash_map.into_raw();
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_encodingCreate<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, input: JString, add_special_tokens: jboolean) -> jlong {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let sequence: String = env.get_string(&input).expect("Couldn't get java string!").into();
    let input_sequence = tokenizers::InputSequence::from(sequence);
    let encoded_input = EncodeInput::Single(input_sequence);
    let encoding = tokenizer.encode_char_offsets(encoded_input, add_special_tokens == JNI_TRUE);
    match encoding {
        Ok(out) => box_and_return_id(out),
        Err(err) => {
            env.throw(err.to_string()).unwrap();
            return 0;
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_encodingDelete(_: JNIEnv, _: JObject, encoding_id: jlong) {
    box_delete_by_id::<Encoding>(encoding_id);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokensGet<'local>(mut env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JObjectArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let tokens = encoding.get_tokens();
    let len = tokens.len() as jsize;
    let array = env.new_object_array(len, "java/lang/String", JObject::null()).unwrap();
    for (i, token) in tokens.iter().enumerate() {
        let item: JString = env.new_string(&token).unwrap();
        env.set_object_array_element(&array, i as jsize, item).unwrap();
    }
    return array;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenIdsGet<'local>(env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let ids = encoding.get_ids();
    let len = ids.len() as jsize;
    let mut long_ids: Vec<jlong> = Vec::new();
    long_ids.reserve(len as usize);
    for i in ids {
        long_ids.push(*i as jlong)
    }
    let array = env.new_long_array(len).unwrap();
    env.set_long_array_region(&array, 0, &long_ids).unwrap();
    return array;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenTypeIdsGet<'local>(env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let type_ids = encoding.get_type_ids();
    let len = type_ids.len() as jsize;
    let mut long_ids: Vec<jlong> = Vec::new();
    for i in type_ids {
        long_ids.push(*i as jlong)
    }
    let array = env.new_long_array(len).unwrap();
    env.set_long_array_region(&array, 0, &long_ids).unwrap();
    return array;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_tokenWordIdsGet<'local>(env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
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
    let array = env.new_long_array(len).unwrap();
    env.set_long_array_region(&array, 0, &long_ids).unwrap();
    return array;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_attentionMaskGet<'local>(env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let attention_masks = encoding.get_attention_mask();
    let len = attention_masks.len() as jsize;
    let mut long_ids: Vec<jlong> = Vec::new();
    for i in attention_masks {
        long_ids.push(*i as jlong)
    }
    let array = env.new_long_array(len).unwrap();
    env.set_long_array_region(&array, 0, &long_ids).unwrap();
    return array;
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_specialTokenMaskGet<'local>(env: JNIEnv<'local>, _: JObject, encoding_id: jlong) -> JLongArray<'local> {
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let special_token_masks = encoding.get_special_tokens_mask();
    let len = special_token_masks.len() as jsize;
    let mut long_ids: Vec<jlong> = Vec::new();
    for i in special_token_masks {
        long_ids.push(*i as jlong)
    }
    let array = env.new_long_array(len).unwrap();
    env.set_long_array_region(&array, 0, &long_ids).unwrap();
    return array;
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_decode<'local>(mut env: JNIEnv<'local>, _: JObject, tokenizer_id: jlong, token_ids: JLongArray<'local>, skip_special_tokens: jboolean) -> JString<'local> {
    let tokenizer = box_select_by_id::<Tokenizer>(tokenizer_id);
    let long_ids = unsafe { env.get_array_elements(&token_ids, ReleaseMode::NoCopyBack) }.unwrap();
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
    let result = env.new_string(&decoding).expect("Couldn't create java string!");
    return result;
}

// ----------------------------------------------------------------------------

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_modelCreate<'local>(mut env: JNIEnv<'local>, _: JObject, gpu_id: jint, path: JString<'local>) -> jlong {
    let directory: String = env.get_string(&path).expect("Couldn't create get path!").into();
    let device = if gpu_id >= 0 { Device::new_cuda(gpu_id as usize).unwrap() } else { Device::Cpu };
    let file_config = format!("{}/config.json", directory);
    let file_weights = format!("{}/pytorch_model.bin", directory);
    let data_config = std::fs::read_to_string(file_config).unwrap();
    let config: Config = serde_json::from_str(&data_config).unwrap();
    let vb = VarBuilder::from_pth(file_weights, DTYPE, &device).unwrap();
    let model = BertModel::load(vb, &config);
    return model.map(box_and_return_id).map_err(|e| env.throw(e.to_string()).unwrap()).unwrap_or(0);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_modelDelete(_: JNIEnv, _: JObject, model_id: jlong) {
    box_delete_by_id::<BertModel>(model_id);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddingsCreate<'local>(mut env: JNIEnv<'local>, _: JObject, model_id: jlong, encoding_id: jlong) -> jlong {
    let model = box_select_by_id::<BertModel>(model_id);
    let encoding = box_select_by_id::<Encoding>(encoding_id);
    let tokens = encoding.get_ids().to_vec();
    let token_ids = Tensor::new(&tokens[..], &model.device).unwrap().unsqueeze(0).unwrap();
    let token_type_ids = token_ids.zeros_like().unwrap();
    let embeddings = model.forward(&token_ids, &token_type_ids);
    return embeddings.map(box_and_return_id).map_err(|e| env.throw(e.to_string())).unwrap_or(0);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddingsDelete(_: JNIEnv, _: JObject, embeddings_id: jlong) {
    box_delete_by_id::<Tensor>(embeddings_id);
}

#[no_mangle]
pub extern "system" fn Java_page_foliage_ai_candle_CandleLibrary_embeddings<'local>(mut env: JNIEnv<'local>, _: JObject, embeddings_id: jlong) -> JObjectArray<'local> {
    let embeddings = box_select_by_id::<Tensor>(embeddings_id);
    let (_n_sentence, n_tokens, _hidden_size) = embeddings.dims3().unwrap();
    let embeddings = (embeddings.sum(1).unwrap() / (n_tokens as f64)).unwrap();
    let vectors: Vec<Vec<f32>> = embeddings.to_vec2::<f32>().unwrap();
    let length = vectors.len() as jsize;
    let float_array_class = env.find_class("[F").expect("Failed to find float array class!");
    let jarray: JObjectArray = env.new_object_array(length, float_array_class, JObject::null()).unwrap();
    for (i, vec) in vectors.iter().enumerate() {
        let jvec: JFloatArray = env.new_float_array(vec.len() as jsize).unwrap();
        env.set_float_array_region(&jvec, 0, &vec).unwrap();
        env.set_object_array_element(&jarray, i as jsize, jvec).unwrap();
    }
    return jarray;
}

// ----------------------------------------------------------------------------
