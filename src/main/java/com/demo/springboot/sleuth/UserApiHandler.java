package com.demo.springboot.sleuth;

import com.demo.springboot.sleuth.exception.AppException;
import com.demo.springboot.sleuth.exception.DemoErrorAttributes;
import com.google.common.collect.MapMaker;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * TODO: Add a description
 *
 * @author Niranjan Nanda
 */
@Component
public class UserApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserApiHandler.class);
    
    public static final String CLASS_NAME = UserApiHandler.class.getCanonicalName();
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private Tracer tracer;
    
    private static final ConcurrentMap<String, User> kvStore = new MapMaker()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors()) // # of concurrent segments = available threads
            .initialCapacity(Runtime.getRuntime().availableProcessors() * 10) // Each segment will have 10 elements
            .makeMap();
    
    public UserApiHandler() {
        User user = new User();
        user.setId("U1");
        user.setFirstName("U1_First");
        user.setLastName("U1_Last");
        kvStore.put(user.getId(), user);
        
        user = new User();
        user.setId("U2");
        user.setFirstName("U2_First");
        user.setLastName("U2_Last");
        kvStore.put(user.getId(), user);
        
        user = new User();
        user.setId("U3");
        user.setFirstName("U3_First");
        user.setLastName("U3_Last");
        kvStore.put(user.getId(), user);
        
        user = new User();
        user.setId("U4");
        user.setFirstName("U4_First");
        user.setLastName("U4_Last");
        kvStore.put(user.getId(), user);
        
        user = new User();
        user.setId("U5");
        user.setFirstName("U5_First");
        user.setLastName("U5_Last");
        kvStore.put(user.getId(), user);
    }
    
    @NewSpan(name="com.demo.springboot.sleuth.UserApiHandler#getById")
    public Mono<ServerResponse> getById(final ServerRequest request) {
        final String userId = request.pathVariables().get("id");
        
        logger.info("Beans of type 'brave.Tracing': {}", applicationContext.getBeansOfType(Tracing.class));
        
        final TraceContext traceContext = Tracing.current().currentTraceContext().get();
        logger.info("[TraceId: {}][SpanId: {}]", traceContext.traceId(), traceContext.spanId());
        
        return fetchUser(userId)
                .doOnError(t -> logger.info("Exception while fetching user with id '{}'", userId, t))
                .doOnSuccess(user -> logger.info("Successfully fetched user '{}'.", user))
                .flatMap(user -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(user)).publishOn(Schedulers.newElastic("custom-thread")))
                ;
    }
    
    @NewSpan(name="com.demo.springboot.sleuth.UserApiHandler#getById2")
    public Mono<ServerResponse> getById2(final ServerRequest request) {
        return Mono.subscriberContext()
            .flatMap(context -> {
                final String userId = request.pathVariables().get("id");
                
                logger.info("[NewSpan Annotation] Beans of type 'brave.Tracing': {}", applicationContext.getBeansOfType(Tracing.class));
                
                final TraceContext traceContext = Tracing.current().currentTraceContext().get();
                logger.info("[NewSpan Annotation][TraceId: {}][SpanId: {}]", traceContext.traceId(), traceContext.spanId());
                
                return fetchUser(userId)
                        .doOnError(t -> logger.info("[NewSpan Annotation] Exception while fetching user with id '{}'", userId, t))
                        .doOnSuccess(user -> logger.info("[NewSpan Annotation] Successfully fetched user '{}'.", user))
                        .flatMap(user -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(BodyInserters.fromObject(user)).publishOn(Schedulers.newElastic("custom-thread")))
                        ;
            })
        ;
    }
    
    @NewSpan(name="com.demo.springboot.sleuth.UserApiHandler#getByIds")
    public Mono<ServerResponse> getByIds(final ServerRequest request) {
        final String allUserIds = request.queryParam("ids")
                .orElseThrow(() -> new IllegalArgumentException("Query param 'ids' is null."));
        
        return Flux.fromArray(StringUtils.split(allUserIds, ","))
            .flatMap(userId -> 
                this.fetchUser(userId)
                        .doOnError(t -> logger.info("[getByIds] Exception while fetching user with id '{}'", userId, t))
                        .doOnSuccess(user -> logger.info("[getByIds] Successfully fetched user '{}'.", user))
                        .subscribeOn(Schedulers.newElastic("getByIds-thread"))
            )
            .collect(Collectors.toList())
            .flatMap(fetchedUsers -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(fetchedUsers)))
        ;
        
    }
    
    private Mono<User> fetchUser(final String id) {
        final long spanStartTimestamp = System.currentTimeMillis();
        final Span newSpan = this.tracer.nextSpan().name("com.demo.springboot.sleuth.UserApiHandler#fetchUser");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpanInScope(newSpan.start())) {
            logger.info("[Custom New Span] fetchUser was called with id '{}'", id);
            // Simulate exception
            if (StringUtils.equals(id, "U-1")) {
                return Mono.error(new RuntimeException("Throwing RuntimeException"));
            } 
            
            if (StringUtils.equals(id, "U-100")) {
                return Mono.error(new AppException(DemoErrorAttributes.DEFAULT_ERROR_CODE, DemoErrorAttributes.DEFAULT_ERROR_MESSAGE));
            }
            
            final User user = kvStore.get(id);
            if (user == null) {
                return Mono.error(new AppException("APP-404001", "User with ID '" + id + "' was not found." ));
            }
            
            return Mono.just(user)
                    .doOnNext(u -> logger.info("[Custom New Span][#fetchUser] Fetched a user --> {}", u));
        } finally {
            newSpan.finish(TimeUnit.MICROSECONDS.convert(spanStartTimestamp, TimeUnit.MILLISECONDS));
        }
    }
}
