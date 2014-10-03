package com.ofg.microservice

import com.codahale.metrics.Counter
import com.codahale.metrics.MetricRegistry
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.constraints.NotNull

import static org.springframework.web.bind.annotation.RequestMethod.GET

@Slf4j
@RestController
@RequestMapping('/application')
@CompileStatic
class LoanApplicationStatusController {
    private final ServiceRestClient serviceRestClient
    private final Counter counter

    @Autowired
    LoanApplicationStatusController(ServiceRestClient serviceRestClient, MetricRegistry metricRegistry) {
        this.serviceRestClient = serviceRestClient
        counter = metricRegistry.counter('application.status')
    }

    @RequestMapping(method = GET, value = "/{loanId}/{clientId}")
    Status checkStatus(@NotNull @PathVariable("loanId") String loanId,
                       @NotNull @PathVariable("clientId") String clientId) {
        counter.inc()
        log.debug("Checking status of: $loanId and $clientId")
        boolean decisionAboutTheLoan = loadLoanDecision(loanId)
        String marketingOffer = loadOffers(clientId)
        return new Status(decisionAboutTheLoan, marketingOffer)
    }

    private boolean loadLoanDecision(String loanId) {
        final boolean decisionAboutTheLoan = serviceRestClient.forService("loan-application-decision-maker").
                get().
                onUrl("/api/loanApplication/$loanId").
                andExecuteFor().
                aResponseEntity().
                ofType(Decision).
                body.result

        log.debug("DECISION ABOUT THE LOAN: $loanId is: $decisionAboutTheLoan")
        return decisionAboutTheLoan
    }

    private String loadOffers(String clientId) {
        final String marketingOffer = loadGracefully(clientId)
        log.debug("Marketing offer: $clientId is: $marketingOffer")
        return marketingOffer
    }

    private String loadGracefully(String clientId) {
        try {
            return serviceRestClient.forService("marketing-offer-generator").
                    get().
                    onUrl("/api/marketing/$clientId").
                    andExecuteFor().
                    aResponseEntity().
                    ofType(MarketingOffer).
                    body.marketingOffer
        } catch (Exception e) {
            log.warn("Error fetching marketing offers", e)
            return "Failure to load"
        }
    }


}


@TypeChecked
@Canonical
class Status {
    boolean decisionAboutTheLoan
    String offers
}

class Decision {
    String applicationId
    Long id
    Boolean result
}

class MarketingOffer {
    String marketingOffer;
}