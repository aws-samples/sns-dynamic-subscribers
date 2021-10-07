package com.amazonaws.samples;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.util.SQSMessageConsumer;
import com.amazonaws.services.sqs.util.SQSMessageConsumerBuilder;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Bootstrap {

    private static boolean running = true;
    private String queue_url = "";
    private final String SQS_CONTAINER_MAPPING_TABLE = "SQSContainerMappping"; //need to fix this with env property

    public Bootstrap()  {

        try {
            //insert the queue name and container id in a DynamoDB table
            String taskId = getTaskId();
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            CreateQueueRequest create_request = new CreateQueueRequest(taskId);
            sqs.createQueue(create_request);

            queue_url = sqs.getQueueUrl(taskId).getQueueUrl();
            AmazonDynamoDB ddbClient = AmazonDynamoDBClientBuilder.defaultClient();

            Map<String, AttributeValue> item_values = new HashMap<String,AttributeValue>();

            item_values.put("id", new AttributeValue(taskId));
            item_values.put("QueueName", new AttributeValue(taskId));
            //item_values.put("id", new AttributeValue(UUID.randomUUID().toString()));

            //ddbClient.putItem(SQS_CONTAINER_MAPPING_TABLE,item_values);




        }catch(IOException exp){
            exp.printStackTrace();
        }catch (AmazonSQSException e) {
            e.printStackTrace();
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }


    }

    private String getTaskId()throws IOException{
        Map<String, String> map = System.getenv();
        String metaDataURL = map.get("ECS_CONTAINER_METADATA_URI_V4");
        System.out.println("The meta data url : " + metaDataURL);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String taskId = "";
        try {

            HttpGet request = new HttpGet(metaDataURL);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    System.out.println(result);
                    String taskARN = JsonPath.read(result, "$['Labels']['com.amazonaws.ecs.task-arn']").toString();
                    String[] arnTokens = taskARN.split("/");
                    taskId = arnTokens[arnTokens.length-1];
                    System.out.println("The task arn : "+taskId);

                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return taskId;
    }

    public void processMessages() {
        //String queueUrl = "https://sqs.us-west-2.amazonaws.com/681921237057/sns-subsciption-test";
        System.out.println("Starting up consumer using queue: " + queue_url);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        AmazonSQSResponder responder = AmazonSQSResponderClientBuilder.standard()
                .withAmazonSQS(sqs)
                .build();

        SQSMessageConsumer consumer = SQSMessageConsumerBuilder.standard()
                .withAmazonSQS(responder.getAmazonSQS())
                .withQueueUrl(queue_url)
                .withConsumer(message -> {
                    System.out.println("The message is " + message.getBody());
                    sqs.deleteMessage(queue_url,message.getReceiptHandle());

                }).build();
        consumer.start();
    }

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.processMessages();
        while (running) {
        };

    }
}
