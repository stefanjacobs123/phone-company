# Phone Company

Each day at The Phone Company a batch job puts all the customer calls for the previous day into a single log file of:

`'customer id','phone number called','call duration'`

For a customer the cost of a call up to and including 3 minutes in duration is charged at 0.05p/sec, any call over 3 
minutes in duration the additional time is charged at 0.03p/sec. However, there is a promotion on and the calls made 
to the phone number with the greatest total cost is removed from the customer's bill.

## Task

Write a program that when run will parse the `calls.log` file and print out the total cost of calls for the day for 
each customer. You can use any libraries you wish to.

## Implementation

From my side, the goal of this exercise was more focused on finding different solutions to the problem. As appose to 
structuring the project perfectly and writing a production ready service with error handling and performance tests. 
The mechanism for picking up new files were also not considered.

The verdict is out on which solution would perform better. For production, we'll have to consider how large these files 
typically are and how many calls are made each day and to how many different numbers. Both of the solutions I provide, 
I'm sure, will be able to handle the load of calls for any large network provider. A more in depth analysis will 
obviously need to be made to make such a statement. 

I've looked at two different solutions:
1. Utilising only Scala collections
2. Utilising akka-streams
    1. Without promotion implementation
    2. With promotion implementation

I've decided to not include the day's date in the output. The program will print a human readable message - something 
that's not very useful for a downstream service. This can easily be altered by changing the override toString message of 
the case class containing the daily bill (`com.phone.model.Bill`) for each customer.

Please let me know if anything could be done better. Would love to learn!

### Notes

1. Empty lines are filtered out of the file.
2. Testing should be improved.
3. Added one Call to calls.log - customer with only one call.

## Run

`sbt run`

## Test

`sbt test`