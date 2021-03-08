package servlets;
import Service.CustomUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pojo.Person;
import sun.misc.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@WebServlet("/demo")
public class TestServlet  extends HttpServlet {
    private List<Person> personList;

    @Override
    public void init() {
        personList = CustomUtils.createDummyList();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        Gson gson = new GsonBuilder().create();
        if (id != null && !id.equals("")){
            Person person = getPersonById(Integer.parseInt(id));
            if (person == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            resp.setContentType("application/json");

            PrintWriter out = resp.getWriter();
            out.println(gson.toJson(person));
            out.close();
        }else {
            resp.setContentType("application/json");

            PrintWriter out = resp.getWriter();
            out.println(gson.toJson(personList));
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String name = req.getParameter("name");
//        String id = req.getParameter("id");
//
        String requestData= IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
        if (name != null && id != null){
            this.personList.add(new Person(name,Integer.parseInt(id)));

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

            PrintWriter out = resp.getWriter();
            out.println("Person "+ name +" is added");
            out.close();
        } else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.println("Please try again later!" + req.getParameterNames());
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null && !id.equals("")){
            String status = deleteById(Integer.parseInt(id), resp);
            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        }else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.println("Please try again later!");
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String id = req.getParameter("id");

        if (name != null  && id != null){
            String status = updateById(Integer.parseInt(id), name, resp);
            PrintWriter out = resp.getWriter();
            out.println(status);
            out.close();
        }else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = resp.getWriter();
            out.println("Please try again later!");
            out.close();
        }
    }

    @Override
    public void destroy() {
        this.personList.clear();
        System.out.println("In DESTROY Method");
    }

    private Person getPersonById(int id){
        for (Person person : personList){
            if (person.getId() == id){
                return person;
            }
        }
        return null;
    }

    private String deleteById(int id, HttpServletResponse response){
        for (Person person : personList){
            if (person.getId() == id){
                personList.remove(person);
                return "Person Deleted";
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "Person id not found";
    }

    private String updateById(int id, String name, HttpServletResponse response){
        for (Person person : personList){
            if (person.getId() == id){
                person.setName(name);
                return "Person name changed successfully";
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "Person id not found";
    }
}
