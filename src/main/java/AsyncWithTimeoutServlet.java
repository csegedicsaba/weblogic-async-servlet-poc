import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "async-with-timeout", urlPatterns = { "/async-with-timeout" }, asyncSupported = true)
public class AsyncWithTimeoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        final AsyncContext asyncContext = req.startAsync(req, res);
        asyncContext.setTimeout(1000);
/**
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                // System.out.println("On complete");
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("On timeout");
                //asyncContext.complete();
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                System.out.println("On error");
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                // System.out.println("On start async");
            }
        });
**/
        new Thread(() -> {
            try {
                String cscsIdFromHeader = req.getHeader("cscs-id");
                // System.out.println("cscsIdFromHeader: " + cscsIdFromHeader);
                String sleep = req.getHeader("sleep");
                // System.out.println("sleep: " + sleep);

                StringBuilder sb = new StringBuilder();

                BufferedReader bufferedReader = req.getReader();
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                    sb.append(charBuffer, 0, bytesRead);
                }

                String requestBody = sb.toString();

                if (!requestBody.equals(cscsIdFromHeader)) {
                    System.out.println(requestBody + " <> " + cscsIdFromHeader);
                }

                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();

                Random rn = new Random();
                Thread.sleep(Integer.parseInt(sleep) + rn.nextInt(400));

                response.addHeader("cscs-id", cscsIdFromHeader);

                out.print("cscs-" + requestBody);
                out.flush();
                asyncContext.complete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }
}
