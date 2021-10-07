package com.amazonaws.samples;

import com.jayway.jsonpath.JsonPath;

import java.util.List;

public class JSONUtil {

    public static void main(String []args){
        String jsonVal = "{\n" +
                "    \"DockerId\": \"b13f28bfa21c5287bb3c1fc2240b5b30359da4fcdfe5908ab96a1fc28c1455fd\",\n" +
                "    \"Name\": \"sns-dynamic-subscriber-task\",\n" +
                "    \"DockerName\": \"ecs-sns-dynamic-subscriber-task-5-sns-dynamic-subscriber-task-ac8efed6b3daebf6c401\",\n" +
                "    \"Image\": \"681921237057.dkr.ecr.us-west-2.amazonaws.com/sns-demo/sns-dynamic-subscriber:latest\",\n" +
                "    \"ImageID\": \"sha256:ba496c6cbef5a49797ee76b3ca6584815dc9d847a975162e379701542ac667ef\",\n" +
                "    \"Labels\": {\n" +
                "        \"com.amazonaws.ecs.cluster\": \"arn:aws:ecs:us-west-2:681921237057:cluster/sns-demo-cluster\",\n" +
                "        \"com.amazonaws.ecs.container-name\": \"sns-dynamic-subscriber-task\",\n" +
                "        \"com.amazonaws.ecs.task-arn\": \"arn:aws:ecs:us-west-2:681921237057:task/sns-demo-cluster/8d2c6267104e4f5888c3cf3abedd9f97\",\n" +
                "        \"com.amazonaws.ecs.task-definition-family\": \"sns-dynamic-subscriber-task\",\n" +
                "        \"com.amazonaws.ecs.task-definition-version\": \"5\"\n" +
                "    },\n" +
                "    \"DesiredStatus\": \"RUNNING\",\n" +
                "    \"KnownStatus\": \"RUNNING\",\n" +
                "    \"Limits\": {\n" +
                "        \"CPU\": 512,\n" +
                "        \"Memory\": 1024\n" +
                "    },\n" +
                "    \"CreatedAt\": \"2021-02-18T07:12:14.248169859Z\",\n" +
                "    \"StartedAt\": \"2021-02-18T07:12:19.891038547Z\",\n" +
                "    \"Type\": \"NORMAL\",\n" +
                "    \"Volumes\": [\n" +
                "        {\n" +
                "            \"DockerName\": \"9ee502787a779313f924a73c13c1168654493392ec22ddda6b1cb1329eb8aa26\",\n" +
                "            \"Source\": \"/var/lib/docker/volumes/9ee502787a779313f924a73c13c1168654493392ec22ddda6b1cb1329eb8aa26/_data\",\n" +
                "            \"Destination\": \"/tmp\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Networks\": [\n" +
                "        {\n" +
                "            \"NetworkMode\": \"awsvpc\",\n" +
                "            \"IPv4Addresses\": [\n" +
                "                \"10.11.0.129\"\n" +
                "            ],\n" +
                "            \"AttachmentIndex\": 0,\n" +
                "            \"IPv4SubnetCIDRBlock\": \"10.11.0.0/24\",\n" +
                "            \"MACAddress\": \"02:b3:87:5b:46:9b\",\n" +
                "            \"DomainNameServers\": [\n" +
                "                \"10.11.0.2\"\n" +
                "            ],\n" +
                "            \"DomainNameSearchList\": [\n" +
                "                \"us-west-2.compute.internal\"\n" +
                "            ],\n" +
                "            \"PrivateDNSName\": \"ip-10-11-0-129.us-west-2.compute.internal\",\n" +
                "            \"SubnetGatewayIpv4Address\": \"10.11.0.1/24\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String json = "{ \"name\": \"Baeldung\", \"java\": true }";

        //System.out.println(JsonPath.read(jsonVal, "$['Labels']['com.amazonaws.ecs.task-arn']").toString());
        //List<String> list = JsonPath.read(jsonVal, "$['Labels']['com.amazonaws.ecs.task-arn']");
        //System.out.println(list.get(0));

        String taskARN = JsonPath.read(jsonVal, "$['Labels']['com.amazonaws.ecs.task-arn']").toString();
        String[] arnTokens = taskARN.split("/");
        String taskId = arnTokens[arnTokens.length-1];
        System.out.println("The task arn : "+taskId);

    }
}
