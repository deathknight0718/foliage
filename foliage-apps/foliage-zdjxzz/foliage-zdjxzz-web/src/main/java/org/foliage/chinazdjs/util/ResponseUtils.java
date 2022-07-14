/*******************************************************************************
 * Copyright 2020 deathknight0718@qq.com..
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.chinazdjs.util;

import java.io.File;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.foliage.guava.common.net.MediaType;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class ResponseUtils {

    public static Response handleSuccess() {
        return Response.ok() //
            .type(MediaType.PLAIN_TEXT_UTF_8.toString()) // 1111395@greatwall.com.cn: 解决火狐浏览器报错： XML Parsing Error
            .header("Access-Control-Allow-Origin", "*") //
            .build();
    }

    public static Response handleSuccess(String json) {
        return Response.ok(json) //
            .type(MediaType.JSON_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .build();
    }

    public static Response handleFile(File file) {
        return Response.ok(file) //
            .type(MediaType.PDF.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .header("Content-Disposition", String.format("attachment; filename=%s", file.getName())) //
            .build();
    }

    public static Response handleStreamOfPDF(StreamingOutput output, String name) {
        return Response.ok(output) //
            .type(MediaType.PDF.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .header("Content-Disposition", String.format("attachment; filename=%s", name)) //
            .build();
    }

    public static Response handleStreamOfCSV(StreamingOutput output, String name) {
        return Response.ok(output) //
            .type(MediaType.CSV_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .header("Content-Disposition", String.format("attachment; filename=%s", name)) //
            .build();
    }

    public static Response handleStreamOfTXT(StreamingOutput output, String name) {
        return Response.ok(output) //
            .type(MediaType.PLAIN_TEXT_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .header("Content-Disposition", String.format("attachment; filename=%s", name)) //
            .build();
    }

    public static Response handleStreamOfZIP(StreamingOutput output, String name) {
        return Response.ok((Object) output) //
            .type(MediaType.OCTET_STREAM.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .header("Content-Disposition", String.format("attachment; filename=%s", name)) //
            .build();
    }

    public static Response handleHtml(String html) {
        return Response.ok(html) //
            .type(MediaType.HTML_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .build();
    }

    public static Response handleException(String message) {
        return Response.serverError() //
            .type(MediaType.PLAIN_TEXT_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .entity(message) //
            .build();
    }

    public static Response handleException(Status status, String message) {
        return Response.status(status) //
            .type(MediaType.PLAIN_TEXT_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .entity(message) //
            .build();
    }

    public static Response handleException(Throwable e) {
        return Response.serverError() //
            .type(MediaType.PLAIN_TEXT_UTF_8.toString()) //
            .header("Access-Control-Allow-Origin", "*") //
            .entity(e.getMessage()) //
            .build();
    }

}
