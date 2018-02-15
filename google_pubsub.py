from google.cloud import pubsub
import os

project_id = "engel429pubsub"

#### Based on documentation examples at
#### https://cloud.google.com/pubsub/docs/quickstart-client-libraries#pubsub-client-libraries-python

# it seems that in Python clients are either PublisherClients or SubscribeClients
def join(type_of_client):
    if type_of_client == 'publisher':
        return pubsub.PublisherClient()

def publish(publisher, topic, message):
    publisher.publish(topic, message)

    print("Publication" + message + "successfully created.")

def create_subscribe(subscriber, subscription_name, topic):
    subscription_path = 'projects/' + project_id + "/subscriptions/" + subscription_name
    subscriber.create_subscription(subscription_path, topic)

    print("Subscription " + subscription_name + "successfully created.")

def retrieve_subscription(subscriber, subscription_name):
    subscription = subscriber.subscribe('projects/' + project_id + "/subscriptions/" + subscription_name)
    future_result = subscription.open()

    return future_result.result()

def create_topic(publisher, name):
    topic_path = 'projects/' + project_id + "/topics/" + name
    topic = publisher.create_topic(topic_path)

    print("Successfully created topic:", name)
