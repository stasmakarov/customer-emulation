NOTE: This project is in development process for now and can contain some bugs/errors, and it is not production ready in any way. Use it on your own risk.

== Review

This is the first application - customer emulation. Use Control panel view to start requests creation. Check the steps below to start entire project correctly.  

=== How to start project

Logically, this part is a generator, but, in fact, should be started after order-processing app due to RabbitMQ stuff init process.

* Start `RabbitMQ` (if not started during order-processing app startup) 

[source, bash]
----
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
----

* Start order-processing application - https://github.com/stasmakarov/order-processing, pay attention to README file in the second project. If order-processing application works already - just continue.
* Start this application
* Go to http://localhost:8081/control-panel-view and press the Start button 