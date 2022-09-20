package me.aki.demo.camunda.util;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;

import java.util.List;

public class BpmnUtil {
    private BpmnUtil() {
    }

    public static List<DomElement> getElementByName(BpmnModelInstance model, String name) {
        String ns = model.getDefinitions().getTargetNamespace();
        DomDocument document = model.getDocument();
        return document.getElementsByNameNs(ns, name);
    }

    public static String toVarExp(String varName) {
        return String.format("${%s}", varName);
    }

    public static String toXmlStr(BpmnModelInstance model) {
        return Bpmn.convertToString(model);
    }
}
