package com.jetbrains.handson.website

import com.jetbrains.handson.website.data.BlogEntry
import com.jetbrains.handson.website.data.Datas
import freemarker.cache.*
import freemarker.core.HTMLOutputFormat
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker){
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
    }
    registerJournalRoutes()
    registerStaticRoutes()
}


fun Application.registerJournalRoutes(){
    routing {
        get("/"){
            call.respond(
                FreeMarkerContent("index.ftl", mapOf("entries" to Datas.entries))
            )
        }
        post("/submit"){
            val params = call.receiveParameters()
            val headline = params["headline"] ?: return@post call.respondText(
                "Expecting for headline",
                status = HttpStatusCode.BadRequest,
            )
            val body = params["body"] ?: return@post call.respondText(
                "Expecting for body",
                status = HttpStatusCode.BadRequest,
            )
            val newEntry = BlogEntry(headline, body)
            Datas.entries.add(0, newEntry)
            call.respondHtml {
                body {
                    h1 {
                        +"Thank fo your entry!"
                    }
                    p {
                        +"The submitted entri headline:"
                        b { +newEntry.headline }
                    }
                    p {
                      +"Current entry count: ${Datas.entries.size}"
                    }
                    a("/"){ +"Go back" }
                }
            }
        }
    }
}

fun Application.registerStaticRoutes(){
    routing {
        static("/sitatic") {
            resources("files")
        }
    }
}
