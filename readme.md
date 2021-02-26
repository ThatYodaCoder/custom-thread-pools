1. BoundedThreadpoolExecutor implemented using Semaphore. Java executor service throws RejectedExecutionException when the task queue becomes full. 
Using unbounded queue may result in out of memory error. 
This can be avoided by controlling the number of tasks being submitted using executor service. 
This can be done by using semaphore or by implementing RejectedExecutionHandler.  

2. StripedExecutorService: The purpose of this executor service is to distribute the tasks in round robin fashion. 
This will help us to avoid customer starvation which can/may happen in case of single thread pool
