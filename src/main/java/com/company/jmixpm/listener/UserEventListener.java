package com.company.jmixpm.listener;

import com.company.jmixpm.entity.User;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntityLoadingEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserEventListener {
    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    @EventListener
    public void onUserLoading(EntityLoadingEvent<User> event) {
        log.debug("Loading: " + event.getEntity().getId());
    }

    @EventListener
    public void onUserSaving(EntitySavingEvent<User> event) {
        log.debug("Saving: " + event.getEntity().getId());
    }

    @EventListener
    public void onUserChangedBeforeCommit(EntityChangedEvent<User> event) {
        log.debug("Before Commit: " + event.getEntityId());
    }

    @TransactionalEventListener
    public void onUserChangedAfterCommit(EntityChangedEvent<User> event) {
        log.debug("After Commit: " + event.getEntityId());
    }
}