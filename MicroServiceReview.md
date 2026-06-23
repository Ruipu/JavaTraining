# Microservice Review June 22

[David’s Note](https://app.notion.com/p/David-s-Note-3881db30d2df804b9082d0d23b60959f?pvs=21)

[Youtube Video——**Spring Cloud Eureka | Java Techie Summary**](https://app.notion.com/p/Youtube-Video-Spring-Cloud-Eureka-Java-Techie-Summary-3881db30d2df80fea469dbe22e66154b?pvs=21)

## API Gateway.

- User send request from frontend. First hit API Gateway
- API Gateway: Routing traffic, ID checking, Rate limiting(100 request, same request,tracking ID address, you can set up rate limiting.)
- The only thing expose to public.

## Different business modules.

Order service, Account service, Shipment service.

- Cluster: each service module has multiple services, they clustered.
- 1 server-3servers: horizontal scaling.
- How do we know which service is alive and which is down? **Service discovery: Eureka**

## Service Discovery

- Always annotation and configuration in yaml file.
- Register instance 1,2,3. @EnbaleEureka.
- tell Eureka: I am still alive frequently.
- If Eureka loses the hit from one service for a time (30s), it knows the service is down.

## Load Balancer

**Which one of there instance will handle the request?**

- Across multiple cluster, for example, account service cluster Africa, Europe, North America
- which instance is to deal with this request? load balancer

## Configuration Server

- Example DB credential.—>put into the configuration server.
- If not, manually configre in the yml file.
- Concentrated configuration server, namespace(employee service—>QA,DEV—→YML file)
- Safety in configuration server. Put into the **vault** (db url = “url” (referring variable in the vault))

## Database consistency

### **1. A service cluster can have two database designs**

David first said that suppose we have an `Employee Service` cluster, and inside the cluster there are multiple backend instances, such as instance 1, instance 2, and instance 3. There can be two database designs.

### **Each backend instance connects to its own independent database**.

- For example, instance 1 connects to DB1, instance 2 connects to DB2, and instance 3 connects to DB3. The benefit of this design is that high availability and fault tolerance are stronger. If instance 1 or DB1 goes down, instance 2 and instance 3 are still alive, so the system can still handle part of the requests. In other words, a single point of failure will not directly make the whole service unavailable.
- But the cost of this design is that **data synchronization becomes more complicated.** Because each database has its own state, if the user’s first request goes to instance 1 and the second request is assigned by the load balancer to instance 2, the states in the two databases may become inconsistent.
- For example, the user’s first request is to confirm an order. The order status is written into DB1 and shown as confirmed. Later, the user’s second request is to cancel the order, but this request goes to instance 2, so the canceled status is written into DB2. As a result, the order in DB1 is still confirmed, while the order in DB2 is already canceled. The whole system now has an inconsistent state. At this point, the system either has to sacrifice performance to synchronize DB1, DB2, and DB3, or it has to accept data inconsistency for a short period of time.

### **Multiple backend instances share one centralized database**.

The benefit of this design is that data consistency is easier to guarantee, because all instances read from and write to the same database, so there is no need to synchronize state across multiple databases. 

But the drawback is that this single database may become a bottleneck, and it may also become a potential single point of failure. So this design reduces synchronization complexity, but it increases database pressure.

### **Strong consistency is not always better; it depends on the business**

David then focused on a very important idea: **not every business requires real-time consistency. :        real-time consistency** and **eventual consistency**.

Real-time consistency means the system state must be consistent immediately. 

For example, in banking, financial transactions, account balances, transfers, and payments, these scenarios cannot accept temporary inconsistency. If money is deducted from one account, the other account must receive it immediately. We cannot say that the system will synchronize it tonight. Banking systems usually care more about real-time consistency.

Eventual consistency means the system allows short-term inconsistency, but eventually the data must be synchronized and become consistent. 

Using Walmart selling carrots as example.  Suppose one Walmart store expects to sell two tons of carrots every day, but the warehouse has four tons of inventory, so the business has a buffer. If the system shows that 2.2 tons were sold because of database inconsistency, then technically there is overselling at the database level, but from the business perspective, it is still acceptable because the warehouse still has enough inventory. After Walmart closes at night, the system can synchronize DB1, DB2, and DB3 together, find out that 0.2 tons were oversold today, and then notify the replenishment system to send 0.2 extra tons of carrots tomorrow.

This example shows that **some inconsistency is not completely prevented by technology.** Instead, it is absorbed by the business model itself. In other words, **the business model can decide the technical solution**. Retail, e-commerce, and inventory systems can often accept eventual consistency, while banking, financial, and transaction systems usually require real-time consistency.

### **Microservice architecture does not have one universal answer; different industries have different requirements**

Big companies like Walmart, Amazon, and Nordstrom may all use microservice architecture, but their business models are different, so their technical solutions are also different.

This sentence is very important: **the same architecture style does not mean the same technical design**.

*They are all microservices, but Walmart’s inventory service, Amazon’s order service, and a bank’s transaction service have different requirements for consistency, availability, latency, and fault tolerance. So we cannot simply say, “microservices should be designed this way.” Instead, we need to look at whether the business can accept delayed synchronization, whether it can accept temporary state inconsistency, and whether it can accept a small amount of overselling.*

### **The monitoring system is used to observe the status of the whole cluster**

**Monitoring platform: Actuator, Prometheus, Grafana.**

Because once you have multiple backend instances, multiple databases, caches, and a configuration server, you cannot know whether every node is healthy just by looking at the system manually. At this point, you need a monitoring platform.

**Spring Boot Actuator exposes application metrics, such as request count, error count, latency, memory usage, and thread pool status.** 

**Prometheus collects these metrics and stores them. Grafana visualizes the data from Prometheus as dashboards.**

**Grafana can also set up webhooks or alerts.** 

If 1,000 requests return a 401 exception in the past five minutes and exceed the threshold you set, Grafana can automatically ping a Slack or Teams channel. After engineers receive the warning, they go to Grafana to check which requests caused the exception, then try to reproduce the bug, find the root cause, and finally debug, fix, and optimize the system.

So the role of monitoring is not to directly fix bugs. Its role is to let you know where the system has a problem, when the problem happened, how large the problem is, and whether it has exceeded the threshold.

### **5. `@Transactional` has boundaries in distributed systems**

**Even if multiple backend instances share one database, it does not mean all transaction problems are solved.**

Spring’s `@Transactional` works inside a single Spring Boot instance. In other words, the transaction on instance 1 can only manage the database operations inside the current call chain of instance 1. The transactions on instance 2 and instance 3 are separate transaction contexts.

If your business operation crosses multiple services, multiple instances, or multiple databases, then a normal `@Transactional` may not guarantee the consistency of the whole distributed transaction. 

This is why distributed systems have more complex transaction designs, such as Saga, two-phase commit, and event-driven compensation. David did not expand on this part here. He only reminded us first that **a local transaction is not the same as a distributed transaction**.

### **The purpose of cache is to protect the database and prevent it from being overwhelmed**

**The purpose of cache is very direct: do not let every request directly hit the database.**

If all requests directly execute SQL, the database can easily become saturated, slow down, or even crash. So the system **checks the cache first. If the data is in the cache,** the system returns it directly without accessing the database. Only when there is a cache miss will the system access the database.

**Two layers of cache.**

- The first layer is **local cache**. For example, in each Spring Boot instance, we can import **Guava cache.**
    - Guava cache can basically be understood as a key-value map stored in JVM memory. It runs in the same JVM as the current Spring Boot application, so it is very fast because there is no network communication cost. After a request enters instance 1, it can check the local cache of instance 1 first. After a request enters instance 2, it can check the local cache of instance 2 first.
    - **The advantage of local cache is that it is extremely fast, because it is in local memory.** The disadvantage is that **its capacity is limited**, such as 128 MB or 256 MB, and each instance has **its own independent local cache.** The local cache of instance 1 is not the same as the local cache of instance 2, **so cache inconsistency** may also happen between them.
- The second layer is **centralized remote cache**, such as **Redis**.
    - Redis is an independently deployed cache server that can be shared by all backend instances. It is a little slower than local cache because it requires network communication, but it is much faster than the database, and it can centrally manage a large amount of cached data.
    - A more complex microservice system usually does not only have Redis. It usually has at least **two layers of cache: one local in-memory cache and one centralized Redis cache.**

## Monitoring Platform

- Actuator, prometheus, grafana.
- Knows the status of each of the applications.

## One single shared database:

**Multiple backend instances sharing one database”** to **transaction and cache layer**. 

The logic is: sharing one DB does reduce the data synchronization problem between databases, but it does not mean the system has no other problems. You still need to think about transaction boundaries and database pressure.

**Scenario: instance 1, instance 2, and instance 3 all connect to the same centralized database**.

In this design, performance will be a little better than the design where “each instance has its own independent DB, and then DB1, DB2, and DB3 still need to synchronize with each other.” 

**Why? Because you only have one database.** All instances read from and write to the same place, so you do not need to worry about DB1 and DB2 being out of sync, and you do not need to spend extra cost synchronizing the states of multiple databases. For example, if an order is confirmed or canceled in the same DB, all backend instances will see the same data.

**But even if there is only one database, you still need to worry about transaction.** 

The key point here is: **Spring’s `@Transactional` only manages the transaction inside the current Spring Boot instance. It does not automatically manage the transaction of the whole distributed system.**

For example, you have three backend instances: instance 1, instance 2, and instance 3. Each instance is an independently running Spring Boot application. When you add `@Transactional` to a service method, this transaction only works inside the method call of the current instance. Suppose a request is routed to instance 1. Then the `@Transactional` inside instance 1 can **guarantee that the database operations inside this method either all succeed together or all roll back together.**

**But if a business process crosses multiple services, or crosses multiple backend instances, or crosses multiple databases, a normal `@Transactional` cannot control everything.**

For example, suppose you have an order service and a payment service. When the user places an order, the order service needs to create the order, and the payment service needs to charge the money. 

If creating the order succeeds but charging the money fails, you cannot only rely on the `@Transactional` inside the order service, because it can only roll back the part of the database operations controlled by the order service itself. It cannot automatically roll back the database operations of the payment service, and it cannot automatically coordinate the states of the two services. This kind of problem is a distributed transaction, not a normal local transaction.

**The main meaning is: sharing one database can reduce the database synchronization problem, but it cannot automatically solve the distributed transaction problem.**

**Why do we need to add a cache layer?**

**The answer is: the cache layer is used to protect the database and prevent the database from being overwhelmed directly by requests.**

If every request comes in and directly accesses the database, directly executing SQL, then the pressure on the database will be very high. The database is important, but it is not infinitely fast. Under high concurrency, if a large number of requests hit the database at the same time, the database may become saturated. In practice, this means queries become slower, the connection pool runs out, requests time out, and in more serious cases, the whole service may crash.

**So a better design is: after a request comes in, do not query the DB immediately. Check the cache first.**

For example, the user requests employee id = 1. Normally, the backend may need to execute:

```sql
select * from employee where id = 1
```

But if this employee information has been queried before and has already been put into cache, then when the next request comes in, the system can directly get it from cache without hitting the DB again.

### Local Cache

Local cache means each Spring Boot instance has its own internal cache. For example, instance 1 has its own cache, and instance 2 also has its own cache. This cache runs inside the JVM of the Spring Boot application.

We can use Guava. Guava cache can be understood as a more advanced Java `Map`. The basic idea is very simple: key-value pair. For example:

The key is `employee:1`, and the value is the data of employee id = 1.

When a request enters instance 1, originally it should go to the DB and execute SQL. But before executing SQL, it first checks whether `employee:1` exists in the local cache of instance 1. If it exists, the system directly returns the value from cache. If it does not exist, then it queries the DB. After querying the DB, it can also put the result into cache, so next time it does not need to query the DB again.

**Local cache and the Spring Boot application are inside the same JVM.**

This means it is very fast. Because it does not need network communication, does not need to access an external server, and does not need to load data from the hard disk. It is simply looking up a key-value map inside the memory of the current application. So it is faster than accessing Redis, and much faster than accessing the database.

But local cache also has problems. Because it is local, the cache of instance 1 only belongs to instance 1, and the cache of instance 2 only belongs to instance 2. They are not naturally shared.

For example, the cache of instance 1 stores employee id = 1, name = Tom. Later, a user request goes to instance 2 and changes this employee’s name in the DB to Jerry. At this moment, the DB has Jerry, but the local cache of instance 1 may still have Tom. If instance 1 does not update or clear its cache in time, then next time a request goes to instance 1, it may still return the old data Tom.

**The tradeoff is: Local cache is very fast, but it may cause cache inconsistency, and its capacity is limited.**

You can understand the logic of this section like this:

After sharing one DB, the synchronization problem between databases is reduced, but the system still needs to consider transaction boundaries. `@Transactional` only guarantees the local transaction inside a single Spring Boot instance. It cannot automatically handle distributed transactions across services, instances, or databases. In addition, to prevent all requests from directly accessing the database, the system adds a cache layer. The request checks cache first. If it is a cache hit, the system returns directly. If it is a cache miss, then the system accesses the database. Local cache, such as Guava cache, is a key-value cache embedded in the same JVM as the Spring Boot application, so it is extremely fast. But the problem is that each instance has its own independent cache, so cache inconsistency may happen.

## Circuit Breaker

**A circuit breaker is a fault-tolerance pattern that prevents one failing service from repeatedly damaging the whole system.**

**When a downstream service keeps failing or responding too slowly, the circuit breaker temporarily stops sending requests to it and returns a fallback response or error quickly.**

**After some time, it allows a small number of test requests to check whether the downstream service has recovered before fully opening traffic again.**

- `@CircuitBreaker + Define the own fallback function.`

This section mainly explains the **practical role of a circuit breaker in microservices**, especially this idea: **when a downstream service is down, the upstream service should not keep waiting blindly. Instead, it should fail fast, execute a fallback method, protect the system, and protect the user experience.**

The scenario here has two clusters. Suppose the user first sends a request to cluster 1. Instance 1 inside cluster 1 receives the request, and then it needs to call cluster 2. The normal data flow is: the user sends a request to cluster 1, cluster 1 calls cluster 2, cluster 2 processes the request and returns a response to cluster 1, and finally cluster 1 returns the response to the user.

Under normal conditions, this is fine. But if an instance inside cluster 2 is down, the problem appears. After the user clicks the button, the request first enters cluster 1. Cluster 1 continues to send the request to cluster 2, but cluster 2 is already down, so it will not return any response. From the user’s perspective, they click the button, but nothing happens on the page. The page keeps loading and may eventually time out. This is a very bad user experience.

More seriously, this is not only bad for user experience. It can also drag down the upstream service. Because the requests inside cluster 1 keep waiting for cluster 2 to return, but cluster 2 does not return anything. These requests will be stuck inside the JVM of cluster 1. Every stuck request occupies threads, memory, and connection resources. As more and more requests accumulate, cluster 1 itself becomes slower and slower. In the end, maybe only cluster 2 was down at first, but cluster 1 is also dragged down. This is called cascading failure.

So the role of a circuit breaker is: **when the downstream service keeps failing, do not continue calling it without limit. Instead, temporarily cut off the call and go to the fallback method.**

David says the circuit breaker is written inside the Spring Boot application, and usually it is placed in the service layer implementation. For example, inside your `EmployeeServiceImpl`, there are many service methods. You can add a circuit breaker annotation to each method. Then you define a fallback method, such as `createEmployeeFallback`. When the original downstream call fails, times out, or reaches the failure threshold, the system will no longer keep calling the downstream service directly. Instead, it will automatically execute this fallback method.

What does the fallback method do? David explains two key points here.

First, **it gives the user a friendly response**. Do not let the user’s page keep spinning, and do not let the user keep clicking the button without seeing any response. The fallback method can return a user-friendly message, such as “Website is under maintenance, please give us a moment.” Or it can be more specific: “Your order has been received and is under processing.” In this way, the user at least knows that the system has received the request, instead of feeling that nothing happened.

Second, **it temporarily saves important requests**. David mentioned a reservoir database, which can be understood as a temporary holding database. For example, the user placed an order, but the downstream order service is temporarily down. This request cannot simply be lost, otherwise the company may lose the order and lose money. So the fallback method can temporarily persist this order request into the reservoir database. After the downstream service recovers, the system can take these requests out of the reservoir database and process them again.

So fallback is not simply returning “system error.” In real business scenarios, fallback can do many recovery actions: return a friendly page, save the request, write logs, send alerts, degrade the service, return a default value, or enter an asynchronous processing flow.

David then talks about the retry pattern. Sometimes the downstream service is not truly down. It may just be a short-term network fluctuation. For example, the first request fails maybe only because the network is jammed, or the downstream service is slow for a moment. So the circuit breaker can be configured with a retry mechanism.

For example, after the first failure, retry once after 3 seconds. If it still fails, retry a second time after 5 seconds. If it still fails again, retry a third time after 7 seconds. If all three retries fail, the system will think this is not just a normal network fluctuation, but that the downstream service really has a problem. At this point, the circuit breaker will consider the circuit to be broken, open the circuit breaker, stop calling the downstream service, and enter the fallback method.

You should notice that the name circuit breaker is very intuitive. It is like the circuit breaker in your home. If there is a problem in the electrical circuit, it trips and cuts off the circuit first, preventing the whole system from being further damaged. In microservices, after the downstream service is down, the circuit breaker is the mechanism that “trips.” It prevents the upstream service from continuously sending requests to the failed downstream service, and prevents the upstream service from being dragged down as well.

So the core logic of this section is:

In microservice architecture, one service often needs to call another service. If the downstream service is down, the upstream service cannot keep waiting forever, and it cannot allow requests to accumulate endlessly inside the JVM. The circuit breaker monitors these calls. If the calls keep failing or timing out, it stops calling the downstream service and executes the fallback method. The fallback method can return a friendly page, and it can also save critical requests into a reservoir database so that the system can continue processing them after recovery. To avoid breaking the circuit immediately because of a short-term network fluctuation, the circuit breaker can retry a few times first. If the retries still fail, then it truly enters fallback. The purpose of this mechanism is to protect the user experience, protect the upstream service, and prevent one service failure from dragging down the whole microservice system.

---

## Real Engineering Environment.

In the second half, David is basically pulling microservice architecture back from a “conceptual diagram” into a real engineering environment. Earlier, concepts like API Gateway, Service Discovery, Config Server, Load Balancer, Circuit Breaker, and Cache may sound like separate technical terms. Here, he is trying to say that these components do not appear out of nowhere, and they are not used just to make the architecture look fancy. Once a system moves from a monolithic architecture to a microservice architecture, new problems naturally appear one after another, and these components are added step by step to solve those problems.

The most important idea is this: microservices are not a magic wand. They help solve problems related to high availability, fault tolerance, and scalability, but they also introduce network communication cost, configuration complexity, deployment complexity, and operational complexity.

At the beginning, David says that a large project is usually monolithic at first. That means all business modules are inside one large application. For example, employee, account, order, and payment modules may all be inside the same Spring Boot project. The whole project is packaged into one jar file and deployed as one application.

Later, when the business becomes more complex, the system may be split into smaller parts. This means splitting one large system into multiple relatively independent business modules. For example, Employee Service manages employee-related logic, Account Service manages account-related logic, and Order Service handles order-related logic. Each service can be an independent Spring Boot application, with its own controller, service layer, repository, and database connection. Each service can also be deployed and scaled independently.

So he emphasizes that microservice architecture is not that mysterious. The original idea is simply to split the business into different modules and let those modules run on different servers or containers.

This also connects to your earlier question. The “service” here is not the service layer inside one Spring Boot project. Here, “service” means an independent application, an independent project, and a Spring Boot application that can be deployed separately.

After the system is split into multiple Spring Boot applications, new problems immediately appear. Where should the user request go? Which service should receive it? Where should authentication be done? Where should rate limiting be done? Where should routing be done? Where should logging, permission checks, filtering, and traffic control happen?

In theory, you could write all these common functions inside every Spring Boot application. For example, Employee Service has authentication logic, Account Service also has authentication logic, and Order Service also has authentication logic. But this is not a good design, because once you scale horizontally, every new instance has to carry the same repeated logic.

For example, when Employee Service has only one instance, you write one copy of the authentication logic. Later, when traffic increases and Employee Service scales to five instances, all five instances have to run the same authentication logic. The same problem also happens to Order Service and Account Service. This creates duplicated code, makes maintenance harder, and makes security policy changes more difficult.

That is why engineers extract these common functions and place them in an API Gateway or a larger shield/security layer. The API Gateway receives external requests and handles routing, authentication, authorization, rate limiting, request filtering, and load balancing. Then the Spring Boot services behind it can focus more on their own business logic.

So you can understand API Gateway this way: it is the single entry point of a microservice system. It is not a business service itself. It stands in front of all business services and safely routes external requests to internal services.

David then talks about Eureka, which is service discovery. His point is that in a microservice system, the same service may have multiple instances, and those instances may not be on the same machine.

For example, Employee Service may have three instances. Employee Service instance 1 may run on server A. Employee Service instance 2 may run on server B. Employee Service instance 3 may run on server C.

They are all Employee Service, but their IP addresses, ports, and deployment locations may be different. If the API Gateway or another service wants to call Employee Service, it should not hard-code one fixed IP address, because one instance may go down, and another new instance may be started.

That is why we need a service discovery server like Eureka. Every Spring Boot application knows where the Eureka Server is through annotations and YAML configuration. After each service starts, it registers itself with Eureka and continuously sends heartbeats.

David makes an important distinction here: service discovery is an independent server. It is not just a random class inside your business code.

For example, Eureka Server itself is a Spring Boot application. In the Eureka Server project, you add `@EnableEurekaServer`. Then in each client service, such as Employee Service or Account Service, you configure the Eureka Server address in the YAML file and add the required client dependency or configuration. After Employee Service starts, it knows, “I need to register myself with this Eureka Server and send heartbeats to it.”

A heartbeat means the service periodically tells Eureka, “I am still alive.” If a service instance does not send heartbeats for a long time, Eureka will consider it unavailable and stop routing requests to it.

So Eureka solves this problem: which service instances are currently alive, and where are they?

Your question about YAML is very practical. David’s answer is that annotation and YAML serve different purposes.

An annotation tells Spring Boot, “I want to enable this capability.” For example, `@EnableEurekaServer` means this application is a Eureka Server. Some client-side annotations or dependencies tell the application that it should register with a discovery server.

The YAML file tells Spring Boot the specific parameters, such as the address, port, service name, and configuration values. For example, it tells the application where the Eureka Server is, what port it uses, what the current application name is, whether this service should register itself with Eureka, and whether it should fetch the registry from Eureka.

So the annotation is more like a switch, while the YAML file is more like the detailed configuration.

David also talks about the Configuration Server. Its logic is similar to Eureka. Config Server is also an independent service, not an ordinary class inside your business code.

Why do we need a Config Server? Because after we have many microservices, each service has its own configuration, such as database URL, username, password, feature flags, timeouts, third-party API keys, and profile-specific settings. If every service hard-codes its configuration inside its own `application.yml`, maintenance becomes difficult.

A centralized Configuration Server allows these configurations to be managed in one place. When each Spring Boot application starts, it knows where the Config Server is through YAML configuration, and then it pulls its own configuration from that server.

So Service Discovery answers the question, “Who is alive, and where are they?”

Config Server answers the question, “Where should I get my configuration parameters?”

Both of them are common infrastructure components in microservice architecture.

David also distinguishes another group of components. Eureka, Config Server, and API Gateway are usually system-level infrastructure. But Circuit Breaker and Local Cache usually live inside your own Spring Boot application.

For example, Employee Service may need to call Account Service. If Account Service is down, you do not want Employee Service to keep waiting, retrying, and eventually drag down the whole system. In this case, you can add a circuit breaker to the calling logic inside Employee Service, such as Resilience4j. It monitors failures. If there are too many failures, it opens the circuit and stops calling the downstream service. Instead, it directly uses a fallback method.

Local cache is similar. If Employee Service frequently reads certain data, and you do not want to query the database or call another service every time, you can cache the data inside the application.

So Eureka, Config Server, and API Gateway are system-level infrastructure components. Circuit Breaker and Local Cache are fault-tolerance and performance optimization logic that can be implemented inside each Spring Boot service.

David also makes a very realistic point: one remote server does not always run only one application.

A remote server is basically a computer that you cannot physically touch. You can deploy your Spring Boot jar file to that machine and run it there. A server may run only one application, or it may run multiple applications.

For example, one server may run Employee Service, Account Service, Notification Service, a cache component, and a log collector at the same time.

Of course, in a modern cloud-native environment, these applications are more likely to run inside Docker containers or Kubernetes pods. But the basic idea is the same: your Java application must eventually run on some computing resource. That computing resource may be a physical server, a virtual machine, a Docker container, or an EKS pod.

This is why David says that your Spring Boot application can live in a Docker container, an EKS pod, or simply run as a Java jar on a remote server.

This point is important because it connects Spring Boot with the real deployment environment. Locally, you write code. After packaging, the code becomes a jar file. That jar file is deployed to a server, container, or pod. The real user is not accessing your IntelliJ project. The user is accessing the runtime application after deployment.

When David talks about horizontal scaling, he means that if the traffic to Employee Service increases, you do not necessarily make one machine stronger. Instead, you can start more Employee Service instances.

Originally, you may have only one Employee Service instance. Later, when QPS increases, you may have Employee Service instance 1, Employee Service instance 2, and Employee Service instance 3.

These instances may run on the same server, or they may be distributed across different servers. As long as they all register with Eureka, the API Gateway or Load Balancer can find them and distribute traffic to them.

This is the core of horizontal scaling: instead of making one machine stronger, you add more service instances to share the traffic.

This also explains why service discovery is important. When there are many instances and their deployment locations are not fixed, you need a central registry to tell the system which instances are currently available.

David then asks whether we can now see how microservices achieve their benefits. The three main benefits he mentions are high availability, fault tolerance, and scalability.

High availability means the system tries to remain available. For example, if Employee Service has three instances and one goes down, the other two can still handle requests.

Fault tolerance means the system can tolerate partial failures. For example, if Account Service is down, Employee Service can use a circuit breaker and fallback method, so the whole system does not collapse.

Scalability means the system can grow. For example, if order traffic increases, you can scale only Order Service without scaling the entire monolithic application.

So microservices are not used just because they look advanced. They are used because large systems need these abilities.

But David also emphasizes the cost of microservices.

This is one of the most important points in the whole section. In a monolithic application, Employee Module may call Account Module through a normal Java method call. That is fast, cheap, and relatively easy to debug.

But in a microservice system, Employee Service usually calls Account Service through an HTTP request, RPC request, or message queue. In other words, the request has to go through the network.

Network calls have cost. They are slower than local method calls. They may time out. They may fail. They require serialization and deserialization. They require retry logic, circuit breakers, and monitoring. They also make debugging more complicated and make distributed tracing more important.

So when David says every “from here to here” communication is an HTTP request over the network, he is saying that this is the cost you pay for microservice architecture.

Later, David draws a more realistic deployment environment. He says the API Gateway is exposed to the public network, while the service clusters behind it are inside private networks.

Public network means the network that external users can access. For example, users’ browsers, mobile apps, or external clients can send requests to the API Gateway.

Private network means the company’s internal network. Employee Service, Account Service, Order Service, databases, and caches are not directly exposed to the public internet. External users cannot directly access these services. They can only enter through the API Gateway or security layer.

The benefit is security. Even if an attacker knows that your Employee Service exists, they cannot directly access it because it is not on the public network. The external world can only access the API Gateway, and the API Gateway may be protected by a firewall, WAF, shield layer, or security team.

So in an enterprise system, the flow is usually like this: external user, public internet, shield or firewall or API Gateway, private network, internal services.

That is why, even if you are mainly responsible for writing Spring Boot business services, you still need to understand how deployment, networking, gateway, and security teams interact with your application.

David also talks about a shield layer in large companies such as Meta, Google, Amazon, or Nvidia. Here, “shield” should not be understood as one fixed product name. It is more like an internal security gateway, firewall, or access control platform built by the company.

This shield layer may handle authentication, authorization, rate limiting, routing, traffic filtering, application whitelisting, service registration, request validation, and security auditing.

It is usually not written by your business team. It is maintained by a dedicated platform, security, or infrastructure team. David says such a team may have dozens or even hundreds of engineers. They do not work on your business modules. They focus on the company-level security and access layer.

Your job is different. After you finish your Spring Boot application, you deploy it to the company’s assigned private network, server, container, or pod. Then you may need to register your application with the shield team or platform system. You provide information such as service name, application ID, private network number, private IP, port, and allowed upstream or downstream services.

After registration, they may give you a unique application ID. This ID may be placed into your YAML file or company-specific configuration. Later, when services communicate with each other or when OAuth/security validation happens, the app ID can be included to prove that this application is a legitimate whitelisted application.

So the key idea here is not coding. It is enterprise-level system governance. In a large company, not every Spring Boot service can call any other service freely. Communication usually requires registration, configuration, and authorization.

David then talks about another point that may sound confusing: different private networks may have repeated internal IP addresses. Strictly speaking, `127.0.0.1` is the loopback address, which means the local machine. His wording here is not completely precise, but his teaching intention is that different private networks are isolated address spaces, so internal addresses can overlap. You cannot identify a globally unique service location only by looking at the IP string itself.

In AWS, the related concept is VPC, which means Virtual Private Cloud. A VPC is like an isolated private network. Different VPCs usually cannot communicate with each other by default. If a service in VPC 16 needs to access a service in VPC 17, then route tables, VPC peering, Transit Gateway, NAT, security groups, network ACLs, or similar configurations may be needed to control how traffic moves.

David mentions route table. A route table basically tells the network which path should be used to reach a target address. Without route tables or network mapping, two private networks may not know how to communicate with each other.

So in a large company, a request from Service A to Service B is not only a Java code problem. It may also involve network routing, security policies, firewall rules, service mesh, DNS, and load balancers.

As an application developer, you may not need to configure all of these things yourself. But you should at least understand what people mean when they say, “Deploy this service to VPC 17,” “Register this application with the shield,” “Open routing from network 16 to network 17,” or “Add this app ID to the whitelist.”

David also explains why cloud platforms became popular. His Tom Cruise USB example is a joke, but the point is practical.

If a company buys and maintains its own servers, it has to handle physical security, electricity, cooling systems, fire protection, flood protection, hardware maintenance, network devices, physical access control, and disaster recovery.

This is complicated. Cloud platforms such as AWS, Azure, and GCP take over many of these low-level infrastructure responsibilities. After paying for the service, the company can use their data centers, networks, virtual machines, container platforms, VPCs, security groups, load balancers, and other services.

So cloud is not popular simply because it sounds advanced. It is popular because it outsources many non-business but extremely important infrastructure problems to specialized platforms.

For you as a future SDE, the point is that you mainly write Spring Boot applications, but your application will eventually be deployed to a cloud environment, server, container, or pod. You do not need to become a DevOps expert immediately, but you do need to understand basic terms like VPC, private network, public network, load balancer, EKS, pod, container, route table, and security group.

David also mentions that large companies do not only have Java Spring Boot services. They may have services written in Java, Python, C, C++, Go, and many other languages.

These services may be categorized into different layers, such as upstream, downstream, and middleware.

Upstream usually means the service that calls you, or the source of the request. For example, if API Gateway or Order Service calls Payment Service, then from Payment Service’s perspective, Order Service is upstream.

Downstream usually means the service that you call. For example, if Employee Service calls Account Service, then Account Service is downstream from Employee Service.

Middleware refers to middle-layer components, such as message queues, caches, config services, service mesh, gateways, and logging agents.

These services may be deployed in different private networks. Communication between different layers requires routing, security configuration, and permission control. That is why real enterprise systems are very complex. Running a few Spring Boot projects locally cannot fully simulate the real production environment.

At the end, another student asks whether Spring Cloud or Kubernetes is more advanced. David’s answer is very important. He says technology selection is not about which technology is always more advanced. Technologies move in and out of popularity because of business requirements, cost, organizational structure, and maintenance complexity.

He gives the example of Amazon Prime Video. Some systems moved away from microservices and back toward a monolithic or more centralized architecture because the communication cost between services was too high. The key idea is that in some scenarios with very frequent communication, strong coupling, or strong cost sensitivity, a monolithic architecture may be cheaper, simpler, and more efficient than microservices.

So we should not say microservices are always better than monoliths. That would be too naive.

A more mature understanding is that microservices are useful when the system needs independent deployment, independent scaling, fault isolation, and team-level ownership. However, they also introduce network overhead, distributed system complexity, monitoring challenges, and operational cost. If the system is small, tightly coupled, or cost-sensitive, a monolithic architecture may be simpler and more efficient.

That is what David means when he says there is no silver bullet. No single architecture can solve every problem.

In real work, this section may sound very big, as if you need to build the entire Amazon architecture from scratch. But that is not the case.

When you first join a project, you probably will not be asked to design API Gateway, Eureka, Config Server, VPC, Shield, or Route Tables from scratch. In large companies, these things are usually already handled by platform teams, DevOps teams, security teams, or infrastructure teams.

As a Java/Spring Boot developer, your most common tasks are still around writing business logic inside a Spring Boot service. You may add a controller endpoint, modify a service method, change a repository query, call a downstream API, add configuration to a YAML file, register a service name or application ID based on company documentation, work with DevOps to deploy the service to an environment, and check logs, metrics, or traces to debug failed requests.

So you do not need to independently build a complete microservice platform on day one. What you need is to understand the relationship between these components and know where your Spring Boot application sits inside the whole system.

That is the real purpose of this lecture. David is not trying to turn you into a cloud architect immediately. He wants you to hear terms like API Gateway, Eureka, Config Server, VPC, private network, shield, and route table without getting lost.

The entire section can be summarized in one sentence:

Microservice architecture means splitting a large system into multiple independent services, and each service can be deployed and scaled independently. To make these services work together safely, reliably, and efficiently, we need components such as API Gateway, Service Discovery, Config Server, Load Balancer, Circuit Breaker, Cache, Monitoring, Private Network, and Route Table. Microservices bring high availability, fault tolerance, and scalability, but they also introduce network communication cost, configuration complexity, and operational cost. Therefore, microservices are not a universal solution, and whether we should use them depends on the business scenario.

Once you understand this section, your understanding of microservices becomes much stronger. You are no longer just memorizing “API Gateway routes requests” or “Eureka does service discovery.” You understand why these components are needed after a system is split, and what engineering problems each component is solving.