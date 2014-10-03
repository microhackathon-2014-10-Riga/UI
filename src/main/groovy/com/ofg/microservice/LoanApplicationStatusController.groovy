package com.ofg.microservice
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.web.bind.annotation.RequestMethod.GET

@Slf4j
@TypeChecked
@RestController
@RequestMapping('/application')
class LoanApplicationStatusController {
    private final ServiceRestClient serviceRestClient

    @Autowired
    LoanApplicationStatusController(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient
    }

    @RequestMapping(method = GET, value = "/{loanId}")
    String checkStatus(@PathVariable("loanId") String loanId) {
        log.debug("Checking status of: $loanId")
        Boolean decisionAboutTheLoan = serviceRestClient.forService("loan-application-decision-maker").
                get().
                onUrl("/api/loanApplication/$loanId").
                andExecuteFor().
                aResponseEntity().
                ofType(Decision).
                body.result

        log.debug("DECISION ABOUT THE LOAN: $loanId is: $decisionAboutTheLoan")


        return new Status(decisionAboutTheLoan, null)
    }



}


@TypeChecked
@Canonical
class Status {
    Boolean decisionAboutTheLoan
    String offers
}

class Decision {
    String applicationId
    Long id
    Boolean result
}

