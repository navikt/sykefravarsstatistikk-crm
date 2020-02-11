package no.nav.arbeidsgiver

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer

object Server {
    private const val ISALIVE = "/its-alive"
    private const val ISREADY = "/its-ready"
    fun create(ev: EnvVar = EnvVarFactory.envVar): Http4kServer {

        val salesforceTokenHandler: HttpHandler = { _ ->
            val token = Salesforce.getCachedToken().copy(accessToken = "[REDACTED]", signature = "[REDACTED]")
            Response(Status.OK).body(Utils.toJson(token).toString())
        }
        val salesforceAccountHandler: HttpHandler = { _ ->
            val data = Salesforce.getAccountById("0010E00000dtgBVQAY")
            Response(Status.OK).body(data)
        }
        val salesforceQueryHandler: HttpHandler = { _ ->
            val data = Salesforce.querySalesforce("SELECT name,Id,INT_OrganizationNumber__c from Account")
            Response(Status.OK).body(data)
        }
        val salesforceQueryTasksHandler: HttpHandler = { _ ->
            val data =
                Salesforce.querySalesforce("SELECT AccountId,Id,OwnerId,Description,CreatedDate,Subject from Task")
            Response(Status.OK).body(data)
        }
        return routes(
            ISALIVE bind Method.GET to { Response(Status.OK).body("its alive") },
            ISREADY bind Method.GET to { Response(Status.OK).body("its ready") },
            "/salesforce/token" bind Method.GET to salesforceTokenHandler,
            "/salesforce/account" bind Method.GET to salesforceAccountHandler,
            "/salesforce/query" bind Method.GET to salesforceQueryHandler,
            "/salesforce/tasks" bind Method.GET to salesforceQueryTasksHandler
        ).asServer(Netty(8087)).start()
    }
}
