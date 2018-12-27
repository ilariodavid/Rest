package org.me;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ilario.david
 */
@WebServlet(name = "Notes", urlPatterns =
{
    "/notes/*"
})
public class Notes extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    class Note
    {

        private String id;
        private String title;
        private String body;

        public Note(String id, String title, String body)
        {
            this.id = id;
            this.title = title;
            this.body = body;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getBody()
        {
            return body;
        }

        public void setBody(String body)
        {
            this.body = body;
        }

    }

    private Gson gson = null;

    private final HashMap<String, Note> notesDb = new HashMap<>();

    public Notes()
    {
        super();

        gson = new Gson();

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        String id3 = UUID.randomUUID().toString();

        notesDb.put(id1,
                new Note(
                        id1,
                        "Title 1 ",
                        "Nota aleatória"));

        notesDb.put(id2,
                new Note(
                        id2,
                        "Title 2",
                        "Nota aleatória"));

        notesDb.put(id3,
                new Note(
                        id3,
                        "Title 3",
                        "Nota aleatória"));
    }

    private void sendAsJson(
            HttpServletResponse response,
            Object obj) throws IOException
    {

        response.setContentType("application/json");

        String res = gson.toJson(obj);

        PrintWriter out = response.getWriter();

        out.print(res);
        out.flush();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/"))
        {

            Collection<Note> models = notesDb.values();

            sendAsJson(response, models);
            return;
        }

        String[] splits = pathInfo.split("/");

        if (splits.length != 2)
        {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String modelId = splits[1];

        if (!notesDb.containsKey(modelId))
        {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        sendAsJson(response, notesDb.get(modelId));
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/"))
        {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }

            String payload = buffer.toString();

            Note model = gson.fromJson(payload, Note.class);

            model.id = UUID.randomUUID().toString();

            notesDb.put(model.id, model);

            sendAsJson(response, model);
        } else
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException
    {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/"))
        {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] splits = pathInfo.split("/");

        if (splits.length != 2)
        {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String modelId = splits[1];

        if (!notesDb.containsKey(modelId))
        {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null)
        {
            buffer.append(line);
        }

        String payload = buffer.toString();

        Note model = gson.fromJson(payload, Note.class);

        model.id = modelId;

        notesDb.put(modelId, model);

        sendAsJson(response, model);
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException
    {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/"))
        {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] splits = pathInfo.split("/");

        if (splits.length != 2)
        {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String modelId = splits[1];

        if (!notesDb.containsKey(modelId))
        {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Note model = notesDb.get(modelId);

        notesDb.remove(modelId);

        sendAsJson(response, model);
    }
}
