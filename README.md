# MessageService

Usage: Please enter the number beside the option in the console to select an option

# Main menu. 

*************** Message Queue Service ***************

Select an option:
1. Registrations
2. Publish Message
3. Utils
4. Exit the program

option1: To register publishers, queues, add or remove subscribers
option2: To publish message. Take publisher id <String> and file location as input. The message is enqueued. Dequeue is scheduled to run every 30 seconds for main queues and 60 seconds for retry queue. Can be changed in config file.
option3: Some utility methods to check all publisher ids, subscribers in a queue, message lag etc.


# Registration Menu

*************** Registration Menu ***************

Select an option:
1. Register new queue
2. Register new publisher
3. Add new subscriber to queue
4. Remove subscriber from queue
5. Return to main menu

Please register at least one publisher, message queue and one subscriber before publishing messages.
When asked for id, you can enter unique id for publisher, message queue or subscriber

# Utility Menu

*************** UTILS ***************

Select an option:
1. View all publisher ids registered
2. View all message queues registered
3. View all publisher to message queues mapping
4. View all subscribers of a queue
5. View message queue lag
