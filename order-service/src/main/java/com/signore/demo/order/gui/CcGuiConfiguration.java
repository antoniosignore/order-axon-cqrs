package com.signore.demo.order.gui;

import com.signore.demo.order.api.CountOrderSummariesQuery;
import com.signore.demo.order.api.CountOrderSummariesResponse;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("gui")
public class CcGuiConfiguration {

	@EventListener(ApplicationReadyEvent.class)
	public void helloHub(ApplicationReadyEvent event) {
		QueryGateway queryGateway = event.getApplicationContext().getBean(QueryGateway.class);
		queryGateway.query(new CountOrderSummariesQuery(),
				ResponseTypes.instanceOf(CountOrderSummariesResponse.class));
	}

}
