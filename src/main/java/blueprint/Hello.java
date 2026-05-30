package blueprint;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easygoingapi.yoja.core.http.HttpMethod;
import com.easygoingapi.yoja.http.server.HttpRouter;
import com.easygoingapi.yoja.http.server.HttpServer;
import com.easygoingapi.yoja.http.server.WebApp;

public class Hello {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Hello.class);
    
    public static void main(String[] args) {
        final String folderWebapp;
        if (args.length == 0) {
            folderWebapp = Path.of("webapp/blueprint").toAbsolutePath().toString();
        }
        else {
            folderWebapp = args[0];
        }
        
        HttpRouter router = 
            HttpRouter.builder()
                      .contentType("js", "application/javascript")
                      .contentType("html", "text/html")
                      .webResource(WebApp.folder(folderWebapp), "/*")
                      .webService(HttpMethod.GET, "/hello", r -> r.response().send("hello, yoja"))
                      .build();
        
        HttpServer.builder(router, 8080)
                  .start()
                  .onSuccess(server -> {
                      LOGGER.info("folder webapp: {}", folderWebapp);
                      LOGGER.info("blueprint Hello — http://localhost:{}/index.html", server.port());
                  })
                  .onFailure(Throwable::printStackTrace);
    }
    
}