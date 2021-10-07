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

    #create_queue_resp = sqs_client.create_queue(QueueName=queue_name)

    queue_url = sqs_client.get_queue_url(QueueName=queue_name)['QueueUrl']
   
    queue_attr = sqs_client.get_queue_attributes(QueueUrl=queue_url,AttributeNames=['All'])
    queue_arn = queue_attr['Attributes']['QueueArn']

    # get subscription ARN

    ddbresponse = dynamodb.get_item(TableName=SQS_CONTAINER_MAPPING_TABLE,Key={'id': { 'S' : taskId}})
    subscription_arn = ddbresponse['Item']['SubscriptionArn']['S']
    print('Subscription ARN retrieved {}'.format(subscription_arn))



    # remove the subscription
 

    snsresp = sns.unsubscribe(SubscriptionArn=subscription_arn)
    print('Unsubscribe response {}'.format(snsresp))

    # remove the queue

    queuedelresp = sqs_client.delete_queue(QueueUrl=queue_url)

    print('Queue delete response {}'.format(queuedelresp))


    return

