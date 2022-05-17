package me.aki.demo.camunda.controller;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class Index {
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("word", "麻了");
        return "index";
    }

    @RequestMapping("/task")
    public String taskList() {
        return "task";
    }

    @RequestMapping("/inst")
    public String instanceList() {
        return "inst";
    }
}
