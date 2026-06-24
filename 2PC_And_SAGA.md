# 2PC and SAGA

**This part mainly talks about distributed transactions in a microservice architecture, and why a normal `@Transactional` annotation is not enough when the business flow crosses multiple services.**

## Transactional for separate database.

Assume:

- A microservice architecture with an **employee service cluster** and an **audit log cluster**. Each cluster may have multiple Spring Boot application instances.
- Each Spring Boot application has its own controller layer, service layer, and DAO layer. The DAO layer interacts with the database.
- The database can be a SQL database, such as employee and account tables, or it can be a NoSQL database, such as a MongoDB audit collection.

**Transactions inside a single Spring Boot application.** 

For example, when we create a new employee, the business flow may require two SQL statements: 

first, insert a new record into the employee table; second, insert a new record into the account table. 

The business requirement is that every employee must have an account. If the employee record is inserted successfully but the account insertion fails, then the whole create employee procedure should be considered failed. Without an account, the employee cannot participate in any workload in the company system.

**So here we need atomicity.** 

That means either both insert operations succeed, or neither of them should be executed.

This is the core idea of a transaction. In Spring Boot, we can use `@Transactional` to make sure that the database operations inside one method body are executed as one transaction. 

`@Transactional` is implemented through **AOP proxy**. Spring wraps the method with a proxy object, and the proxy controls the transaction before and after the method execution.

**But the problem is that `@Transactional` only works inside the same Spring Boot application, or inside the same JVM.**

For example, after the employee is created and the account is created, we may also need to insert an audit log in another service. This audit log may be handled by another Spring Boot application, and its database may even be MongoDB. For example, we insert a log saying that employee ID 101 has been created and account ID X10001 has been activated.

From the business perspective, if the employee is created and the account is activated, but the audit log is not inserted, then the whole business flow may still be considered failed. The audit log is also part of the business requirement. However, the employee and account operations are inside the employee service, while the audit log operation is inside the audit service. These are two different Spring Boot applications running in two different JVMs.

**At this point, a normal `@Transactional` annotation is not enough.** 

It cannot guarantee atomicity across different JVMs, different services, or different databases. **This kind of transaction across multiple services is called a distributed transaction.**

**Comparison between the monolithic architecture with microservice architecture:**

In a monolithic architecture, all business logic is inside one large Java application, and all DAO operations are usually inside the same project. So technically, `@Transactional` can usually handle the transaction problem. 

But once we split the business flow into different microservices, such as employee service, account service, and audit service, one complete business flow may involve multiple services. **Then we need to deal with distributed transactions. This is one of the trade-offs when migrating from monolithic architecture to microservice architecture.**

## **2PC, or Two-Phase Commit**.

**The core idea of 2PC is to introduce an independent coordinator.** 

The coordinator can be understood as a separate service. It is responsible for knowing which services are involved in the whole business flow, what the execution status of each service is, and whether each step can be completed successfully.

For example, in an employee registration flow, the first step is that the employee service needs to insert the employee and create the account. The second step is that the audit service needs to insert the audit log. 

The coordinator will first ask the employee service: “Can you insert this employee? Can you create this account?” At this stage, the service does not really commit the data yet. It only checks whether the operation is executable. For example, it checks whether the employee ID already exists, whether the account already exists, whether the database connection is working, whether the table exists, and whether the SQL statement can be executed.

If the employee service says both SQL statements can be executed, the coordinator will then ask the audit service: “Can you insert this audit log?” The audit service also checks its database, collection, connection, and constraints. If it also says the operation can be executed, **then the coordinator knows that all participating services are read**y.

**This is the first phase of 2PC: the preparation phase. In this phase, the services do not really commit the data. They only check whether they are able to complete their own operations.**

**After all services return that they are ready, the system enters the second phase: the execution phase, or commit phase.** 

The coordinator sends confirmation to all involved microservices and tells them that they are allowed to actually execute or commit their operations. Then the employee service inserts the employee and account, and the audit service inserts the audit log. In this way, the system tries to make sure that the cross-service business flow succeeds together.

**2PC is a strong consistency design pattern.** 

The advantage is that it is relatively easy to understand: first, check whether every service can execute successfully; if all services are ready, then commit; if any service is not ready, then nobody commits. This can reduce inconsistent situations, such as the employee being created but the audit log not being inserted.

**But there is a limitation.**

**2PC does not provide a very flexible rollback mechanism.** 

Its main idea is not to do complicated rollback after failure. Instead, it tries to check everything in the preparation phase before the real commit happens. In other words, the logic is not “fail first and then roll back everything.” The logic is more like “confirm that everyone can succeed first, and then let everyone commit.” 

In one sentence, this part demonstrates that **inside a single Spring Boot application, we can use `@Transactional` to guarantee transactions; but in microservices, because the business flow may cross services, JVMs, and databases, `@Transactional` cannot handle everything, so we need distributed transactions; and 2PC is a strong consistency solution that uses a coordinator to manage multiple services through a preparation phase and a commit phase.**

---

**The implementation logic of 2PC, the code demo, and the disadvantages of 2PC**. 

The core idea is this: what he explained on the whiteboard earlier is the real distributed transaction scenario across multiple microservices and multiple servers. Now he is using one Spring Boot application connected to two databases as a conceptual demo, so that everyone can understand how 2PC guarantees transactions across databases.

---

**A more detailed explanation from David**

He first continues from the previous part. In the first phase of 2PC, which is the preparation phase, the system checks whether all queries inside the employee service can be executed. If the employee service confirms that inserting the employee and inserting the account can both be executed, then the coordinator will follow the business flow defined in the service layer and move to the next step, such as the audit service. In a real business flow, there may be more steps, such as step 3, step 4, or step 5. 

**Only after all steps confirm that they can be executed will the system move to the second phase, which is the execution phase.** In the second phase, the coordinator sends confirmation to every involved microservice module and says, “You are now allowed to actually execute the SQL statements.” In this way, the distributed transaction can guarantee that either all related services execute successfully, or none of them executes.

**This is a strong consistency design pattern.** 

It is relatively easy to understand, because if any query cannot be executed during the preparation phase, the whole flow will not move to the execution phase. So theoretically, there is not much rollback to do, because the system has not really committed anything yet. If the check fails during the preparation phase, the whole business flow simply fails, and no service is allowed to commit by itself.

*Then he emphasizes that the code demo he is going to show is not exactly how it works in a real production environment. It is only a conceptual demo. In a real system, the employee service should run on one server, the audit service should run on another server, and the coordinator should also be an independent service running on a third server. They communicate with each other through HTTP requests. However, for the convenience of classroom demonstration, he puts everything inside the same Spring Boot application. This application simply connects to two different databases at the same time. So even though it is not a fully realistic microservice deployment, the underlying idea is the same.*

*In his demo, there is an EMS Spring Boot application. Previously, this application only connected to one EMS database, which contained the employee table. Now he adds a second database called the audit log database, which contains the audit table. Both databases are H2 in-memory SQL databases. The EMS database contains the employee table, with fields such as employee ID, email, first name, and last name. The audit database contains the audit table, which records audit events, such as audit ID, action, employee ID, employee email, and the time when the audit event happens.*

**Code Demo**

Next, he explains the configuration. Usually, **we configure one datasource in `application.properties`,** such as the database URL, username, and password. 

**Spring Boot will use the default datasource auto-configuration and transaction manager auto-configuration to set everything up for us.** 

But now he has two databases: one EMS database and one audit database. The default configuration usually works for a single datasource, so he explicitly excludes Spring Boot’s default datasource auto-configuration and datasource transaction manager auto-configuration.

In other words, **he does not want Spring Boot to use the default transaction manager.**

Because the default one mainly handles transactions for a single database. Now he wants to **manage transactions across two databases,** so he writes his own configuration class and manually configures two datasource beans: one primary datasource connected to the EMS database, and one audit datasource connected to the audit database.

**He also configures the corresponding JDBC template.** 

For example, the audit JDBC template receives the audit datasource, so this JDBC template knows that it should connect to the audit database. Later, when the audit repository inserts an audit log, it uses this audit JDBC template to write data into the audit table in the audit database.

**He also mentions Atomikos, which is a third-party library.** 

This library provides transaction management capability across multiple datasources. Because Spring Boot 3 uses Jakarta, while some libraries may still involve Javax/Jakarta compatibility issues, there may be some adapter or bridge code in the demo. But David says we do not need to worry too much about that part, because this demo is only used to show the concept. In a real microservice system, these components would be split into different servers.

**On the service layer, the service layer is still using the same `@Transactional` annotation.** 

But this time, the transaction manager behind `@Transactional` is **no longer Spring Boot’s default single-database transaction manager.** Since he has excluded the default configuration and configured Atomikos transaction manager by himself, this Atomikos transaction manager works like the 2PC coordinator in this demo. It can manage both the EMS database and the audit database at the same time.

*This is also the key point you asked him about in class. You asked, “Isn’t `@Transactional` only able to guarantee transactions inside the same database? Now you are working with two different tables and two different datasources. How can it still work?” **David answered that the annotation is still the same annotation, but the implementation behind it has changed.** Previously, the default transaction manager only supported a single database. Now he has configured Atomikos transaction manager, so `@Transactional` will call this new transaction manager, and this transaction manager can coordinate commit and rollback across two databases.*

**An actual test:**

His create employee API is still the same RESTful endpoint. After the request comes in, the service layer first uses the employee repository to insert the employee into the EMS database, and then uses the audit repository to insert an audit log into the audit database. Under normal conditions, the request returns `201 Created`. The employee table will contain the employee record, and the audit table will also contain the corresponding audit log.

To test whether the transaction really works across two databases, he manually throws a runtime exception after the employee insertion. This simulates a situation where the employee has been inserted successfully, but something goes wrong in a later step. Without cross-database transaction management, this could cause data inconsistency: the employee may already be inserted, but the audit log may not be inserted. However, in his demo, because the Atomikos transaction manager manages both datasources, once the exception happens, the employee insertion is rolled back, and the audit insertion is not executed. After checking the databases, the employee table is empty and the audit table is also empty. This shows that the transaction across two databases is working.

Then he comments out the line that manually throws the exception and runs the request again. This time, the request succeeds and returns `201`. After refreshing the databases, we can see that the employee table has an employee record, and the audit table has an audit log record. This shows that when there is no exception, both database operations are committed together.

**In a real architecture, it is not one Spring Boot application connecting to two databases. Instead, multiple services run on different servers.** 

For example, service 1 operates on its own database, service 2 operates on its own database, and service 3 operates on its own database. The business requirement is that either service 1, service 2, service 3, and service 4 all succeed, so the whole business flow succeeds, or none of them executes. To achieve this, we need distributed transactions.

**The role of the coordinator is to synchronize the execution status of all services. It has two phases: the first phase is preparation, and the second phase is execution.** 

During the preparation phase, the coordinator asks service 1, service 2, service 3, and service 4 one by one: “Are you ready to execute?” 

Only when all services confirm that their following queries can be executed successfully will the coordinator move the whole process to the execution phase. 

If one service, such as service 15, says it is not ready to execute the SQL, then the whole request will stay in the preparation phase and will not move to the execution phase. Therefore, the system can guarantee “all or nothing.”

**The disadvantages of 2PC.** 

**The first disadvantage is latency.** 

Because 2PC has two phases, and the coordinator needs to wait for all services to confirm. If service 1 and service 2 confirm quickly, but service 3 is slow, then the whole distributed transaction has to wait for service 3. This slow service becomes the bottleneck.

**The second disadvantage is that the coordinator may become a single point of failure.** 

The original purpose of microservice architecture is to achieve high availability, scalability, and failure tolerance. **But if all distributed transactions depend on the coordinator, once the coordinator goes down, even if the employee service, audit service, and account service are still alive, distributed transactions cannot be executed normally.** 

So in production, the coordinator usually needs a high-availability design, such as multiple coordinators behind a load balancer. In this way, even if one coordinator goes down, other coordinators can still serve requests. The total QPS may decrease, but the whole platform will not completely shut down.

In one sentence, David is saying that **he uses one Spring Boot application connected to two H2 databases to simulate a distributed transaction; by customizing datasources, the transaction manager, and Atomikos, he makes `@Transactional` work across two databases; this demo reflects the idea of a 2PC coordinator in a real microservice architecture. 2PC can guarantee strong consistency, but the cost is higher latency, and the coordinator itself must be highly available, otherwise it can become a bottleneck or a single point of failure.**

---

## **Saga design pattern**

Another solution for distributed transactions in microservices. 

The biggest difference between Saga and 2PC is this: 

**2PC focuses on strong consistency, while Saga focuses more on eventual consistency.** 

**2PC uses a coordinator to prepare first and then commit, while Saga usually does not have a preparation phase. Instead, it executes first, and if something fails, it uses compensation logic to undo the previous successful steps.**

**The problem with 2PC is that it has a centralized coordinator.** 

All services need to report to the coordinator whether they are ready, and then the coordinator decides whether the whole flow can move to the commit phase. This gives strong consistency, but it also creates latency and may create a single point of failure. In order to avoid this centralized mechanism in 2PC, we can use another design pattern called the **Saga pattern**.

The basic idea of Saga is: 

**Do not check everything first, and do not wait for a coordinator to approve everything.**

 **Instead, let the business flow execute step by step.** 

**If one step succeeds, it calls the next service. If one step fails, then we use compensation logic to undo the operations that have already been successfully executed.**

For example, suppose a business flow requires this order:

S1 → S2 → S3

After the user clicks a button, the request first goes to S1. S1 executes its own database operations. If S1 succeeds, it sends a signal to S2. Then S2 executes its own database operations. If S2 succeeds, it sends a signal to S3. Then S3 executes its own database operations. This process is different from 2PC. Saga does not first ask every service, “Are you ready?” It just executes the flow step by step.

If something fails in the middle, for example S2 fails, then the system will not continue to call S3. At the same time, it needs to undo what S1 has already done. This undo operation is not an automatic database rollback, because the transaction in S1 may already have been committed. Saga uses **compensation operations**.

If the operation in S1 is to insert an employee, then the compensation SQL may be to delete that employee. In other words, the forward operation is inserting an employee, and the compensation operation is deleting that employee by employee ID.

If the forward operation is an update, then the compensation operation may be another update that changes the data back to the original value.

If the forward operation is a delete, then the compensation operation may be inserting the deleted record back.

**So in Saga, every important business operation should usually have a corresponding compensation method. In this way, when a later step fails, the system can call the compensation APIs of the previous steps to undo the business actions that have already been completed.**

**One Style of Saga: Choreography style.** 

**In this style, there is no central coordinator. Each service is responsible for executing its own logic and then sending a signal to the next service.** 

For example, after S1 succeeds, it notifies S2. After S2 succeeds, it notifies S3. If S2 fails, it should not continue to notify S3, and it should trigger compensation for S1. This design is decentralized, because the flow control is distributed across the services.

*Understand choreography this way: each service knows which service to notify next, and it also knows which compensation operation should be triggered if something fails. There is no central commander. The services move the process forward by passing events or requests to each other.*

**Saga provides eventual consistency, not strong consistency like 2PC.**

The reason is that during Saga execution, S1 may have already written data into its database, and S2 may have already written data into its database, but S3 may still be running. 

During this short time window, **the system state is not fully consistent.** The data in the first two services has already changed, but the data in the third service has not changed yet. **However, if S3 eventually succeeds, the whole system will eventually reach a consistent state. This is called eventual consistency.**

**2PC is different.** 

In 2PC, the system prepares first. Only after all participants confirm that they are ready will the coordinator allow them to commit together. So 2PC is closer to real-time strong consistency. Saga executes first and compensates after failure, so it cannot guarantee that the system is consistent at every single moment. It can only try to make the system eventually consistent.

**The second style of Saga —— Orchestration style.**

Orchestration is different from choreography. 

In choreography, services send signals to each other by themselves. In orchestration, we introduce a dedicated application or service to control the whole business flow. 

This application is not the same as the coordinator in 2PC, **because it does not do preparation and it does not manage strong-consistency commits.** It is more like a **flow controller** or **orchestrator**. Its job is to tell each service what to do next.

*For example*, 

this orchestration service can also be a Spring Boot application with a controller layer, service layer, and DAO layer. In its service layer, we define different business flows. One business flow may be S1 → S3 → S2 → S5. 

Another business flow may be S3 → S7 → S13. Each controller endpoint may correspond to a specific business flow. After a request comes in, this orchestration service sends signals in order: first call S1, then call S2, then call S3.

**The difference between orchestration and choreography is this: in choreography, S1 calls S2 by itself, and S2 calls S3 by itself. In orchestration, a centralized orchestration application is responsible for calling S1, S2, and S3.**

**The advantage of orchestration** 

**Flow control is more centralized and the business flow is easier to change**. If the business flow changes, we do not need to modify the calling logic inside every service. We mainly need to modify the flow definition inside the orchestration service. 

For example, if the old flow is S1 → S2 → S3, and now we want to change it to S1 → S4 → S3, we mainly change the orchestrator, instead of modifying S1, S2, and S3 separately.

### So the core summary of this part is:

**Saga is a solution for distributed transactions, but it does not work like 2PC. It does not prepare first and then commit. Instead, Saga directly executes each step. If a step succeeds, the flow continues. If a step fails, the system calls compensation methods to undo the previous successful operations. Saga has two common styles: choreography, where services pass signals by themselves in a decentralized way; and orchestration, where a dedicated orchestrator service centrally controls the business flow. The advantage of Saga is that it reduces the strong centralization problem of 2PC and improves availability and scalability. The disadvantage is that Saga provides eventual consistency, not strong consistency like 2PC.**

---

**Summary and Recall.** 

Explaining **Saga choreography and Saga orchestration**. 

Compares the essential difference between **2PC and Saga**. 

*In orchestration, if one step has already been completed and a later step fails, do we still need to undo the previous steps?* 

YES, **we still need to undo them**. 

The difference between orchestration and choreography is not whether we need compensation. The difference is **who controls the business flow**.

**In choreography style, the whole flow is decentralized.** 

After S1 finishes, S1 sends a signal to S2. After S2 finishes, S2 sends a signal to S3. Each service only knows what logic it should execute after receiving a request, and if some later service tells it to roll back, it runs its own compensation logic. 

S1 does not necessarily know what happened inside S2 or S3, and it does not know the full picture of the whole business flow. It only knows: “If someone asks me to execute, I execute. If someone asks me to roll back, I compensate.”

**In orchestration style, there is a dedicated flow control application, or orchestrator.** 

This orchestrator is also an independent Spring Boot application, and it exposes RESTful endpoints to the UI. After the user clicks a button, the request first hits the orchestrator. 

Inside the service layer of the orchestrator, the full business flow has already been defined. For example, call S1 first, then call S2, then call S3. After S1 succeeds, it returns the result to the orchestrator, and the orchestrator calls S2. After S2 succeeds, the orchestrator calls S3. If S3 fails, the orchestrator will send rollback requests in reverse order, first to S2 and then to S1.

So the key difference between choreography and orchestration is this: 

**choreography means services pass signals to each other by themselves, while orchestration means a centralized orchestrator controls the whole flow.** 

But both of them belong to Saga, so neither of them uses prepare and commit. Both of them follow the idea of **executing first and compensating after failure**.

**Rollback in Saga is not an automatic database rollback.** 

It is a **compensation SQL statement** or a **compensation method** that we write ourselves. 

For example, if we created an employee earlier, the compensation logic is to delete that employee. If we created an audit log earlier, the compensation logic is to delete that audit log. 

If we sent a notification earlier, the compensation logic may be marking the notification as invalid, sending a cancellation notification, or writing a reverse status record. The exact compensation logic depends on the business requirement.

**What choreography may look like in code.** 

In the demo, uses an **event listener** to simulate real HTTP requests or a message queue. 

In reality, S1 calling S2 may be implemented with OpenFeign, RestTemplate, WebClient, or by publishing events through a message queue like Kafka. 

For example, after an employee is created, the employee service publishes an `EmployeeCreatedEvent`. 

Then the audit listener listens to this event and inserts an audit log. After the audit step finishes, it may publish another event to the notification service. This is the event-driven choreography idea.

**The orchestration code is more straightforward.** 

There is a centralized service that writes the steps in order. It calls S1 first, then S2, then S3. If anything goes wrong, it calls the compensation logic of the services that have already succeeded. 

**If something goes wrong, run the compensation logic.**

**2PC handles problems that require something closer to real-time consistency, or strong consistency.** 

It first does preparation, and then it commits. Only when all services confirm that they are ready will the coordinator allow them to commit together. So once the commit succeeds, actions such as employee saved, audit log saved, and notification sent are logically completed together.

**Saga handles eventual consistency.** 

Saga executes the first step, then the second step, then the third step. 

For example, the employee service may already think the employee has been created, and the audit service may already think the audit log has been inserted, but the notification service may not have sent the notification yet. 

At that moment, for the same “create employee” task, different services have inconsistent statuses. The employee service thinks the task is done, the audit service thinks the task is done, but the notification service thinks the task is not done yet. 

If the remaining steps eventually succeed, the system will eventually become consistent. If a later step fails, the system relies on compensation logic to undo the previous steps. This is eventual consistency.

**Different tools are suitable for different scenarios.** 

If we need strong consistency, we may consider 2PC, but we must handle the coordinator’s single point of failure problem. If we care more about availability, scalability, and service decoupling, we may consider Saga, but we must accept temporary inconsistency and carefully design compensation logic.

---

### Transition into **cloud platforms, AWS, remote servers, RDS, and high-availability architecture**.

**Cloud platforms and AWS**.

A remote server is still just a computer. The only difference is that it is not a personal computer on your desk, but a computer placed in a remote server room or data center. It also has CPU, RAM, and hard disk, and it also needs an operating system, usually Linux, and sometimes Windows Server. Like personal computers, servers also need cooling, power supply, fire protection, flood protection, disaster prevention, and maintenance.

**Large companies may build their own data centers.** 

For example, Apple may have its own data centers, with racks of servers, or even racks of Mac Mini or Mac Studio machines, to support Apple’s own cloud infrastructure, iCloud, Apple Arcade, and other services. The benefit of building private data centers is that, in the long run, it may be cheaper and highly customized for the company’s own business.

However, small and medium-sized companies usually cannot afford to build their own data centers. 

They also do not want to buy hardware, maintain hardware, manage security, handle upgrades, or build disaster recovery systems by themselves. That is why cloud platforms appeared. AWS, Azure, and GCP are basically companies that build a large number of data centers and rent computing resources, storage resources, database services, and other services to other companies. Customers do not need to care too much about the underlying hardware. They mainly pay to use the service.

**For backend developers, focusing on AWS Solutions Architect Associate.** 

Cloud Practitioner is too basic and not very helpful. 

SysOps is more for DevOps, and Developer is more about specific deployment and implementation. 

The certification that can actually help backend developers build architecture sense is Solutions Architect Associate. So for an entry-level backend developer, this certificate may be more useful for resumes and interviews.

**RDS, or Relational Database Service, as an example.** 

In the past, if we deployed a database by ourselves, we needed to buy a server, install the database, configure backups, and handle problems such as server downtime, database downtime, hacking, and data loss. 

If we wanted to increase resilience, we might need to buy three servers, deploy the database on each server, and maintain consistency between those databases ourselves. This is very complicated.

**The value of AWS RDS is that it wraps all these things into a service.** 

We pay AWS, and AWS gives us a relational database service. We can choose database engines such as PostgreSQL, Oracle, or SQL Server. 

We do not need to care too much about how the underlying CPU, RAM, and hard disk are configured. We also do not need to implement replication, high availability, failure tolerance, and scalability from scratch. 

For users, RDS is like a black box. We only need to know that it provides highly available, fault-tolerant, and scalable database capability.

**The physical foundation of cloud platforms.** 

A map of the United States; AWS may have data centers in Texas, on the East Coast, and in California. Our main database may be deployed in Texas, and a**t the same time there may be `replicas` on the East Coast and in California.** 

If the California data center goes down because of a disaster, Texas and the East Coast may still be available, so the service can continue. 

This is the underlying logic of high availability, failure tolerance, and scalability. If we pay more, we can get more regions and more replicas, even across South America, Asia, and Europe. David says very directly that the strongest shield is basically built with money.

**Do not to assume that everything is absolutely safe just because we use AWS, GCP, or Azure.** 

Cloud platforms are still maintained by humans and hardware systems, so they can also fail. He gives the example of GCP deleting data for an Australian pension fund. Even a giant like Google can have serious incidents. If the cloud platform fails and our data and services depend on only one platform, the impact can be very painful.

**So modern large-scale applications sometimes use multi-cloud or diversified infrastructure.** 

For example, Apple’s own cloud infrastructure may handle 99% of the traffic, while there are standby instances on AWS, GCP, and Azure. These standby instances may normally handle only 1% of the traffic to make sure the backup paths are alive. Once the main platform goes down, the company can quickly scale out on the backup cloud platforms and shift more traffic there. This diversified deployment can reduce platform-level risk.

As an engineer, at the beginning we may mainly write Spring Boot applications. But as we become more senior, we need to move from the perspective of a single application to the perspective of the whole platform. 

We need to think about how the system is deployed, how to achieve high availability, how to handle disaster recovery, and how to reduce risk across cloud platforms. Later, when someone becomes a manager or director, they may not write code directly anymore. Instead, they supervise, architect, and design the system.

**SUMMARY:** 

**Choreography means services pass events by themselves**

**Orchestration means an orchestrator centrally controls the flow, but both rely on compensation logic and provide eventual consistency**. 

**Compares Saga with 2PC: one focuses on eventual consistency, while the other focuses on strong consistency.** 

**Finally, he transitions to cloud platforms and explains remote servers, data centers, AWS, RDS, high availability, replication, multi-cloud backup, and why backend developers need some understanding of cloud architecture.**