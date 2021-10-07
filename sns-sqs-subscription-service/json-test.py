import json
import boto3

sqs_client = boto3.client('sqs')
sampleJson = {
    "name": "John",
    "age": 30,
    "city": "New York",
    "detail": {
        "taskArn": "arn:aws:ecs:us-west-2:681921237057:task/sns-demo-cluster/e556ffd914994b4c980ead90f2165987"
    }
}
# convert into JSON string:
y = json.dumps(sampleJson)
# convert json string to Json Dict
jsonDict = json.loads(y)

taskArn = jsonDict['detail']['taskArn']

print(taskArn)

taskArnTokens = taskArn.split('/')

print(taskArnTokens[len(taskArnTokens)-1])

queue_name = taskArnTokens[len(taskArnTokens)-1]

#queue = sqs_client.get_queue_url(QueueName=taskArnTokens[len(taskArnTokens)-1])

#queue_url = sqs_client.get_queue_url(QueueName=queue_name)['QueueUrl']

#queue = sqs_client.get_queue_attributes(
#    QueueUrl=queue_url, AttributeNames=['All'])

sns = boto3.client('sns')

response = sns.create_topic(
    Name='sns-demo-topic'
)

#queue_arn = queue['Attributes']['QueueArn']
topic_arn = response['TopicArn']

#print(queue['Attributes']['QueueArn'])
print(response['TopicArn'])

#response = sns.subscribe(TopicArn=topic_arn, Protocol="sqs", Endpoint=queue_arn)

#subscription_arn = response["SubscriptionArn"]
# print(subscription_arn)

#subs_resp = sns.list_subscriptions_by_topic(TopicArn=response['TopicArn'])
# print(subs_resp)

dynamodb = boto3.client('dynamodb')

print('Helloo..')


#table = dynamodb.Table('SQSContainerMappping')

#ddbresponse = dynamodb.get_item(TableName='SQSContainerMappping', Key={
#                                'id': {'S': '35bfbb20a1f94c4f8c705d4e805dd8d9'}})
#print(ddbresponse['Item']['SubscriptionArn']['S'])
#subscription_arn = ddbresponse['Item']['SubscriptionArn']['S']

#print('Subscription ARN retrieved  ----{}', subscription_arn)

#snsresp = sns.unsubscribe(SubscriptionArn=subscription_arn)
#print('Unsubscribe response {}', snsresp)

# remove the queue

#queuedelresp = sqs_client.delete_queue(QueueUrl=queue_url)

#print('Unsubscribe response {}', queuedelresp)
ddbresponse = dynamodb.update_item(
        TableName='SQSContainerMappping',
        Key={
            'id': {
                'S' : 'dsdsdsds'
            }
        },
        AttributeUpdates={
            'SubscriptionArn':{
                'Value': {
                    'S': 'ddddddddddfdfdfd'
                }
            }
        },
        ReturnValues="UPDATED_NEW"
    )
print(ddbresponse)