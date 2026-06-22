# Mock Interview of Data Base and Design Pattern

[Homework](https://app.notion.com/p/Homework-3851db30d2df8017983fc46bcab1a494?pvs=21)

## 1. What is the Singleton design pattern?

`Singleton Scope (Spring) Singleton design pattern(Java basic)`

`Eager loading and Lazy loading, implementation, private constructor`

`Volatile Keyword`

The Singleton is a creational design pattern that guarantees **a class has exactly one instance** for the whole application, and gives you a single global access point to it. 

**I first let the single instance as private and static field.** 

**I also implement it by making the constructor private** so nobody can call new from outside.

**I establish a static getInstance method.** 

**Eager loading**

With eager initialization, **I create the single instance as soon as the class is loaded, before anyone actually asks for it.** 

I do this by initializing the static instance field at the point where I declare it, so the instance is built immediately when the class is first loaded into the JVM. 

The getInstance method then simply returns that already-created instance every time. 

The big advantage is that it's very simple and inherently thread-safe, because the instance is created once during class loading, before any threads can compete for it, so I don't need any locking at all. 

The trade-off is that the instance gets created whether or not it's ever used, which wastes resources if it turns out nobody needs it, and it isn't ideal when creating the object is expensive.

**Lazy loading**

With lazy initialization, I don't create the instance up front. Instead I leave the static instance field empty, and I only create the object the first time getInstance is actually called. 

Inside getInstance I check whether the instance is still null, and if it is, I create it; otherwise I just return the one that already exists. 

The advantage is that I save resources by never creating the instance if it's never needed, and I defer any expensive setup until it's actually required. The catch is that in a multithreaded environment this isn't safe on its own, because two threads could both find the instance null and both create one, so I have to make it thread-safe, typically with double-checked locking and a volatile field, which is where the synchronized block and the second null check come in.

**In a multithreaded environment I use lazy loading with double-checked locking with a volatile field.** 

**I check for null outside the lock for performance**, so that once the instance exists every later call returns it without paying the cost of acquiring the lock.

**I also check the null inside the lock for correctness.** That way, if two threads both pass the first check, only one of them actually creates the instance.

**The volatile keyword is required there, because it stops other threads from seeing a half-constructed object due to instruction reordering**. 

Typical uses are database connection pools, configuration objects, loggers, and caches. The main drawback is that it introduces global state, which makes unit testing harder, and that's exactly why many teams prefer dependency injection instead. 

In Spring, beans are singletons by default, but they're managed by the container, which is a cleaner approach.

`design patter`. `Singleton, Builder, Factory, Commander.`

## 2. How to write Restful API ?

`pathvariable`, `Requestparam` , `requestbody`, `responsebody`

## 3. How do you increase the Microservice fault tolerance?

`API Gateway, Service Discovery, Load Balancer, Monitoring tools (ELK), circuit breaker,` 

The way I'd approach increasing fault tolerance is to recognize that **fault tolerance isn't one single thing,** it's made up of several parts, and really the whole set of microservice components exists to support it. 

The starting point **is horizontal scaling,** where I run multiple instances of a service to avoid a single point of failure, so that if one instance goes down, others can still handle the traffic. On top of that, I'd add retry and timeout mechanisms to deal with transient failures.

But the key point I'd make is that simply running multiple instances doesn't automatically solve everything, it actually introduces new questions. 

With many instances, I now have to ask: what's the status of each one, how do I manage them, how do I balance the traffic across them, and what happens when one of them goes down. That's exactly why we set up all the other components in the architecture.

**So the API Gateway, service discovery, the load balancer, and the circuit breaker are all there to answer those questions and increase fault tolerance together.** 

**The first component is Service Discovery, such as Eureka.** 

**Service discovery continuously tracks the health status of every registered service instance.** When a new instance comes online, it registers itself, and when an instance goes down, it gets deregistered automatically. 

This means the system always has an up-to-date picture of which servers are alive and healthy, so traffic is never routed to a dead instance.

**The second component is the Load Balancer, such as Ribbon.** 

Once service discovery knows which instances are healthy, the load balancer distributes incoming traffic across those healthy instances. This prevents any single instance from being overwhelmed and ensures that if one instance goes down, the remaining ones continue to absorb the traffic without the user noticing any disruption.

**The third component is the API Gateway.** 

**The gateway sits at the entry point of the entire system and is responsible for routing incoming requests to the correct downstream service.**

 It also acts as a central place to enforce rate limiting, authentication, and authorization, which prevents the system from being overloaded by excessive or unauthorized requests in the first place.

The fourth component is the Circuit Breaker, implemented using a library like Resilience4j. Even with healthy instances and proper load balancing, individual service calls can still fail. 

**The circuit breaker monitors those failures and when a service call fails repeatedly, it trips open and stops sending requests to that service temporarily.** 

Instead of letting the failure cascade across the entire call chain and bringing down the whole system, the circuit breaker triggers a fallback method that handles the failure gracefully, whether that means retrying the request, returning a cached response, or showing the user a friendly maintenance message.

In summary, these four components work as a complete system. Service discovery knows who is alive, the load balancer spreads the traffic across healthy instances, the API gateway routes and protects the entry point, and the circuit breaker ensures that when something does fail, it degrades gracefully rather than cascading into a full system outage. Together, that is what makes a microservices architecture truly fault tolerant.

## 3.5 If we have Monolithic application, which has login logout account info features, how do you convert it to microservice?

`API Gateway, Service Discovery, Load Balancer, Monitoring tools (ELK), circuit breaker,`  

If we're converting the login, logout, and account info features of a monolithic application into microservices, **the first thing I'd establish is an API Gateway, which becomes the single entry point into the system.** 

I could use an existing implementation like AWS API Gateway or Kong, or build one within Spring Boot using Spring Cloud Gateway. **The key idea is that I'd centralize the authentication and authorization logic inside this gateway, so that all of the login and logout handling lives in one place, which keeps the rest of the modules independent and secure.**

For the authentication itself, I'd use Spring Boot together with Spring Security inside that gateway application. If there's any requirement for third-party account login, such as signing in with a Google or GitHub account, I'd enable OAuth2 to handle that integration.

**Then I'd register multiple instances of the login and logout services into a service discovery component like Eureka, along with the authorization server, so the services can be discovered and run as multiple instances for availability.** All of the credentials and configuration would be managed centrally in a Config Server, rather than being scattered across the services.

I'd also give this part its own dedicated, secure user account database, specifically for handling the login credentials, kept separate from the business data. 

The remaining business modules would sit behind a load balancer and operate on their own, while everything related to login and logout is handled inside the API Gateway to ensure each piece stays independent and secure.

Finally, I'd set up a dedicated monitoring cluster specifically for the API Gateway, because the gateway is the single entry point. If the gateway goes down, users won't be able to reach any of the business modules at all, so its availability has to be watched very closely. Overall, this same framework, an API Gateway with centralized authentication and authorization, service discovery, a config server, an independent account database, and dedicated monitoring, is the approach I'd apply to most microservice architecture problems.

## 4. Where can we set CORS, backend or frontend or both?

CORS, which stands for Cross-Origin Resource Sharing, is the rule that by default an application running on a different URL, or even the same URL on a different port, isn't allowed to send requests to another origin. My main point would be that you should always handle CORS on the backend, by keeping a whitelist of the URLs, IP addresses, and ports that are allowed to send requests to your backend. It's not something you solve from the frontend.

What makes it cleaner is thinking about it in terms of the microservice architecture. The user's UI sends its request to the API Gateway first, and the gateway is what routes and distributes that request out to the different backend web services. So the API Gateway is the only thing I actually need to keep on the whitelist. I might even run multiple API Gateways for fault tolerance, because if the gateway goes down, the user's browser can't reach any of the backend at all.

The important consequence is that I don't need to whitelist all the individual user browsers or every user's IP address, because the users never talk to my services directly, they only ever send requests to the API Gateway. The gateway is the single thing I expose to the outside world. Everything else, the whole platform, runs inside a private network, a VPC, so the backend services aren't public-facing at all.

So in practice, each backend Spring Boot application only needs to keep a whitelist of the IP addresses of my API Gateways, and nothing more. And this stays exactly the same even in a polyglot system, where some services are written in Java, some in Python, some in Node, or some in PHP. Every backend server, whatever language it's in, just needs to whitelist the API Gateways. That's the whole approach.

**CORS, or Cross-Origin Resource Sharing, is a browser security mechanism that controls whether a web page from one origin is allowed to make requests to a different origin.** 

**By default the browser blocks these cross-origin requests,** and the server has to explicitly allow them by sending response headers like Access-Control-Allow-Origin. 

So it's really the backend that decides which origins are permitted, while the browser is the one that enforces that decision.

CORS is enforced by the browser, **but it has to be configured on the backend.** The server is what sends the Access-Control-Allow-Origin and related response headers that tell the browser which origins are allowed to call it. 

The frontend can't really set CORS, because it can't grant itself permission, that would defeat the whole point of the security control. So the real answer is the backend. 

**In Spring Boot I'd configure it either on a controller, or globally through a configuration class or a CORS filter, or at the API Gateway level if there's one in front.** 

The only frontend angle is during local development, where people set up a dev proxy so requests look like they come from the same origin, but that's just a convenience for development, not an actual CORS configuration.

## 5. How do you write Hint in SQL?

A hint in Hibernate is basically a set of parameters you can use to tweak the performance of the underlying SQL statement. You set them through the **setHint method, and they tell Hibernate how you want it to run a particular query, since Hibernate translates your query into SQL against the database.** 

That said, I'd be honest that hints aren't something used very heavily in real projects. If I have a genuinely complicated SQL statement that needs tuning, I'd usually rewrite it as a native SQL query, and more often than not, adding a proper index or introducing a cache layer is a more direct and effective way to improve performance than relying on the hint API in Hibernate.

## 6. Can you write HQL in Hibernate?

Yes. HQL, which stands for Hibernate Query Language, looks a lot like SQL, but the important difference is that it works on entity objects and their fields rather than on database tables and columns. 

For example, instead of querying a person table, I query the Person entity, and instead of an age column I refer to the age field on that entity. 

Hibernate then translates that into the actual SQL for whatever database is underneath. 

That's what makes HQL database-independent, the same query works whether the database is MySQL or PostgreSQL. When I need something more dynamic or type-safe I'd use the Criteria API instead, or JPQL through the JPA EntityManager.

## 7. Monolithic versus microservices

**A monolith is a single deployable application where everything, the UI, the business logic, the data access, lives in one codebase and runs in one process.** 

**A microservices architecture splits that application into small, independently deployable services.** **Each one owns a single business capability and usually its own database.** 

They talk to each other over the network using REST, gRPC, or messaging. 

The monolith is **simpler to build, test, and deploy early on, it's all one codebase,** and **debugging locally is easy because there are no network calls between modules.** 

The trade-off is **that it's hard to scale just one part of it, a single bug can bring the whole thing down**, large teams start stepping on each other, and you're locked into one technology stack. 

Microservices give you **independent deployment and scaling per service**, teams can own their services autonomously, failures are more isolated, and each service can use a different technology if it makes sense. 

The cost is **a lot more operational complexity**, because now you have to deal with **networking,** service discovery, distributed transactions, monitoring, and **debugging that spans multiple services.** 

My own view is that for a new product I'd start with a monolith, and only extract microservices later, once the scaling needs, the team size, or clear domain boundaries actually justify that added complexity.

## 8. Would you choose stored procedures or Java and Hibernate logic?

I'd generally choose Java and Hibernate logic over stored procedures. The first reason is about where the workload runs. A stored procedure is handled by the database server, while Java and Hibernate logic is handled by my backend server, and I'd rather load most of the work onto the backend. If the backend goes down, it's just my server down, which is bad but still recoverable. If the database goes down, all my logic and the state of my data records could be lost, which can leave the whole application unable to recover.

The other reasons are flexibility and portability. Java and Hibernate logic is code I write myself, so it's flexible, easy to maintain, and easy to change. A stored procedure is logic written inside SQL that's locked to a specific database vendor. For example, a stored procedure written for PostgreSQL might not even be recognized if I move it into Oracle, because the engine won't understand the syntax. So stored procedures are lower in portability, flexibility, and maintainability, which is why I'd prefer keeping the logic in Java and Hibernate.

**A stored procedure is a piece of SQL logic that you write ahead of time and save inside the database itself, not in your application code.** 

You give it a name, and your application just calls it by that name to run it. All the work, like checking a balance, updating several tables, looping through rows, happens on the database side, close to the data.

**Java and Hibernate logic means keeping the business logic in your Java application instead, and using Hibernate to talk to the database.** 

In this approach the database mostly just stores the data, while the rules for how to process that data live in your Java code. Hibernate pulls the data into objects, your Java code applies the logic, and Hibernate saves the results back.

For my choice, It depends on the situation, but my default is to keep the business logic in Java and Hibernate. **(I prefer to keep the business logic in Java and Hibernate.)**

**I prefer that because the logic is then version-controlled, easy to unit-test, easier to debug, portable across databases, and visible to the whole team in one place.** 

**Business rules belong in the application layer. I'd reach for a stored procedure when there's a heavy, set-based data operation, something like processing millions of rows where pulling all that data into the application would be slow, or complex batch and ETL jobs, or when cutting down round-trips to the database really matters for performance.** 

Stored procedures run right next to the data, so for that kind of data-intensive work they can be much faster. The downside is that they scatter logic into the database, they're harder to test and version, and they tie you to a specific database. So my summary is, application logic by default, and stored procedures only for the performance-critical, data-heavy operations.

## 9. Person table with a name column and an age column. Return the name and the age of the oldest person.

**I'd select the name and age from the person table, order the rows by age in descending order so the oldest comes first, and limit the result to one row.** 

That gives me a single record containing both the name and the age of the oldest person. The one thing I'd mention is ties. 

If several people could share the maximum age and I want all of them rather than just one, I'd instead select where the age equals a subquery that returns the maximum age from the table, because limiting to one row would arbitrarily drop the others.

```sql
SELECT name, age
FROM person
ORDER BY age DESC
LIMIT 1;

SELECT name, age
FROM person
WHERE age = (SELECT MAX(age) FROM person);
```

## 10. Order table and customer table. Find the largest price in the last ten years and return the price along with the customer name.

I'd join the order table to the customer table on the customer foreign key so I can get the customer's name. 

Then I'd filter to only the orders whose date falls within the last ten years, by comparing the order date against the current date minus ten years. 

After that I'd order the remaining rows by price in descending order and take the top one. 

That returns the highest price within that ten-year window together with the name of the customer who placed it. 

I'd also note that the exact date syntax differs between databases, MySQL and PostgreSQL express the interval slightly differently, but the logic is the same.

```sql
SELECT c.customer_name, o.price
FROM orders o
JOIN customer c ON o.customer_id = c.customer_id
WHERE o.order_date >= DATE_SUB(CURRENT_DATE, INTERVAL 10 YEAR)
ORDER BY o.price DESC
LIMIT 1;
```

## 11. What annotations and configuration did you use for Eureka in Spring Boot?

On the Eureka server side, the key annotation is `@EnableEurekaServer`, which I put on the main application class to turn that application into the registry. 

Along with that I add the **eureka-server starter dependency,** and in the configuration I set register-with-eureka and fetch-registry to false, because the server itself is the registry, so it shouldn't register with or pull from anything.

On each client service, I **add the eureka-client starter dependency** and I can put `@EnableDiscoveryClient`, or the older `@EnableEurekaClient`, on the main class to register the service with Eureka. 

In recent versions of Spring Cloud that **annotation is actually optional,** because just having the dependency on the classpath enables it automatically. 

In the configuration I give the service a name t**hrough the [spring.application.name](http://spring.application.name/) property,** which is the name other services use to find it, and I point it at the registry through the eureka service-url defaultZone property.

For calling other services by name, I usually combine it with one of two things. 

Either I **annotate a RestTemplate bean with `@LoadBalanced`** so it can resolve service names through Eureka and load-balance across instances, or I use **OpenFeign**, where I put `@EnableFeignClients` on the main class and then declare a `@FeignClient` interface that points at the target service's registered name. 

So the main annotations I'd mention are `@EnableEurekaServer` on the server, `@EnableDiscoveryClient` on the clients, and `@LoadBalanced` or `@EnableFeignClients` with `@FeignClient` for the service-to-service calls.

## 12. What mechanism to keep/maintain fault tolerance (SLA, SLI, SLO)?

**SLI, SLO, and SLA**

These are the metrics we use to design and monitor reliability in a microservice architecture. 

**SLI stands for Service Level Indicator,** and it's the actual measurement we collect, things like latency, response time, error rate, or system boot-up time, usually gathered through the actuator to monitor each module. 

**SLO stands for Service Level Objective,** which is the internal target we set for those indicators. For example, my team might decide that latency should never exceed 50 milliseconds, so if the monitoring tool reports the latency has jumped to over a second, that's a violation of the SLO and someone needs to investigate. 

**SLA stands for Service Level Agreement,** which is the external contract that defines what happens if performance falls below a guaranteed threshold. For instance, AWS guarantees that S3 has a monthly uptime of at least 99.9 percent, with the remaining fraction accounting for the inevitable network fluctuation, and if the provider can't meet that, they refund the customer. 

So the way I'd connect them is: the SLI is what you measure, the SLO is the internal target you hold yourself to, and the SLA is the external promise you're contractually responsible for.

## **What is Circuit breaker**

**A circuit breaker is a fault-tolerance mechanism that detects when a service keeps failing and temporarily stops calling it, redirecting to a fallback instead, so that one failing service doesn't cascade and bring down the rest of the system.**

A circuit breaker is one of the mechanisms I use for fault tolerance in microservices. I set one up on each method in the service layer, and for every circuit breaker I define a fallback method that gets triggered when that service call fails. 

The ideal behavior isn't to fail immediately, it's to retry the call a few times first, and only if it's still failing after, say, three attempts, do I fall back. 

The fallback then redirects the user to a friendly error page, something that tells them this function is currently under maintenance and asks them not to keep clicking, rather than just throwing a raw error. 

In terms of implementation it's actually simple, it's an annotation on the service API plus a fallback function that holds the extra logic for retrying or redirecting. 

The whole point is to stop a failure in one service from cascading and to give the user a graceful experience while the problem is being fixed.

## 13. Why RDS in AWS?

RDS stands for Amazon Relational Database Service, and it's a fully managed relational database.

**The main reason to use it is that you don't have to rent your own server or keep running and maintaining the database engine yourself.** 

**You just pay Amazon, and they provide the database along with the hardware and the software.** 

**All you really do on your side is configure the connection, once you have the RDS you get a URL, and you set up the login username and password, and then you can just use the database.**

The other big reason is **fault tolerance for your data. Behind the scenes, AWS deploys your database across multiple servers in multiple regions.** 

For example, it might keep one copy on the east coast and an identical copy on the west coast, so if the network on the east coast goes down, the west coast is still there to support you. Depending on how much you want to pay and the level of resilience you need, this can even be cross-region or cross-continental.

**It's also flexible on the engine side, because RDS supports multiple relational database engines like Aurora, SQL Server, and Oracle, depending on the license you want to pay for.** 

And just to distinguish it from DynamoDB, RDS is for relational, SQL databases, whereas DynamoDB is a NoSQL database more comparable to MongoDB, so they serve completely different purposes.

## 14. What annotation you use for Eureka?

When setting up Eureka in a Spring Boot project, the key annotation you need is `@EnableEurekaServer`, which is placed on the main application class; that is the same class that carries the `@SpringBootApplication` annotation. This annotation tells the Spring Boot application that upon startup, it should register itself with the Eureka service discovery server.

On the configuration side, there are a few important properties to define in the application.properties or YAML file. 

**First, you set the application name using `spring.application.name` so that other services can identify it within the registry.** 

**Second, you specify the server port.** 

**Most critically, you provide the Eureka server's URL and port number, which tells the application exactly which service discovery instance it should register with.** 

This is especially important in a remote or production environment where the Eureka server is running on a separate host.

## **15. Did you implement any fault tolerance mechanisms in the service layer?**

Yes. I configured Circuit Breakers at the service layer using Resilience4j.

Each service method was wrapped with a circuit breaker. For each one, I also defined a fallback method.

The way it works is simple. If a service call fails, the system automatically retries. Usually up to three times. If all three retries fail, the circuit breaker triggers the fallback method.

The fallback doesn't let the app crash. Instead it redirects the user to a friendly error page telling them the feature is temporarily down and will be back once the issue is fixed.

This way failures are handled gracefully and don't cascade through the whole system.

## 16. What is your responsibility in the microservices? (Personalize this before the interview.)

In our microservices architecture, the platform contained around 20 to 30 modules in total. 

I was primarily responsible for developing and maintaining five to six business modules, such as the Employee Service, Transaction Service, Order Service, and Report Service. 

My day-to-day work involved both frontend and backend development, implementing new features and maintaining existing ones within those modules.

**However, being a full stack developer in a microservices environment means you are not isolated to just your own modules. About 20 percent of my time was spent engaging with the broader architecture components.** 

For example, whenever I brought a new module online or extended an existing one with new endpoints, I had to coordinate with the DevOps team to register the new routes and RESTful endpoints to the API Gateway, and register the service instances to Eureka, our service discovery tool, so that other services could locate it properly.

**On the API Gateway side, I was involved in integrating Spring Security with OAuth2 to handle authentication and authorization, and I also implemented a rate limiter to prevent users from sending excessive duplicate requests.**

For monitoring and debugging, **I used tools such as Grafana, Kibana, Elasticsearch, and Splunk to check logs, write timestamp queries, and identify which microservice module a failing request was coming from.** 

I also periodically checked the API Gateway to ensure it was routing traffic correctly.

Overall, my responsibility spanned both the business logic layer and the infrastructure support layer, making sure the modules I owned were running correctly while also contributing to the stability of the overall microservices architecture.

---

## 17. If services call each other in a chain, A to B to C, and some of them return 500s or errors, what should we do?

**The very first priority is to stop the bleeding**. 

When a service is down, the company is losing money every second, so the goal is to restore service as quickly as possible before doing any investigation.

**If we have standby servers prepared in advance for critical services, the first thing we do is redirect traffic to those standby instances to keep the platform running.** 

**For services where real-time processing is not strictly required, we can also activate a reservoir database to temporarily persist all incoming requests.** 

**We return a success message to the user, such as confirming their order was received, and then process those requests once the service recovers.** 

This works well for an e-commerce platform but would not be appropriate for something like stock trading where everything needs to be handled in real time. Kafka message queues can also serve a similar purpose, since messages are retained for up to 30 days by default, meaning no data is lost even if a service goes down.

**Once the bleeding is stopped, we trace the call chain to identify the origin of the failure.** For example, if service A calls B and B calls C, and the 500 errors are coming from the B to C leg, then service C is likely the problem. 

We restart the affected service or cluster and verify whether the issue is resolved. After the service is back up, **we go into the monitoring tools such as Grafana, Kibana, or Elasticsearch to check the logs and identify what requests originally triggered the 500 errors.** 

It is also worth noting that sometimes the root cause is not application logic at all, but infrastructure, such as an AWS outage.

## 18. How do you secure communication in microservices?

There are three main layers to securing microservice communication.

**The first is enforcing HTTPS across all inter-service communication.** HTTPS adds an SSL security layer on top of HTTP, using asymmetric encryption to protect data in transit and prevent man-in-the-middle attacks between modules.

**The second is network isolation. Only the API Gateway should be exposed to the public internet. All other components, including business service modules, databases, and cache services, should remain on a private internal network.** This means that even if an attacker is trying to probe the system, they can only reach the API Gateway. Everything behind it is completely invisible from the outside.

**The third is securing the API Gateway itself.** Even though the internal services are on a private network, we should still enable HTTPS for internal communication in case the API Gateway is ever breached. On top of that, the API Gateway should enforce both authentication and authorization, for example through Spring Security combined with OAuth2, to ensure that only verified and permitted requests are allowed through.

## 19.Where Do You Use JOIN, GROUP BY, and COUNT in SQL?

JOIN is used when you need to retrieve data from multiple tables at the same time. An INNER JOIN returns only the records that exist in both tables, while a LEFT or RIGHT JOIN retains all records from one side of the join regardless of whether a match exists in the other table.

GROUP BY is used to categorize rows based on the values in a specific column. For example, if you want to organize all employees by their department, you would group by the department column.

COUNT is an aggregation function that is typically used together with GROUP BY. For instance, if you want to find out how many employees are in each department, you would use COUNT in your SELECT statement and GROUP BY the department column. This gives you a summarized view of the data grouped by category.

## 20.How do Microservice communicate with each other?

Microservices communicate through HTTP requests and responses via RESTful endpoints.

In Spring Boot, there are a few ways to implement this.

**RestTemplate** — the basic way. You call another service via HTTP. But the URL is hardcoded, so it only hits one fixed instance.

**Feign Client** — solves that problem. Instead of a fixed URL, you register a service name. Feign Client finds the available instances through Eureka and load balances across them using Ribbon.

Both are synchronous. As QPS grows, requests pile up and latency increases.

**WebClient** — asynchronous. Built on Spring WebFlux. 100 requests come in, all processed in parallel instead of waiting in line.

**Kafka** — the most modern approach for high QPS. Instead of calling another service directly, you publish a message to Kafka and the downstream service consumes it. High throughput, messages retained for 30 days, no data loss even if a service goes down.

In practice, async is always preferred. And for high traffic, Kafka is the go-to solution.

**Microservices primarily communicate through RESTful endpoints using HTTP requests and responses.** However, the way we implement this in a Spring Boot application can vary depending on the use case, and it is important to distinguish between synchronous and asynchronous approaches.

The most straightforward way is using RestTemplate, which is a built-in Spring Boot HTTP client. You create a RestTemplate object, define the target URL, send the request, and receive the response entity back. This works fine for simple cases, but the limitation is that the URL is hardcoded, meaning you can only send requests to one fixed server instance. In a real microservices environment where a single service may have multiple instances running behind a load balancer, hardcoding the URL defeats the purpose.

To solve that problem, we use Feign Client, also known as OpenFeign. Instead of providing a fixed URL, you define an interface and register a service name, for example the application name configured in Eureka. Behind the scenes, Feign Client resolves that service name to the actual available instances through Eureka, and then uses Ribbon to load balance the request across those instances based on strategies such as round robin, closest geographical location, or smallest latency. This makes inter-service communication much more flexible and resilient.
Both RestTemplate and Feign Client are synchronous by nature, meaning each request blocks and waits for a response before the next one proceeds. This becomes a serious problem as QPS grows, because requests start piling up, latency increases, and the whole communication channel between services gets jammed. To address this, RestTemplate can be wrapped with CompletableFuture to send requests asynchronously in parallel. Alternatively, Spring WebFlux provides a WebClient which is built on reactive programming using Mono and Flux, and supports asynchronous communication natively. With WebClient, all requests are processed as a data pipeline in a non-blocking way, so 100 users coming in at the same time will have their requests sent in parallel rather than waiting in line.

However, the most robust and modern approach for high QPS traffic is using a messaging queue such as Kafka. Rather than directly hitting another service's RESTful endpoint, you publish a message to Kafka and the downstream service consumes it on its own. Kafka is specifically designed for high throughput, has strong buffering capabilities, and supports a retention policy where messages are kept for up to 30 days by default, meaning no data is lost even if a service goes down temporarily. Most large-scale modern platforms prefer Kafka over direct REST calls precisely because of these performance and reliability advantages.

In summary, when answering this question in an interview, you should cover all of these layers: starting from RESTful endpoints and HTTP, then RestTemplate, then Feign Client, then WebClient, and finally Kafka. Most importantly, you should emphasize that in practice, asynchronous communication should always be the preferred approach to ensure backend performance and scalability, and that Kafka is the go-to solution for high traffic environments.

## 21.What is strategy design pattern?

The Strategy pattern is about taking different behaviors and wrapping each one into its own class.
You define one interface with a common method. Then each behavior implements that interface separately.
When you need to switch behaviors, you just swap in a different class. The main code stays the same.
The benefit is you avoid writing a bunch of if-else or switch statements. And if you need to add a new behavior later, you just create a new class without touching existing code.

## 22.What Microservice Patterns Have You Used?

This question can be interpreted in two different ways depending on the context of the conversation, so I would like to cover both angles.

**From a design pattern perspective.** 

The main microservice pattern I have used is the Circuit Breaker pattern, which is designed to handle graceful degradation when a service fails. 

The idea is that instead of letting a failing service call propagate errors or cause the entire system to hang, the circuit breaker detects the failure and triggers a fallback method instead. 

In my implementation I used the **Resilience4j library to apply circuit breakers at the service layer.** 

Each service method was annotated with a circuit breaker configuration, and a corresponding fallback method was defined to handle failures, whether that meant retrying the request, returning a cached result, or redirecting the user to a friendly error page informing them that the feature is temporarily unavailable.

**From an architectural pattern perspective, there are two distinct approaches to building a microservices architecture, and I have experience with both.** 

**The first is the Spring Cloud approach**, which I would describe as an **intrusive style.** This is because it requires you to make direct changes to your existing Spring Boot codebase, such as adding annotations like `@EnableEurekaServer` and configuring the Eureka server URL in your application properties or YAML file. While this approach works well, it does mean that the microservices infrastructure concerns are mixed into your application code.

**The second approach is the Kubernetes style, which is non-intrusive.** Kubernetes is a container orchestration platform that manages your services across different pods and clusters. What makes it powerful in the context of microservices is that components like the API Gateway, service discovery, and load balancer all have native counterparts within Kubernetes itself. 

**This means you do not need to modify your application code at all to support these architectural concerns, as Kubernetes handles all of the traffic routing and service management externally.** This is considered the more modern approach and is increasingly the industry standard for microservices deployments.

So depending on what the interviewer is asking, my answer would focus on the Circuit Breaker pattern if they are asking about design patterns, or contrast the Spring Cloud and Kubernetes approaches if they are asking about architectural styles.

## 23. Why we use database index?

**A database index is used to improve the performance of read operations.** 

When a specific column is frequently used in WHERE conditions or ORDER BY clauses, creating an index on that column can significantly reduce the time it takes for the database engine to look up the relevant records. 

However, you should not create indexes blindly on every column, because indexes come with trade-offs that need to be carefully considered.

In terms of types, there are two categories of indexes. 

**The first is a clustered index,** of which each table can only have one. By default, the primary key is set as the clustered index. What is important to understand about a clustered index is that the actual data of the table is physically stored in the data structure of the clustered index on disk. So while we conceptually think of table data as rows and columns, in reality the data is organized according to the clustered index structure. This means that choosing the right column for your clustered index actually matters in practice, and it should be based on the most frequent search pattern for that table rather than always defaulting to the primary key. 

**The second type is a non-clustered index, of which a table can have multiple. These are created on other columns that are frequently queried but are not the clustered index.**

****Regarding trade-offs, indexes come with two main costs. 

The first is additional storage space, because an index is an entirely separate data structure, such as a B Tree, B+ Tree, Hash Table, or Bitmap, that needs to be maintained alongside the actual table data. 

The second and more significant cost is that write operations become slower. Every time you perform an INSERT, UPDATE, or DELETE, the database engine also has to update and maintain all the relevant index structures. 

When a table contains millions of records, modifying the index data structure can sometimes break its existing format, forcing the engine to rebuild the entire index, which creates substantial overhead and slows down write performance considerably.

It is also worth noting that different database engines such as Oracle, PostgreSQL, and SQL Server may use different default data structures for their indexes. In most cases the default is either B Tree or B+ Tree depending on the engine, but you can also explicitly specify the data structure type when creating an index.

In summary, indexes are a powerful tool for optimizing read performance, but they trade off write performance and storage space. The key is to create indexes selectively based on actual query patterns rather than adding them everywhere.

## 24.How to prevent SQL injection?

SQL injection is a security vulnerability that occurs when user input is directly concatenated into a SQL string instead of being handled safely. 

For example, if you are using a plain Statement in JDBC and building your query by concatenating user-provided values, a malicious user could input something that manipulates the WHERE clause, such as providing an always-true condition, to bypass authentication or access data they should not be able to see.

**The way to prevent SQL injection is to use a `PreparedStatement` instead of a plain Statement in JDBC.** With a PreparedStatement, you use placeholders in your SQL query rather than directly inserting user input into the string. 

**The database engine then treats the user input strictly as data, not as part of the SQL syntax, which eliminates the possibility of the input being interpreted as a SQL command.**

If you are using an ORM framework such as Hibernate or Spring Data JPA, SQL injection prevention is handled for you automatically under the hood, because these frameworks use the same placeholder approach internally when generating and executing SQL statements. 

So as long as you are working within the ORM and not writing raw concatenated queries, you are protected by default.

In summary, the key principle is to never build SQL queries through string concatenation with user input. Always use PreparedStatements or rely on an ORM framework, both of which ensure that user input is treated as data rather than executable SQL code.

## 25. What API Gateways Have You Used Before?

In my experience, I have worked with both the **Spring Cloud Gateway** and **more modern API gateway solutions**, and I think it is important to understand the distinction between the two approaches.

In earlier projects, I worked with Spring Cloud Gateway as part of a Spring Cloud based microservices architecture. 

It handles routing, rate limiting, authentication, and authorization at the gateway level, and integrates well with other Spring Cloud components like Eureka for service discovery. However, this is considered the intrusive style, meaning the gateway configuration is tightly coupled with the application codebase.

For more modern projects, the industry has largely shifted toward Kubernetes-based architectures, and there are several API gateway solutions that integrate with Kubernetes clusters. 

**One of the most well known is the Amazon API Gateway, which is a fully managed service on AWS that handles routing, rate limiting, authentication, and other gateway concerns without requiring you to manage the underlying infrastructure yourself.** 

**Another popular option is Kong API Gateway, which is open source and highly customizable, allowing teams to extend and modify the gateway logic according to their specific needs, though an enterprise license is also available.** 

*Google Cloud also offers their own solution called Apigee API Management, which is a fully managed API platform that provides similar functionality including routing and rate limiting as a paid service.*

In summary, while Spring Cloud Gateway is still relevant for maintaining older platforms, the modern preference is to use a dedicated API gateway solution that integrates with Kubernetes, such as Amazon API Gateway, Kong, or Apigee, depending on the cloud platform and business requirements of the project.

## 27. SQL with join, group by, and count

A typical example would be counting how many orders each customer has placed. I'd join the customer table to the order table on the customer key, and I'd specifically use a left join so that customers who have zero orders still show up, with a count of zero, whereas an inner join would silently drop them. Then I group by the customer and use the count function on the order key, which only counts the non-null matches. One rule I'd mention is that anything in the select list that isn't aggregated has to appear in the group by clause. And if I needed to filter on the aggregate itself, say only customers with more than five orders, I'd use a having clause rather than a where clause, because where filters the rows before grouping and having filters after the grouping has happened.

##