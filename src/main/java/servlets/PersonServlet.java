package servlets;

import Service.CustomUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import pojo.Person;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/persons")
public class PersonServlet extends HttpServlet {
    private List<Person> personList = new ArrayList<>();

    @Override
    public void init() {
        System.out.println("In INIT Method");
        personList = CustomUtils.createDummyList();
//        ServletConfig config = getServletConfig();
//        System.out.println(config.getInitParameter("driver"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        Gson gson = new GsonBuilder().create();
        String accept = req.getHeader("accept");
        if (id != null && !id.equals("")){
            Person person = getPersonById(Integer.parseInt(id));
            if (person == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                XStream xStream = new XStream();
                xStream.alias("person", Person.class);
                String xml = xStream.toXML(person);
                OutputStream out = resp.getOutputStream();
                out.write(xml.getBytes());
                out.flush();
            } else {
                //resp.setContentType("application/json");
                resp.setHeader("ContentType", "application/json");
                PrintWriter out = resp.getWriter();
                out.write(gson.toJson(person));
                out.close();
            }
        }else {
            if (accept.equals("application/xml")){
                resp.setContentType("application/xml");
                OutputStream out = resp.getOutputStream();
                XStream xStream = new XStream();
                xStream.alias("person", Person.class);
                StringBuilder xml = new StringBuilder();
                for (Person person : personList){
                    xml.append(xStream.toXML(person));
                }
                out.write(xml.toString().getBytes());
                out.flush();
            } else {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.println(gson.toJson(personList));
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String id = req.getParameter("id");

        if (name != null && id != null){
            String status = addUser(Integer.parseInt(id), name, resp);

            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        } else{
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.write("Please try again later!");
            out.close();
        }
    }

    private String addUser(int id, String name, HttpServletResponse response){
        for (Person person : personList){
            if (person.getId() == id){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return "Person with id " + id + " already exists.";
            }
        }
        this.personList.add(new Person(name, id));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "Person successfully added.";
    }

    private Person getPersonById(int id){
        for (Person person : personList){
            if (person.getId() == id){
                return person;
            }
        }
        return null;
    }

}
