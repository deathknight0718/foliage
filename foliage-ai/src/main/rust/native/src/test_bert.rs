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

#[cfg(test)]
mod tests {
    use anyhow::Result;
    use candle::Tensor;
    use candle::{Device, Error};
    use candle_nn::VarBuilder;
    use candle_transformers::models::bert::{BertModel, Config, DTYPE};
    use tokenizers::Tokenizer;

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
    fn it_works() {
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
