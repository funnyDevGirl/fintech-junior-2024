package org.tbank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tbank.annotation.LogExecutionTime;


@Aspect
@Component
public class ExecutionTimeLogger {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeLogger.class);

    @Around("@within(logExecutionTime) || @annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {

        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("Метод {} класса {} выполнен за {} мс", methodName, className, executionTime);

        return proceed;
    }
}
