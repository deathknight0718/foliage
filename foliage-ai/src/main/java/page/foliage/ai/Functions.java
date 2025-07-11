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
package page.foliage.ai;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Functions {

    public static float[][] mean(float[][][] space, int dim) {
        int dims0 = space.length;
        int dims1 = space[0].length;
        int dims2 = space[0][0].length;
        switch (dim) {
        case 0: {
            float[][] mean = new float[dims1][dims2];
            for (int dv0 = 0; dv0 < dims0; dv0++) {
                for (int dv1 = 0; dv1 < dims1; dv1++) {
                    for (int dv2 = 0; dv2 < dims2; dv2++) {
                        mean[dv1][dv2] += space[dv0][dv1][dv2];
                    }
                }
            }
            for (int dv1 = 0; dv1 < dims1; dv1++) {
                for (int dv2 = 0; dv2 < dims2; dv2++) {
                    mean[dv1][dv2] /= dims0;
                }
            }
            return mean;
        }
        case 1: {
            float[][] mean = new float[dims0][dims2];
            for (int dv0 = 0; dv0 < dims0; dv0++) {
                for (int dv1 = 0; dv1 < dims1; dv1++) {
                    for (int dv2 = 0; dv2 < dims2; dv2++) {
                        mean[dv0][dv2] += space[dv0][dv1][dv2];
                    }
                }
            }
            for (int dv0 = 0; dv0 < dims0; dv0++) {
                for (int dv2 = 0; dv2 < dims2; dv2++) {
                    mean[dv0][dv2] /= dims1;
                }
            }
            return mean;
        }
        case 2: {
            float[][] mean = new float[dims0][dims1];
            for (int dv0 = 0; dv0 < dims0; dv0++) {
                for (int dv1 = 0; dv1 < dims1; dv1++) {
                    for (int dv2 = 0; dv2 < dims2; dv2++) {
                        mean[dv0][dv1] += space[dv0][dv1][dv2];
                    }
                }
            }
            for (int dv0 = 0; dv0 < dims0; dv0++) {
                for (int dv1 = 0; dv1 < dims1; dv1++) {
                    mean[dv0][dv1] /= dims2;
                }
            }
            return mean;
        }
        default:
            throw new IllegalArgumentException();
        }
    }

}
