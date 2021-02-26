BoundedThreadpoolExecutor implemented using Semaphore. Java executor service throws RejectedExecutionException when the task queue becomes full. 
Using unbounded queue may result in out of memory error. 
This can be avoided by controlling the number of tasks being submitted using executor service. 
This can be done by using semaphore or by implementing RejectedExecutionHandler.  
