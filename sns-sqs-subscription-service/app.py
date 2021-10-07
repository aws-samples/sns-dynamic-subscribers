import boto3

import json
import os
import random
import time

TOPIC_NAME = os.environ['TOPIC_NAME']
SERVICE_NAME = os.environ['SERVICE_NAME']
SQS_CONTAINER_MAPPING_TABLE=os.environ['SQS_CONTAINER_MAPPING_TABLE']

sns = boto3.client('sns')
sqs_client = boto3.client('sqs')
dynamodb = boto3.client('dynamodb')

def lambda_handler(event, context):
    print('{} received event: {}'.format(SERVICE_NAME, json.dumps(event)))
    print('Topic Name : {}'.format(TOPIC_NAME))
   
    # get the task id from the event
    taskArn = event['detail']['taskArn']
    taskArnTokens = taskArn.split('/')

    taskId = taskArnTokens[len(taskArnTokens)-1]

    print('{} received for subscription'.format(taskId))

    
    queue_name = taskArnTokens[len(taskArnTokens)-1]

    #queue = sqs_client.get_queue_url(QueueName=taskArnTokens[len(taskArnTokens)-1])

    create_queue_resp = sqs_client.create_queue(QueueName=queue_name)

    print('Response from create queue {}'.format(create_queue_resp))

    queue_url = sqs_client.get_queue_url(QueueName=queue_name)['QueueUrl']

        
   
    queue_attr = sqs_client.get_queue_attributes(QueueUrl=queue_url,AttributeNames=['All'])

    sns = boto3.client('sns')
    

    response = sns.create_topic(
        Name=TOPIC_NAME
    )

    queue_arn = queue_attr['Attributes']['QueueArn']
    topic_arn = response['TopicArn']

    policy_json = allow_sns_to_write_to_sqs(topic_arn, queue_arn)
    set_attr_response = sqs_client.set_queue_attributes(QueueUrl = queue_url,Attributes = {'Policy' : policy_json})
    
    print('Policy is {}'.format(policy_json))

    print('Queue ARN is {}'.format(queue_attr['Attributes']['QueueArn']))
    print('Topic ARN is {}'.format(response['TopicArn']))
    
    
    response = sns.subscribe(TopicArn=topic_arn, Protocol="sqs", Endpoint=queue_arn)

    subscription_arn = response["SubscriptionArn"]
    print('Created subscription {}'.format(subscription_arn))

    #table = dynamodb.Table(SQS_CONTAINER_MAPPING_TABLE)

    print('Task id used for key {}'.format(taskId.strip()))

    ddbresponse = dynamodb.update_item(
        TableName=SQS_CONTAINER_MAPPING_TABLE,
        Key={
            'id': {
                'S' : taskId.strip()
            }
        },
        AttributeUpdates={
            'SubscriptionArn':{
                'Value': {
                    'S': subscription_arn
                }
            }
        },
        ReturnValues="UPDATED_NEW"
    )

    print('DDB update response {}'.format(ddbresponse))
    return


def allow_sns_to_write_to_sqs(topicarn, queuearn):
    policy_document = """{{
        "Version":"2012-10-17",
        "Statement":[
            {{
            "Sid":"SNSToSQSPolicy",
            "Effect":"Allow",
            "Principal" : {{"AWS" : "*"}},
            "Action":"SQS:SendMessage",
            "Resource": "{}",
            "Condition":{{
                "ArnEquals":{{
                "aws:SourceArn": "{}"
                }}
            }}
            }}
        ]
        }}""".format(queuearn, topicarn)

    return policy_document