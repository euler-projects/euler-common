package org.eulerframework.common.util.aliyun;

import org.eulerframework.common.http.JdkHttpClientTemplate;

import java.io.IOException;
import java.net.URISyntaxException;

public class AliyunOpenApiRequestTest {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        String resp = AliyunOpenApiRequest
                .POST()
                .endpoint("https://ecs.cn-shenzhen.aliyuncs.com")
                .action("DescribeInstances")
                .version("2014-05-26")
                .bodyParam("RegionId", "cn-shenzhen")
                .accessKeyId(System.getenv("ACCESS_KEY_ID"))
                .accessKeySecret(System.getenv("ACCESS_KEY_SECRET"))
                .execute(new JdkHttpClientTemplate());

        System.out.println(resp);


//                AliyunOpenApiRequest openApiRequest = new AliyunOpenApiRequest(
//                new AliyunCredentials(System.getenv("ACCESS_KEY_ID"), System.getenv("ACCESS_KEY_SECRET")),
//                AliyunHttpMethod.POST,
//                "https://captcha.cn-shanghai.aliyuncs.com",
//                null,
//                "VerifyIntelligentCaptcha",
//                "2023-03-05",
//                null,
//                null,
//                Collections.singletonMap("CaptchaVerifyParam", "cn-shenzhen")
//        );
//        System.out.println(execute(openApiRequest));


        //        System.out.println('\u007f');
//        URI uri = URI.create("https://a@ecs.cn-shenzhen.aliyuncs.com?q=v&c=%E4%B8%AD%E6%96%87");
//        uri = URIBuilder.of("https://a@ecs.cn-shenzhen.aliyuncs.com?x=v&y=%E4%B8%AD%E6%96%87")
////                .schema("http")
////                //.authority("abc")
////                //.userInfo("tom")
////                .host("www.example.com")
////                //.port(8080)
//                .path("")
//                .query("q", "v")
//                .query("c", "中文")
//                .query("d", "%20")
//                .build();
    }
}